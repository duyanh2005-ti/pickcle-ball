package com.example.demo.Service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Time;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.demo.Entity.BookingEntity;
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
import com.example.demo.dto.BookingDTO;
import com.example.demo.dto.BookingServiceDTO;
import com.example.demo.dto.CustomUserDetails;
import com.example.demo.enums.BookingStatus;
import com.example.demo.exception.BadRequestException;
import com.example.demo.exception.ConflictException;
import com.example.demo.exception.ForbiddenException;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

	@Mock private BookingRepository bookingRepository;
	@Mock private CourtRepository courtrepository;
	@Mock private UserRepository userRepository;
	@Mock private ServiceRepository serviceRepository;
	@Mock private PriceConfigRepository priceConfigRepository;
	@Mock private BookingServiceRepository bookingServiceRepository;

	@InjectMocks private BookingServiceImpl bookingService;

	private static final Long COURT_ID = 10L;

	private UsersEntity owner;
	private UsersEntity customer;
	private UsersEntity otherUser;
	private CourtsEntity court;
	/** Thứ Hai tuần sau, luôn nằm trong tương lai. */
	private LocalDate nextMonday;

	@BeforeEach
	void setUp() {
		owner = user(1L, "COURT_OWNER");
		customer = user(2L, "CUSTOMER");
		otherUser = user(3L, "COURT_OWNER");
		court = new CourtsEntity();
		court.setId(COURT_ID);
		court.setName("Sân 1");
		court.setOwner(owner);
		nextMonday = LocalDate.now().plusDays(1).with(TemporalAdjusters.next(DayOfWeek.MONDAY));
	}

	// ===== Đặt sân: kiểm tra dữ liệu =====

	@Test
	void datSan_gioKetThucTruocGioBatDau_bao400() {
		BookingDTO dto = booking(nextMonday.atTime(10, 0), nextMonday.atTime(9, 0));
		assertThatThrownBy(() -> bookingService.Booking(COURT_ID, dto, me(customer)))
				.isInstanceOf(BadRequestException.class);
		verify(bookingRepository, never()).save(any());
	}

	@Test
	void datSan_thoiDiemDaQua_bao400() {
		LocalDateTime yesterday = LocalDateTime.now().minusDays(1);
		BookingDTO dto = booking(yesterday, yesterday.plusHours(1));
		assertThatThrownBy(() -> bookingService.Booking(COURT_ID, dto, me(customer)))
				.isInstanceOf(BadRequestException.class);
	}

	@Test
	void datSan_trungLich_bao409_vaKhoaSanTruoc() {
		when(courtrepository.findByIdForUpdate(COURT_ID)).thenReturn(Optional.of(court));
		when(bookingRepository.existsOverlap(eq(COURT_ID), eq(BookingStatus.CANCELLED), any(), any()))
				.thenReturn(true);
		BookingDTO dto = booking(nextMonday.atTime(8, 0), nextMonday.atTime(9, 0));

		assertThatThrownBy(() -> bookingService.Booking(COURT_ID, dto, me(customer)))
				.isInstanceOf(ConflictException.class);
		verify(courtrepository).findByIdForUpdate(COURT_ID);
		verify(bookingRepository, never()).save(any());
	}

	// ===== Đặt sân: tính giá =====

	@Test
	void datSan_quaHaiKhungGia_tinhTienTungKhung() {
		// Ngày thường: 06:00-18:00 giá 100k, 18:00-22:00 giá 150k. Đặt 17:00-19:00 => 100k + 150k
		givenBookableCourt(List.of(
				price("Ngày thường", 6, 18, 100_000),
				price("Ngày thường", 18, 22, 150_000)));
		bookingService.Booking(COURT_ID, booking(nextMonday.atTime(17, 0), nextMonday.atTime(19, 0)), me(customer));

		BookingEntity saved = captureSavedBooking();
		assertThat(saved.getTotal()).isEqualTo(250_000);
		assertThat(saved.getStatus()).isEqualTo(BookingStatus.PENDING);
	}

	@Test
	void datSan_khungGiaDenNuaDem_vanTinhDung() {
		// 20:00-00:00 được hiểu là đến hết ngày
		givenBookableCourt(List.of(price("Ngày thường", 20, 0, 200_000)));
		bookingService.Booking(COURT_ID,
				booking(nextMonday.atTime(22, 30), nextMonday.atTime(23, 30)), me(customer));
		assertThat(captureSavedBooking().getTotal()).isEqualTo(200_000);
	}

	@Test
	void datSan_cuoiTuan_dungBangGiaCuoiTuan() {
		LocalDate saturday = nextMonday.plusDays(5);
		givenBookableCourt(List.of(
				price("Ngày thường", 6, 22, 100_000),
				price("Cuối tuần", 6, 22, 180_000)));
		bookingService.Booking(COURT_ID, booking(saturday.atTime(9, 0), saturday.atTime(10, 30)), me(customer));
		assertThat(captureSavedBooking().getTotal()).isEqualTo(270_000);
	}

	@Test
	void datSan_khongCoBangGiaChoKhungGio_bao400_khongTaoDon0d() {
		when(courtrepository.findByIdForUpdate(COURT_ID)).thenReturn(Optional.of(court));
		when(userRepository.findById(customer.getId())).thenReturn(Optional.of(customer));
		when(priceConfigRepository.findByCourtsId(COURT_ID)).thenReturn(List.of(price("Ngày thường", 6, 18, 100_000)));

		// 17:00-19:00: từ 18:00 không có bảng giá
		BookingDTO dto = booking(nextMonday.atTime(17, 0), nextMonday.atTime(19, 0));
		assertThatThrownBy(() -> bookingService.Booking(COURT_ID, dto, me(customer)))
				.isInstanceOf(BadRequestException.class)
				.hasMessageContaining("18:00");
		verify(bookingRepository, never()).save(any());
	}

	@Test
	void datSan_coDichVu_congTienDichVuTheoDonGiaHienTai() {
		givenBookableCourt(List.of(price("Ngày thường", 6, 22, 100_000)));
		ServiceEntity water = new ServiceEntity();
		water.setId(5L);
		water.setName("Nước suối");
		water.setPrice(10_000.0);
		when(serviceRepository.findById(5L)).thenReturn(Optional.of(water));

		BookingDTO dto = booking(nextMonday.atTime(8, 0), nextMonday.atTime(9, 0));
		dto.setBookingService(List.of(serviceItem(5L, 3)));
		bookingService.Booking(COURT_ID, dto, me(customer));

		ArgumentCaptor<BookingEntity> captor = ArgumentCaptor.forClass(BookingEntity.class);
		verify(bookingRepository, org.mockito.Mockito.atLeastOnce()).save(captor.capture());
		assertThat(captor.getValue().getTotal()).isEqualTo(130_000);
	}

	@Test
	void datSan_soLuongDichVuKhongHopLe_bao400() {
		givenBookableCourt(List.of(price("Ngày thường", 6, 22, 100_000)));
		BookingDTO dto = booking(nextMonday.atTime(8, 0), nextMonday.atTime(9, 0));
		dto.setBookingService(List.of(serviceItem(5L, 0)));
		assertThatThrownBy(() -> bookingService.Booking(COURT_ID, dto, me(customer)))
				.isInstanceOf(BadRequestException.class);
	}

	// ===== Phân quyền =====

	@Test
	void xemChiTiet_nguoiKhac_bao403() {
		givenExistingBooking(BookingStatus.PENDING);
		assertThatThrownBy(() -> bookingService.getDetail(100L, me(otherUser)))
				.isInstanceOf(ForbiddenException.class);
	}

	@Test
	void xemChiTiet_khachDatVaChuSan_duocXem() {
		givenExistingBooking(BookingStatus.PENDING);
		assertThat(bookingService.getDetail(100L, me(customer)).getStatus()).isEqualTo("PENDING");
		assertThat(bookingService.getDetail(100L, me(owner)).getCourtName()).isEqualTo("Sân 1");
	}

	@Test
	void xemDonCuaChuSanKhac_bao403() {
		assertThatThrownBy(() -> bookingService.getBooking(owner.getId(), me(otherUser)))
				.isInstanceOf(ForbiddenException.class);
	}

	@Test
	void duyetDon_chuSanKhac_bao403() {
		givenExistingBooking(BookingStatus.PENDING);
		assertThatThrownBy(() -> bookingService.approveBooking(100L, me(otherUser)))
				.isInstanceOf(ForbiddenException.class);
	}

	@Test
	void duyetDon_khachHang_bao403() {
		givenExistingBooking(BookingStatus.PENDING);
		assertThatThrownBy(() -> bookingService.approveBooking(100L, me(customer)))
				.isInstanceOf(ForbiddenException.class);
	}

	@Test
	void duyetDon_chuSan_chuyenSangConfirmed() {
		BookingEntity booking = givenExistingBooking(BookingStatus.PENDING);
		bookingService.approveBooking(100L, me(owner));
		assertThat(booking.getStatus()).isEqualTo(BookingStatus.CONFIRMED);
	}

	@Test
	void huyDon_khachTuHuyDonCuaMinh_chuyenSangCancelled() {
		BookingEntity booking = givenExistingBooking(BookingStatus.PENDING);
		bookingService.cancelBooking(100L, me(customer));
		assertThat(booking.getStatus()).isEqualTo(BookingStatus.CANCELLED);
	}

	@Test
	void huyDon_donDaHuy_bao400() {
		givenExistingBooking(BookingStatus.CANCELLED);
		assertThatThrownBy(() -> bookingService.cancelBooking(100L, me(customer)))
				.isInstanceOf(BadRequestException.class);
	}

	@Test
	void huyDon_nguoiLa_bao403() {
		givenExistingBooking(BookingStatus.PENDING);
		assertThatThrownBy(() -> bookingService.cancelBooking(100L, me(otherUser)))
				.isInstanceOf(ForbiddenException.class);
	}

	// ===== Hàm hỗ trợ =====

	private void givenBookableCourt(List<PriceConfigEntity> prices) {
		when(courtrepository.findByIdForUpdate(COURT_ID)).thenReturn(Optional.of(court));
		when(bookingRepository.existsOverlap(eq(COURT_ID), eq(BookingStatus.CANCELLED), any(), any()))
				.thenReturn(false);
		when(userRepository.findById(customer.getId())).thenReturn(Optional.of(customer));
		when(priceConfigRepository.findByCourtsId(COURT_ID)).thenReturn(prices);
		when(bookingRepository.save(any(BookingEntity.class))).thenAnswer(inv -> inv.getArgument(0));
	}

	private BookingEntity givenExistingBooking(BookingStatus status) {
		BookingEntity booking = new BookingEntity();
		booking.setId(100L);
		booking.setCourt(court);
		booking.setUser(customer);
		booking.setStatus(status);
		booking.setStartTime(nextMonday.atTime(8, 0));
		booking.setEndTime(nextMonday.atTime(9, 0));
		booking.setTotal(100_000.0);
		org.mockito.Mockito.lenient().when(bookingRepository.findById(100L)).thenReturn(Optional.of(booking));
		return booking;
	}

	private BookingEntity captureSavedBooking() {
		ArgumentCaptor<BookingEntity> captor = ArgumentCaptor.forClass(BookingEntity.class);
		verify(bookingRepository).save(captor.capture());
		return captor.getValue();
	}

	private static UsersEntity user(Long id, String role) {
		UsersEntity u = new UsersEntity();
		u.setId(id);
		u.setName("user" + id);
		u.setFullName("Người dùng " + id);
		u.setRole(role);
		return u;
	}

	private static CustomUserDetails me(UsersEntity user) {
		return new CustomUserDetails(user);
	}

	private static BookingDTO booking(LocalDateTime start, LocalDateTime end) {
		BookingDTO dto = new BookingDTO();
		dto.setStartTime(start);
		dto.setEndTime(end);
		return dto;
	}

	private static BookingServiceDTO serviceItem(Long serviceId, int quantity) {
		BookingServiceDTO dto = new BookingServiceDTO();
		dto.setServiceId(serviceId);
		dto.setQuantity(quantity);
		return dto;
	}

	private static PriceConfigEntity price(String day, int fromHour, int toHour, double pricePerHour) {
		PriceConfigEntity p = new PriceConfigEntity();
		p.setDayOfWeek(day);
		p.setStartTime(Time.valueOf(LocalTime.of(fromHour, 0)));
		p.setEndTime(Time.valueOf(LocalTime.of(toHour, 0)));
		p.setPricePerHour(pricePerHour);
		return p;
	}
}
