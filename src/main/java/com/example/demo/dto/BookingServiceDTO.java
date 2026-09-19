package com.example.demo.dto;

public class BookingServiceDTO {
	private Long id;
	private Integer quantity;
	private Double unitPrice;
	private Double total;
	private Long serviceId;
	private String ServiceName;
	public String getServiceName() {
		return ServiceName;
	}
	public void setServiceName(String serviceName) {
		ServiceName = serviceName;
	}
	public Long getServiceId() {
		return serviceId;
	}
	public void setServiceId(Long serviceId) {
		this.serviceId = serviceId;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Integer getQuantity() {
		return quantity;
	}
	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}
	public Double getUnitPrice() {
		return unitPrice;
	}
	public void setUnitPrice(Double unitPrice) {
		this.unitPrice = unitPrice;
	}
	public Double getTotal() {
		return total;
	}
	public void setTotal(Double total) {
		this.total = total;
	}
}
