package com.example.demo.Controller.web;

import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
import com.example.demo.dto.UserResponseDTO;

import jakarta.validation.Valid;

@Tag(name = "Người dùng", description = "Đăng ký, đăng nhập, lịch sử đặt sân")
@RestController
@RequestMapping(value="/api/user")
public class UserController {
	@Autowired
	private BookingService bookingService;
	@Autowired
	UserService userService;
	@PostMapping(value="/register")
	public ResponseEntity<UserResponseDTO> creatUser(@Valid @RequestBody UserDTO userDTO){
		// Lỗi validate (400, danh sách thông báo) và trùng tên đăng nhập (409) do ApiExceptionHandler xử lý
		UsersEntity user = userService.creatUser(userDTO);
		return ResponseEntity.ok(UserResponseDTO.from(user));
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
			@AuthenticationPrincipal CustomUserDetails currentUser){
		List<BookingDTO> booking = bookingService.getBookingCustomer(currentUser.getUser().getId());
		return ResponseEntity.ok(booking);
	}
}

