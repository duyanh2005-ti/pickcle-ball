package com.example.demo.dto;

import java.util.List;

public class BookingDetailDTO extends BookingListDTO{
	
	private Long courtId;
    private Long userId;
    private String address;
    public Long getCourtId() {
		return courtId;
	}
	public void setCourtId(Long courtId) {
		this.courtId = courtId;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public List<BookingServiceDTO> getBookingService() {
		return bookingService;
	}
	public void setBookingService(List<BookingServiceDTO> bookingService) {
		this.bookingService = bookingService;
	}
	private List<BookingServiceDTO> bookingService; 
}
