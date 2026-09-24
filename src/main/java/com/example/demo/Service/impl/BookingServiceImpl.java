package com.example.demo.Service.impl;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.Entity.BookingEntity;
import com.example.demo.Entity.BookingServiceEntity;
import com.example.demo.Entity.CourtsEntity;
import com.example.demo.Entity.PriceConfigEntity;
import com.example.demo.Entity.ServiceEntity;
import com.example.demo.Entity.UsersEntity;
import com.example.demo.Repository.BookingRepository;
import com.example.demo.Repository.BookingServiceRepository;
import com.example.demo.Repository.CourtRepository;
import com.example.demo.Repository.PriceConfigRepository;
import com.example.demo.Repository.ServiceRepository;
import com.example.demo.Repository.UserRepository;
import com.example.demo.Service.BookingService;
import com.example.demo.dto.BookingDTO;
import com.example.demo.dto.BookingDetailDTO;
import com.example.demo.dto.BookingListDTO;
import com.example.demo.dto.BookingServiceDTO;
import com.example.demo.dto.CustomUserDetails;

@Service
public class BookingServiceImpl implements BookingService {

	@Autowired
	private BookingRepository bookingRepository;
	
	@Autowired
	private CourtRepository courtrepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ServiceRepository serviceRepository;
	
	@Autowired
	private PriceConfigRepository priceConfigRepository;
	
	@Autowired
	private BookingServiceRepository bookingServiceRepository;

