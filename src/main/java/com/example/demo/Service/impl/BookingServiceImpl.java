package com.example.demo.Service.impl;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
import com.example.demo.enums.BookingStatus;
import com.example.demo.exception.BadRequestException;
import com.example.demo.exception.ConflictException;
import com.example.demo.exception.ForbiddenException;
import com.example.demo.exception.NotFoundException;

@Service
public class BookingServiceImpl implements BookingService {

	private static final String WEEKEND = "Cuối tuần";
	private static final String WEEKDAY = "Ngày thường";

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
	public void Booking(Long id, BookingDTO bookingDTO, CustomUserDetails userDetails) {
		LocalDateTime start = bookingDTO.getStartTime();
		LocalDateTime end = bookingDTO.getEndTime();

		// 1. Kiểm tra dữ liệu thời gian
		if (start == null || end == null) {
			throw new BadRequestException("Vui lòng chọn giờ bắt đầu và giờ kết thúc!");
		}
		if (!end.isAfter(start)) {
			throw new BadRequestException("Giờ kết thúc phải sau giờ bắt đầu!");
		}
		if (!start.isAfter(LocalDateTime.now())) {
			throw new BadRequestException("Không thể đặt sân cho thời điểm đã qua!");
		}

		// 2. Khoá dòng sân để các yêu cầu đặt cùng sân chạy lần lượt (chống đặt trùng)
		CourtsEntity court = courtrepository.findByIdForUpdate(id)
				.orElseThrow(() -> new NotFoundException("Không tìm thấy sân!"));

		// 3. Kiểm tra trùng lịch (bỏ qua các đơn đã huỷ)
		if (bookingRepository.existsOverlap(id, BookingStatus.CANCELLED, start, end)) {
			throw new ConflictException("Sân đã có người đặt trong khung giờ này!");
		}

		UsersEntity user = userRepository.findById(userDetails.getUser().getId())
				.orElseThrow(() -> new NotFoundException("Không tìm thấy người dùng!"));

		// 4. Tính tiền sân theo từng khung giá mà khoảng đặt đi qua
		List<PriceConfigEntity> priceConfigs = priceConfigRepository.findByCourtsId(id);
		double finalTotal = calculateCourtPrice(priceConfigs, start, end);

		// 5. Lưu đơn đặt
		BookingEntity booking = new BookingEntity();
		booking.setCourt(court);
		booking.setStartTime(start);
		booking.setEndTime(end);
		booking.setUser(user);
		booking.setStatus(BookingStatus.PENDING);
		booking.setTotal(finalTotal);
		BookingEntity savedBooking = bookingRepository.save(booking);

		// 6. Dịch vụ đi kèm (lưu lại đơn giá tại thời điểm đặt)
		if (bookingDTO.getBookingService() != null) {
			for (BookingServiceDTO bServiceDTO : bookingDTO.getBookingService()) {
				if (bServiceDTO.getServiceId() == null) {
					throw new BadRequestException("Thiếu mã dịch vụ đi kèm!");
				}
				Integer quantity = bServiceDTO.getQuantity();
				if (quantity == null || quantity <= 0) {
					throw new BadRequestException("Số lượng dịch vụ phải lớn hơn 0!");
				}
				ServiceEntity serviceEntity = serviceRepository.findById(bServiceDTO.getServiceId())
						.orElseThrow(() -> new NotFoundException("Không tìm thấy dịch vụ đi kèm!"));
				double unitPrice = serviceEntity.getPrice() == null ? 0 : serviceEntity.getPrice();
				double serviceTotal = quantity * unitPrice;

				BookingServiceEntity item = new BookingServiceEntity();
				item.setQuantity(quantity);
				item.setUnitPrice(unitPrice);
				item.setTotal(serviceTotal);
				item.setBooking(savedBooking);
				item.setService(serviceEntity);
				bookingServiceRepository.save(item);

				finalTotal += serviceTotal;
			}
			savedBooking.setTotal(finalTotal);
			bookingRepository.save(savedBooking);
		}
	}

