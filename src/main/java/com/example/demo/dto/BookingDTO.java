package com.example.demo.dto;

import java.time.LocalDateTime;
import java.util.List;

public class BookingDTO {
	
	private Long id;
	private LocalDateTime startTime;
	private LocalDateTime endTime;
	private Double total;
	private String status;
	private Long courtId;
	private Long userId;
	private String courtName;
	private String customerName;
	private List<String> serviceName;

	public List<String> getServiceName() {
		return serviceName;
	}
	public void setServiceName(List<String> serviceName) {
		this.serviceName = serviceName;
	}
	public String getCourtName() {
		return courtName;
	}
	public void setCourtName(String courtName) {
		this.courtName = courtName;
	}
	public String getCustomerName() {
		return customerName;
	}
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}
	private List<BookingServiceDTO> bookingService;
	
	public List<BookingServiceDTO> getBookingService() {
		return bookingService;
	}
	public void setBookingService(List<BookingServiceDTO> bookingService) {
		this.bookingService = bookingService;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public LocalDateTime getStartTime() {
		return startTime;
	}
	public void setStartTime(LocalDateTime startTime) {
		this.startTime = startTime;
	}
	public LocalDateTime getEndTime() {
		return endTime;
	}
	public void setEndTime(LocalDateTime endTime) {
		this.endTime = endTime;
	}
	public Double getTotal() {
		return total;
	}
	public void setTotal(Double total) {
		this.total = total;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
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
	
}
