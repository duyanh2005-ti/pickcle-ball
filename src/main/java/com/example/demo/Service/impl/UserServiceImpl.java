package com.example.demo.Service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.Entity.UsersEntity;
import com.example.demo.Repository.UserRepository;
import com.example.demo.Service.UserService;
import com.example.demo.dto.UserDTO;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UserServiceImpl implements UserService{
	private final UserRepository userRepositoty;
	private final PasswordEncoder passwordEncoder;

	public UserServiceImpl(UserRepository userRepositoty, PasswordEncoder passwordEncoder) {
		this.userRepositoty = userRepositoty;
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	public UsersEntity creatUser(UserDTO userDTO) {
		String username = userDTO.getName().trim();
		// Chặn đăng ký trùng tên đăng nhập (trả về 409 Conflict)
		if (userRepositoty.existsByName(username)) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Tên đăng nhập đã tồn tại");
		}
		UsersEntity user =new UsersEntity();
		user.setName(username);
		user.setFullName(userDTO.getFullName());
		// Chỉ lưu mật khẩu đã băm BCrypt, không bao giờ lưu mật khẩu thô
		user.setPassWord(passwordEncoder.encode(userDTO.getPassword()));
		user.setEmail(userDTO.getEmail());
		user.setPhone(userDTO.getPhone());
		user.setRole("CUSTOMER");
		userRepositoty.save(user);
		return user;
	}
}
