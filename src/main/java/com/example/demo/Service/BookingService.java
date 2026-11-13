package com.example.demo.Service;

import java.util.List;

import com.example.demo.dto.BookingDTO;
import com.example.demo.dto.BookingDetailDTO;
import com.example.demo.dto.BookingListDTO;
import com.example.demo.dto.CustomUserDetails;

public interface BookingService {
	public void Booking(Long courtId, BookingDTO bookingDTO, CustomUserDetails currentUser);
	public List<BookingListDTO> getAllBooking(CustomUserDetails currentUser);
	public BookingDetailDTO getDetail(Long id, CustomUserDetails currentUser);
	public List<BookingDTO> getBooking(Long ownerId, CustomUserDetails currentUser);
	public void approveBooking(Long id, CustomUserDetails currentUser);
	public void cancelBooking(Long id, CustomUserDetails currentUser);
	public List<BookingDTO> getBookingCustomer(Long userId);
}
