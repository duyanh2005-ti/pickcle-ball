package com.example.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

/** Người dùng đã đăng nhập nhưng không có quyền với tài nguyên này (HTTP 403). */
public class ForbiddenException extends ResponseStatusException {
	public ForbiddenException(String message) {
		super(HttpStatus.FORBIDDEN, message);
	}
}
