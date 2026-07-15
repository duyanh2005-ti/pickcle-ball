package com.example.demo.Service.impl;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
import com.example.demo.dto.BookingServiceDTO;

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
	public void Booking(Long id, BookingDTO bookingDTO) {
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
		UsersEntity user = userRepository.findById(bookingDTO.getUserId())
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
}