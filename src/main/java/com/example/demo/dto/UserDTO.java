package com.example.demo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

public class UserDTO {
    @JsonProperty("name")
    @NotBlank(message = "Tên đăng nhập không được để trống")
	private String name;
    @JsonProperty("pass_word")
    @NotBlank(message = "mật khẩu không được để trống")
	private String passWord;
    @JsonProperty("full_name")
    @NotBlank(message = "Họ và tên không được để trống")
	private String fullName;
    @NotBlank(message = "Email is required")
	private String email;
	@NotBlank(message = "Phone number is required")
	private String phone;
	private String role;

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPassword() {
		return passWord;
	}
	public void setPassword(String password) {
		this.passWord = password;
	}
	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	
}
