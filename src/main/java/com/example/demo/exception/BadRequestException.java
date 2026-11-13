package com.example.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

/** Dữ liệu gửi lên không hợp lệ (HTTP 400). */
public class BadRequestException extends ResponseStatusException {
	public BadRequestException(String message) {
		super(HttpStatus.BAD_REQUEST, message);
	}
}
