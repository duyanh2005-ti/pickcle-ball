package com.example.demo.enums;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class BookingStatusConverter implements AttributeConverter<BookingStatus, String> {
	@Override
	public String convertToDatabaseColumn(BookingStatus status) {
		return status == null ? null : status.name();
	}

	@Override
	public BookingStatus convertToEntityAttribute(String dbValue) {
		return BookingStatus.fromDb(dbValue);
	}
}