	/**
	 * Chia khoảng [start, end) theo các khung giá của sân và cộng tiền từng đoạn.
	 * Nếu có đoạn nào không nằm trong khung giá nào thì từ chối đặt (không cho giá 0đ).
	 */
	private double calculateCourtPrice(List<PriceConfigEntity> configs, LocalDateTime start, LocalDateTime end) {
		double total = 0;
		LocalDateTime cursor = start;
		while (cursor.isBefore(end)) {
			PriceConfigEntity config = findPriceConfig(configs, cursor);
			if (config == null) {
				throw new BadRequestException("Sân chưa có bảng giá cho khung giờ "
						+ cursor.toLocalTime() + " (" + dayType(cursor.getDayOfWeek()) + "), không thể đặt!");
			}
			LocalDateTime slotEnd = slotEnd(cursor.toLocalDate(), config);
			LocalDateTime segmentEnd = end.isBefore(slotEnd) ? end : slotEnd;
			double hours = Duration.between(cursor, segmentEnd).toSeconds() / 3600.0;
			total += hours * config.getPricePerHour();
			cursor = segmentEnd;
		}
		return total;
	}

	private PriceConfigEntity findPriceConfig(List<PriceConfigEntity> configs, LocalDateTime at) {
		String day = dayType(at.getDayOfWeek());
		LocalTime time = at.toLocalTime();
		for (PriceConfigEntity item : configs) {
			if (item.getDayOfWeek() == null || item.getStartTime() == null || item.getEndTime() == null
					|| item.getPricePerHour() == null) {
				continue;
			}
			if (!day.equalsIgnoreCase(item.getDayOfWeek().trim())) {
				continue;
			}
			LocalTime configStart = item.getStartTime().toLocalTime();
			LocalTime configEnd = item.getEndTime().toLocalTime();
			boolean untilMidnight = !configEnd.isAfter(configStart); // ví dụ 18:00 - 00:00
			if (!time.isBefore(configStart) && (untilMidnight || time.isBefore(configEnd))) {
				return item;
			}
		}
		return null;
	}

	private LocalDateTime slotEnd(LocalDate date, PriceConfigEntity config) {
		LocalTime configStart = config.getStartTime().toLocalTime();
		LocalTime configEnd = config.getEndTime().toLocalTime();
		if (!configEnd.isAfter(configStart)) {
			return date.plusDays(1).atStartOfDay();
		}
		return date.atTime(configEnd);
	}

	private String dayType(DayOfWeek day) {
		return (day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY) ? WEEKEND : WEEKDAY;
	}

	// ===== Kiểm tra quyền =====

	private boolean isAdmin(CustomUserDetails user) {
		return user != null && "ADMIN".equalsIgnoreCase(user.getUser().getRole());
	}

	private boolean isCourtOwner(CustomUserDetails user, BookingEntity booking) {
		UsersEntity owner = booking.getCourt() == null ? null : booking.getCourt().getOwner();
		return user != null && owner != null && Objects.equals(owner.getId(), user.getUser().getId());
	}

	private boolean isBookingCustomer(CustomUserDetails user, BookingEntity booking) {
		return user != null && booking.getUser() != null
				&& Objects.equals(booking.getUser().getId(), user.getUser().getId());
	}

	private BookingEntity findBooking(Long id) {
		return bookingRepository.findById(id)
				.orElseThrow(() -> new NotFoundException("Không tìm thấy đơn đặt sân!"));
	}

	private static String statusName(BookingStatus status) {
		return status == null ? null : status.name();
	}

	// ===== Truy vấn =====

	@Override
	@Transactional(readOnly = true)
	public List<BookingListDTO> getAllBooking(CustomUserDetails userDetails){
		List<BookingEntity> getList = bookingRepository.findByUserId(userDetails.getUser().getId());
		List<BookingListDTO> listDTO = new ArrayList<>();
		for (BookingEntity item : getList) {
			BookingListDTO dto = new BookingListDTO();
			dto.setId(item.getId());
			dto.setCourtName(item.getCourt().getName());
			dto.setCustomerName(item.getUser().getFullName());
			dto.setEndTime(item.getEndTime());
			dto.setStartTime(item.getStartTime());
			dto.setStatus(statusName(item.getStatus()));
			dto.setTotal(item.getTotal());
			listDTO.add(dto);
		}
		return listDTO;
	}

