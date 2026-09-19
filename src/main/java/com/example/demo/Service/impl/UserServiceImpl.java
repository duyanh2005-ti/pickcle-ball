package com.example.demo.Service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.Entity.UsersEntity;
import com.example.demo.Repository.UserRepository;
import com.example.demo.Service.UserService;
import com.example.demo.dto.UserDTO;

@Service
public class UserServiceImpl implements UserService{
	@Autowired
	UserRepository userRepositoty;
	@Override
	public UsersEntity creatUser(UserDTO userDTO) {
		UsersEntity user =new UsersEntity();
		user.setName(userDTO.getName());
		user.setFullName(userDTO.getFullName());
		user.setPassWord(userDTO.getPassword());
		user.setEmail(userDTO.getEmail());
		user.setPhone(userDTO.getPhone());
		user.setRole("CUSTOMER");
		userRepositoty.save(user);
		return user;
	}
}
