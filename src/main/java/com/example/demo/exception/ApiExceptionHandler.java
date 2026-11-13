package com.example.demo.exception;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;

/**
 * Xử lý lỗi tập trung cho toàn bộ API.
 * Nội dung trả về giữ định dạng hiện tại: chuỗi thông báo, riêng lỗi validate là danh sách thông báo.
 */
@RestControllerAdvice
public class ApiExceptionHandler {
	private static final Logger log = LoggerFactory.getLogger(ApiExceptionHandler.class);

	/** Lỗi nghiệp vụ: BadRequest / Forbidden / NotFound / Conflict ... */
	@ExceptionHandler(ResponseStatusException.class)
	public ResponseEntity<String> handleStatus(ResponseStatusException ex) {
		return ResponseEntity.status(ex.getStatusCode()).body(ex.getReason());
	}

	/** Lỗi @Valid trên @RequestBody: trả danh sách thông báo của từng trường. */
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<List<String>> handleValidation(MethodArgumentNotValidException ex) {
		List<String> messages = ex.getBindingResult().getFieldErrors().stream()
				.map(FieldError::getDefaultMessage)
				.toList();
		return ResponseEntity.badRequest().body(messages);
	}

	/** JSON sai cú pháp hoặc sai kiểu dữ liệu (ví dụ ngày giờ sai định dạng). */
	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<String> handleUnreadable(HttpMessageNotReadableException ex) {
		return ResponseEntity.badRequest().body("Dữ liệu gửi lên không đúng định dạng!");
	}

	/** Tham số trên URL sai kiểu, ví dụ /court/booking/history/abc. */
	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public ResponseEntity<String> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
		return ResponseEntity.badRequest().body("Tham số '" + ex.getName() + "' không hợp lệ!");
	}

	/** Mọi lỗi còn lại: giữ mã HTTP chuẩn của Spring nếu có, nếu không thì 500 và không lộ chi tiết. */
	@ExceptionHandler(Exception.class)
	public ResponseEntity<String> handleOther(Exception ex) {
		if (ex instanceof ErrorResponse errorResponse) {
			// Ví dụ: 404 không có đường dẫn, 405 sai phương thức HTTP...
			return ResponseEntity.status(errorResponse.getStatusCode()).body(errorResponse.getBody().getDetail());
		}
		log.error("Lỗi không mong muốn", ex);
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Đã có lỗi xảy ra, vui lòng thử lại sau!");
	}
}
