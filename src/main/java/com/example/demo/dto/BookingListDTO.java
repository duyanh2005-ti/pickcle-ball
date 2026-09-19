package com.example.demo.dto;

import java.time.LocalDateTime;

public class BookingListDTO {
	 private Long id;
	 private LocalDateTime startTime;
	 private LocalDateTime endTime;
	 private Double total;
	 private String status;
	 private String courtName;
	 private String customerName;
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
	 
}
