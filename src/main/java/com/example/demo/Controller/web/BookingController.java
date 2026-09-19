package com.example.demo.Controller.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.Service.BookingService;
import com.example.demo.dto.BookingDTO;
import com.example.demo.dto.BookingDetailDTO;
import com.example.demo.dto.BookingListDTO;
import com.example.demo.dto.CustomUserDetails;
@RestController
@RequestMapping("/court/booking")
public class BookingController {
	@Autowired 
	private BookingService bookingService;
	@PostMapping("/{id}")
	public ResponseEntity<String> createBooking(@PathVariable ("id") Long id,@RequestBody BookingDTO bookingDTO, @AuthenticationPrincipal CustomUserDetails userDetails){
		try{
			bookingService.Booking(id,bookingDTO,userDetails);
			return new ResponseEntity<>("Đặt sân thành công!", HttpStatus.CREATED);
		}catch(RuntimeException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}
	@GetMapping("/history")
	public List<BookingListDTO> history(@AuthenticationPrincipal CustomUserDetails userDetails) {
		List<BookingListDTO> getAll= bookingService.getAllBooking(userDetails);
		return getAll;
	}
	@GetMapping("/history/{id}")
	public BookingDetailDTO detail(@PathVariable ("id") Long id) {
		BookingDetailDTO getDetail=bookingService.getDetail(id);
		return getDetail;
	}
	@GetMapping("/owner/{owner_id}")
	public List<BookingDTO> getBookingOfOwner(@PathVariable ("owner_id") Long id){
		List<BookingDTO> getBooking=bookingService.getBooking(id);
		return getBooking;
	}
	@PutMapping("/approve/{id}")
	public ResponseEntity<String> approveBooking(@PathVariable("id") Long id) {
	    try {
	        bookingService.approveBooking(id);
	        return ResponseEntity.ok("Duyệt đơn đặt sân thành công!");
	    } catch (RuntimeException e) {
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
	    }
	}

	@PutMapping("/cancel/{id}")
	public ResponseEntity<String> cancelBooking(@PathVariable("id") Long id) {
	    try {
	        bookingService.cancelBooking(id);
	        return ResponseEntity.ok("Hủy đơn đặt sân thành công!");
	    } catch (RuntimeException e) {
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
	    }
	}
}