	@Override
	@Transactional(readOnly = true)
	public BookingDetailDTO getDetail(Long id, CustomUserDetails currentUser) {
		BookingEntity bookingEntity = findBooking(id);
		// Chỉ khách đặt, chủ sân hoặc admin được xem chi tiết
		if (!isBookingCustomer(currentUser, bookingEntity) && !isCourtOwner(currentUser, bookingEntity)
				&& !isAdmin(currentUser)) {
			throw new ForbiddenException("Bạn không có quyền xem đơn đặt sân này!");
		}
		BookingDetailDTO detailDto = new BookingDetailDTO();
		detailDto.setCustomerName(bookingEntity.getUser().getFullName());
		detailDto.setCourtName(bookingEntity.getCourt().getName());
		detailDto.setAddress(bookingEntity.getCourt().getAddressDetail());
		detailDto.setStartTime(bookingEntity.getStartTime());
		detailDto.setEndTime(bookingEntity.getEndTime());
		List<BookingServiceDTO> dtos = new ArrayList<>();
		if (bookingEntity.getBookingService() != null) {
			for (BookingServiceEntity item : bookingEntity.getBookingService()) {
				BookingServiceDTO i = new BookingServiceDTO();
				i.setServiceName(item.getService() != null ? item.getService().getName() : null);
				i.setQuantity(item.getQuantity());
				i.setUnitPrice(item.getUnitPrice());
				i.setTotal(item.getTotal());
				dtos.add(i);
			}
			detailDto.setBookingService(dtos);
		}
		detailDto.setTotal(bookingEntity.getTotal());
		detailDto.setStatus(statusName(bookingEntity.getStatus()));
		return detailDto;
	}

	@Override
	@Transactional(readOnly = true)
	public List<BookingDTO> getBooking(Long ownerId, CustomUserDetails currentUser) {
		// Chủ sân chỉ xem được đơn của sân mình
		if (!isAdmin(currentUser) && !Objects.equals(ownerId, currentUser.getUser().getId())) {
			throw new ForbiddenException("Bạn chỉ được xem đơn đặt của sân mình!");
		}
		return toBookingDTOs(bookingRepository.findByCourtOwnerId(ownerId));
	}

	@Override
	@Transactional(readOnly = true)
	public List<BookingDTO> getBookingCustomer(Long userId){
		return toBookingDTOs(bookingRepository.findByUserId(userId));
	}

	private List<BookingDTO> toBookingDTOs(List<BookingEntity> entities) {
		List<BookingDTO> dtos = new ArrayList<>();
		for (BookingEntity i : entities) {
			BookingDTO dto = new BookingDTO();
			dto.setCourtName(i.getCourt().getName());
			dto.setCustomerName(i.getUser().getFullName());
			dto.setStartTime(i.getStartTime());
			dto.setEndTime(i.getEndTime());
			dto.setTotal(i.getTotal());
			dto.setStatus(statusName(i.getStatus()));
			dto.setId(i.getId());
			dto.setCourtId(i.getCourt().getId());
			dtos.add(dto);
		}
		return dtos;
	}

	// ===== Đổi trạng thái =====

	@Override
	@Transactional
	public void approveBooking(Long id, CustomUserDetails currentUser) {
		BookingEntity booking = findBooking(id);
		if (!isCourtOwner(currentUser, booking) && !isAdmin(currentUser)) {
			throw new ForbiddenException("Bạn chỉ được duyệt đơn của sân mình!");
		}
		if (booking.getStatus() != BookingStatus.PENDING) {
			throw new BadRequestException("Chỉ có thể duyệt đơn ở trạng thái PENDING!");
		}
		booking.setStatus(BookingStatus.CONFIRMED);
		bookingRepository.save(booking);
	}

	@Override
	@Transactional
	public void cancelBooking(Long id, CustomUserDetails currentUser) {
		BookingEntity booking = findBooking(id);
		// Khách tự huỷ đơn của mình, hoặc chủ sân / admin huỷ đơn của sân
		if (!isBookingCustomer(currentUser, booking) && !isCourtOwner(currentUser, booking)
				&& !isAdmin(currentUser)) {
			throw new ForbiddenException("Bạn không có quyền huỷ đơn đặt sân này!");
		}
		if (booking.getStatus() == BookingStatus.CANCELLED) {
			throw new BadRequestException("Đơn này đã được huỷ từ trước!");
		}
		if (booking.getStatus() == BookingStatus.CONFIRMED) {
			throw new BadRequestException("Không thể huỷ đơn đã xác nhận!");
		}
		booking.setStatus(BookingStatus.CANCELLED);
		bookingRepository.save(booking);
	}
}
