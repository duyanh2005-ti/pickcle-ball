package com.example.demo.dto;

import java.util.List;

public class ServiceDTO {
	private Long id;
	private String name;
	private Double price;
	private String unit;
	private String image;
	private List<BookingServiceDTO> bookingServiceDTO;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Double getPrice() {
		return price;
	}
	public void setPrice(Double price) {
		this.price = price;
	}
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	public List<BookingServiceDTO> getBookingServiceDTO() {
		return bookingServiceDTO;
	}
	public void setBookingServiceDTO(List<BookingServiceDTO> bookingServiceDTO) {
		this.bookingServiceDTO = bookingServiceDTO;
	}
}