	@Override
	@Transactional
	public void Booking(Long id, BookingDTO bookingDTO,CustomUserDetails userDetails) {
		// 1. Kiểm tra trùng lịch của sân dựa vào ID từ URL
		boolean isOverlapped = bookingRepository.existsByCourtIdAndStatusNotAndStartTimeLessThanAndEndTimeGreaterThan(
				id, "Cancelled", bookingDTO.getEndTime(), bookingDTO.getStartTime()
		);
		if (isOverlapped) {
			throw new RuntimeException("Sân đã có người đặt trong khung giờ này rồi anh ơi!");
		}

		// 2. Tìm Sân và Người dùng tương ứng
		CourtsEntity court = courtrepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Không thấy sân"));
		UsersEntity user = userRepository.findById(userDetails.getUser().getId())
				.orElseThrow(() -> new RuntimeException("Không thấy người dùng"));

		// 3. Tính số giờ thuê sân
		long minutes = Duration.between(bookingDTO.getStartTime(), bookingDTO.getEndTime()).toMinutes();
		double hours = minutes / 60.0;

		LocalDateTime startTime = bookingDTO.getStartTime();
		DayOfWeek day = startTime.getDayOfWeek();
		String dayOfWeek;
		if (day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY) {
			dayOfWeek = "Cuối tuần";
		} else {
			dayOfWeek = "Ngày thường";
		}

		LocalTime bookingTime = startTime.toLocalTime();
		double pricePerHour = 0;

		// 4. Tìm cấu hình giá sân phù hợp
		List<PriceConfigEntity> price = priceConfigRepository.findByCourtsId(id);
		for (PriceConfigEntity item : price) {
			LocalTime configStart = item.getStartTime().toLocalTime();
			LocalTime configEnd = item.getEndTime().toLocalTime();
			if ((item.getDayOfWeek().equals(dayOfWeek)) && !bookingTime.isBefore(configStart) && bookingTime.isBefore(configEnd)) {
				pricePerHour = item.getPricePerHour();
				break;
			}
		}

		// Tính tiền sân gốc
		double courtTotal = hours * pricePerHour;
		double finalTotal = courtTotal;

		// 5. Tạo đối tượng Booking tổng và lưu trước để sinh ID hóa đơn
		BookingEntity booking = new BookingEntity();
		booking.setCourt(court);
		booking.setStartTime(bookingDTO.getStartTime());
		booking.setEndTime(bookingDTO.getEndTime());
		booking.setUser(user);
		booking.setStatus("PENDING");
		booking.setTotal(finalTotal);

		BookingEntity savedBooking = bookingRepository.save(booking);

		// 6. Xử lý danh sách dịch vụ đi kèm 
		if (bookingDTO.getBookingService() != null && !bookingDTO.getBookingService().isEmpty()) {
			for (BookingServiceDTO bServiceDTO : bookingDTO.getBookingService()) {
				
				// Tìm dịch vụ trong DB dựa theo thuộc tính id của BookingServiceDTO
				ServiceEntity serviceEntity = serviceRepository.findById(bServiceDTO.getServiceId())
						.orElseThrow(() -> new RuntimeException("Không tìm thấy dịch vụ đi kèm!"));

				BookingServiceEntity item = new BookingServiceEntity();
				item.setQuantity(bServiceDTO.getQuantity());
				
				// Set đơn giá tại thời điểm đặt
				item.setUnitPrice(serviceEntity.getPrice());
				
				// Tính tổng tiền riêng cho loại dịch vụ này
				double serviceTotal = bServiceDTO.getQuantity() * serviceEntity.getPrice();
				item.setTotal(serviceTotal);

				// Gán các mối quan hệ thực thể
				item.setBooking(savedBooking);
				item.setService(serviceEntity);

				// Lưu vào bảng trung gian booking_service
				bookingServiceRepository.save(item);

				// Cộng dồn tiền dịch vụ vào tổng hóa đơn chung
				finalTotal += serviceTotal;
			}
			// 7. Cập nhật lại tổng tiền cuối cùng (Tiền sân + Tiền các dịch vụ) vào bản ghi Booking
			savedBooking.setTotal(finalTotal);
			bookingRepository.save(savedBooking);
		}
	}
	@Override
	public List<BookingListDTO> getAllBooking(CustomUserDetails userDetails){
		List<BookingEntity> getList =bookingRepository.findByUserId(userDetails.getUser().getId());
		List<BookingListDTO> listDTO =new ArrayList<>();
		for(BookingEntity item : getList) {
			BookingListDTO dto=new BookingListDTO();
			dto.setId(item.getId());
			dto.setCourtName(item.getCourt().getName());
			dto.setCustomerName(item.getUser().getFullName());
			dto.setEndTime(item.getEndTime());
			dto.setStartTime(item.getStartTime());
			dto.setStatus(item.getStatus());
			dto.setTotal(item.getTotal());
			listDTO.add(dto);
		}
		return listDTO;
	}
	@Override
	public BookingDetailDTO getDetail(Long id) {
		BookingEntity bookingEntity =bookingRepository.findById(id).orElseThrow(()->new RuntimeException("Khong tim thay don dat san!"));
		BookingDetailDTO detailDto=new BookingDetailDTO();
		detailDto.setCustomerName(bookingEntity.getUser().getFullName());
		detailDto.setCourtName(bookingEntity.getCourt().getName());
		detailDto.setAddress(bookingEntity.getCourt().getAddressDetail());
		detailDto.setStartTime(bookingEntity.getStartTime());
		detailDto.setEndTime(bookingEntity.getEndTime());
		List<BookingServiceDTO> dtos = new ArrayList<>();
		if(bookingEntity.getBookingService()!=null){
			for(BookingServiceEntity item:bookingEntity.getBookingService()) {
				BookingServiceDTO i = new BookingServiceDTO();
				ServiceEntity service =serviceRepository.findById(item.getService().getId()).get();
				i.setServiceName(service.getName());
				i.setQuantity(item.getQuantity());
				i.setUnitPrice(item.getUnitPrice());
				i.setTotal(item.getTotal());
				dtos.add(i);
			}
			detailDto.setBookingService(dtos);
		}
		detailDto.setTotal(bookingEntity.getTotal());
		detailDto.setStatus(bookingEntity.getStatus());
		return detailDto;
	}
	@Override
	public List<BookingDTO> getBooking(Long id) {
		List<BookingEntity> entity =bookingRepository.findByCourtOwnerId(id);
		List<BookingDTO> dtos= new ArrayList<>();
		for(BookingEntity i :entity) {
			BookingDTO dto=new BookingDTO();
			dto.setCourtName(i.getCourt().getName());
			dto.setCustomerName(i.getUser().getFullName());
			dto.setStartTime(i.getStartTime());
			dto.setEndTime(i.getEndTime());
			dto.setTotal(i.getTotal());
			dto.setStatus(i.getStatus());
			dto.setId(i.getId());
			dto.setCourtId(i.getCourt().getId());
			dtos.add(dto);
		}
		return dtos;
	}
	@Override
	@Transactional
	public void approveBooking(long id) {
		BookingEntity booking=bookingRepository.findById(id).orElseThrow(()->new RuntimeException("Không thể tìm thấy đơn đặt sân!"));
		if(!"PENDING".equalsIgnoreCase(booking.getStatus())) {
			throw new RuntimeException("Chỉ có thể duyệt đơn ở trạng thái PENDING!");
		}
		booking.setStatus("CONFIRMED");
		bookingRepository.save(booking);
	}
	@Override
	@Transactional
	public void cancelBooking(Long id) {
		BookingEntity booking = bookingRepository.findById(id).orElseThrow(()->new RuntimeException("Không thể tìm thấy đơn đặt sân!"));
		if("CONFIRMED".equalsIgnoreCase(booking.getStatus())) {
			throw new RuntimeException("Không thể huỷ đơn đã xác nhận!");
		}
		if("CANCELLED".equalsIgnoreCase(booking.getStatus())) {
			throw new RuntimeException("Đơn này đã được huỷ từ trước!");
		}
		booking.setStatus("CANCEL");
		bookingRepository.save(booking);
	}
	public List<BookingDTO> getBookingCustomer(Long id){
		List<BookingEntity> entity =bookingRepository.findByUserId(id);
		List<BookingDTO> dtos= new ArrayList<>();
		for(BookingEntity i :entity) {
			BookingDTO dto=new BookingDTO();
			dto.setCourtName(i.getCourt().getName());
			dto.setCustomerName(i.getUser().getFullName());
			dto.setStartTime(i.getStartTime());
			dto.setEndTime(i.getEndTime());
			dto.setTotal(i.getTotal());
			dto.setStatus(i.getStatus());
			dto.setId(i.getId());
			dto.setCourtId(i.getCourt().getId());
			dtos.add(dto);
		}
		return dtos;
	}
}