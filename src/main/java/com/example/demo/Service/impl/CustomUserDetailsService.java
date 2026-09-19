package com.example.demo.Service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.demo.Entity.UsersEntity;
import com.example.demo.Repository.UserRepository;
import com.example.demo.dto.CustomUserDetails;

@Service
public class CustomUserDetailsService implements UserDetailsService{
	@Autowired
	private UserRepository userRepository;
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		UsersEntity user = userRepository.findByName(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy user: " + username));		
		return new CustomUserDetails(user);
	}
	
}
