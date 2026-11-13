package com.example.demo.enums;

/**
 * Trạng thái đơn đặt sân. Lưu trong DB dạng chuỗi (PENDING / CONFIRMED / CANCELLED).
 */
public enum BookingStatus {
	PENDING,
	CONFIRMED,
	CANCELLED;

	/**
	 * Đọc giá trị từ DB, chấp nhận cả các cách viết cũ ("Cancelled", "CANCEL", ...).
	 */
	public static BookingStatus fromDb(String value) {
		if (value == null || value.isBlank()) {
			return null;
		}
		String v = value.trim().toUpperCase();
		if (v.startsWith("CANCEL")) {
			return CANCELLED;
		}
		if (v.startsWith("CONFIRM") || v.equals("APPROVED")) {
			return CONFIRMED;
		}
		if (v.equals("PENDING")) {
			return PENDING;
		}
		throw new IllegalArgumentException("Trạng thái đơn đặt sân không hợp lệ: " + value);
	}
}
