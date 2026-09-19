package com.example.demo.Service;

import java.util.List;

import com.example.demo.dto.BookingDTO;
import com.example.demo.dto.BookingDetailDTO;
import com.example.demo.dto.BookingListDTO;
import com.example.demo.dto.CustomUserDetails;

public interface BookingService {
	public void Booking(Long id,BookingDTO bookingDTO,CustomUserDetails userDetails);
	public List<BookingListDTO> getAllBooking(CustomUserDetails userDetails);
	public BookingDetailDTO getDetail(Long id);
	public List<BookingDTO> getBooking(Long id);
	public void approveBooking(long id);
	public void cancelBooking(Long id);
	public List<BookingDTO> getBookingCustomer(Long id);
}
