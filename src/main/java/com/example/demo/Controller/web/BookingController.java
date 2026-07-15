package com.example.demo.Controller.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.Service.BookingService;
import com.example.demo.dto.BookingDTO;
@RestController
@RequestMapping("/court/booking")
public class BookingController {
	@Autowired 
	private BookingService bookingService;
	@PostMapping("/{id}")
	public ResponseEntity<String> createBooking(@PathVariable ("id") Long id,@RequestBody BookingDTO bookingDTO){
		try{
			bookingService.Booking(id,bookingDTO);
			return new ResponseEntity<>("Đặt sân thành công!", HttpStatus.CREATED);
		}catch(RuntimeException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}
}
