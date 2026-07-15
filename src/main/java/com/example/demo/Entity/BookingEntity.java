package com.example.demo.Entity;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name="booking")
public class BookingEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(name="start_time")
	private LocalDateTime startTime;
	@Column(name="end_time")
	private LocalDateTime endTime;
	@Column(name="total")
	private Double total;
	@Column(name="status")
	private String status;
	@ManyToOne
	@JoinColumn(name="user_id")
	private UsersEntity user;
	@ManyToOne
	@JoinColumn(name="court_id")
	private CourtsEntity court;
	@JsonIgnore
	@OneToMany(mappedBy="booking",fetch=FetchType.LAZY)
	private List<BookingServiceEntity> bookingService=new ArrayList<>();
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
	public UsersEntity getUser() {
		return user;
	}
	public void setUser(UsersEntity user) {
		this.user = user;
	}
	public CourtsEntity getCourt() {
		return court;
	}
	public void setCourt(CourtsEntity court) {
		this.court = court;
	}
	public List<BookingServiceEntity> getBookingService() {
		return bookingService;
	}
	public void setBookingService(List<BookingServiceEntity> bookingService) {
		this.bookingService = bookingService;
	}
}
