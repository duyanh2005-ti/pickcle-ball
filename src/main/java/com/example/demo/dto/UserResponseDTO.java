package com.example.demo.dto;

import com.example.demo.Entity.UsersEntity;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Thông tin người dùng trả về cho client (không bao giờ chứa mật khẩu).
 */
public record UserResponseDTO(
		Long id,
		String name,
		@JsonProperty("full_name") String fullName,
		String email,
		String phone,
		String role) {

	public static UserResponseDTO from(UsersEntity user) {
		return new UserResponseDTO(user.getId(), user.getName(), user.getFullName(),
				user.getEmail(), user.getPhone(), user.getRole());
	}
}
