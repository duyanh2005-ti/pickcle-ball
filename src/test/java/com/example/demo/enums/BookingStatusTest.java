package com.example.demo.enums;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class BookingStatusTest {

	@Test
	void docDuocCacGiaTriCuTrongDb() {
		assertThat(BookingStatus.fromDb("Cancelled")).isEqualTo(BookingStatus.CANCELLED);
		assertThat(BookingStatus.fromDb("CANCEL")).isEqualTo(BookingStatus.CANCELLED);
		assertThat(BookingStatus.fromDb("CANCELLED")).isEqualTo(BookingStatus.CANCELLED);
		assertThat(BookingStatus.fromDb("confirmed")).isEqualTo(BookingStatus.CONFIRMED);
		assertThat(BookingStatus.fromDb(" pending ")).isEqualTo(BookingStatus.PENDING);
		assertThat(BookingStatus.fromDb(null)).isNull();
	}

	@Test
	void giaTriLa_baoLoi() {
		assertThatThrownBy(() -> BookingStatus.fromDb("ABC")).isInstanceOf(IllegalArgumentException.class);
	}
}
