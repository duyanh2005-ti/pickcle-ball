package com.example.demo.dto;

import java.util.List;

public class CourtDTO {
	private Long id;
	private String name;
	private String type;
	private String description;
	private String city;
	private String district;
	private String addressDetail;
	private String status;
	private String image;
	List<PriceConfigDTO> priceConfig;
	
	public List<PriceConfigDTO> getPriceConfig() {
		return priceConfig;
	}
	public void setPriceConfig(List<PriceConfigDTO> priceConfig) {
		this.priceConfig = priceConfig;
	}
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
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getDistrict() {
		return district;
	}
	public void setDistrict(String district) {
		this.district = district;
	}
	public String getAddressDetail() {
		return addressDetail;
	}
	public void setAddressDetail(String addressDetail) {
		this.addressDetail = addressDetail;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	
}
