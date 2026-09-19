package com.example.demo.Service;

import com.example.demo.Entity.UsersEntity;
import com.example.demo.dto.UserDTO;

public interface UserService {
	public UsersEntity creatUser(UserDTO userDTO);
}
