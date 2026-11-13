package com.example.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

/** Xung đột dữ liệu, ví dụ trùng lịch đặt sân (HTTP 409). */
public class ConflictException extends ResponseStatusException {
	public ConflictException(String message) {
		super(HttpStatus.CONFLICT, message);
	}
}
