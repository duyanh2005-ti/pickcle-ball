package com.example.demo.Controller.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.Entity.UsersEntity;
import com.example.demo.Service.BookingService;
import com.example.demo.Service.UserService;
import com.example.demo.Service.impl.CustomUserDetailsService;
import com.example.demo.components.JwtTokenUtil;
import com.example.demo.dto.BookingDTO;
import com.example.demo.dto.CustomUserDetails;
import com.example.demo.dto.LoginDTO;
import com.example.demo.dto.LoginResponseDTO;
import com.example.demo.dto.UserDTO;

import jakarta.validation.Valid;

@RestController
@RequestMapping(value="/api/user")
public class UserController {
	@Autowired 
	private BookingService bookingService;
	@Autowired 
	UserService userService;
	@PostMapping(value="/register")
	public ResponseEntity<?> creatUser(@Valid @RequestBody UserDTO userDTO,BindingResult result){
		try{
			if(result.hasErrors()){
				List<String> errorMessages =result.getFieldErrors().stream().map(FieldError::getDefaultMessage).toList();
				return ResponseEntity.badRequest().body(errorMessages);
			}
			UsersEntity user =userService.creatUser(userDTO);
			return ResponseEntity.ok(user);
		}catch(Exception ex){
			 return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
		}
	}
	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private JwtTokenUtil jwtTokenUtil;
	
	@Autowired
	private CustomUserDetailsService userDetailsService; // Sử dụng lớp này để load thông tin User

	@PostMapping("/login")
	public ResponseEntity<?> login(@Valid @RequestBody LoginDTO loginDTO,BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
	        String errorMessage = bindingResult.getFieldError().getDefaultMessage();
	        return ResponseEntity.badRequest().body(errorMessage); 
	        // Trả về ngay: "mật khẩu không được để trống" với mã HTTP 400 Bad Request
	    }
	    try {
	        // 1. Xác thực thông tin tài khoản đăng nhập
	        authenticationManager.authenticate(
	                new UsernamePasswordAuthenticationToken(loginDTO.getUsername(), loginDTO.getPassword())
	        );

	        // 2. Lấy thông tin user từ DB
	        CustomUserDetails userDetails = (CustomUserDetails) userDetailsService.loadUserByUsername(loginDTO.getUsername());
	        UsersEntity user = userDetails.getUser();

	        // 3. Sinh Token
	        String token = jwtTokenUtil.generateToken(user);

	        // 4. Trả kết quả về cho Frontend
	        return ResponseEntity.ok(new LoginResponseDTO(token, user.getRole(), user.getId()));
	        
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Tài khoản hoặc mật khẩu không chính xác!");
	    }
	}
	@GetMapping("history")
	public ResponseEntity<List<BookingDTO>> getMyBookingHistory(
	        @RequestHeader("Authorization") String authHeader){
		String token = authHeader.substring(7);
		Long userId = jwtTokenUtil.extractUserId(token);
		List<BookingDTO> booking = bookingService.getBookingCustomer(userId);
		return ResponseEntity.ok(booking);
	}
}
