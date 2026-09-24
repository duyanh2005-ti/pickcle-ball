 package com.example.demo.Entity;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
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
@Table(name="court")
public class CourtsEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(name="court_name")
	private String name;
	@Column(name="type")
	private String type;
	@Column(name="description")
	private String description;
	@Column(name="city")
	private String city;
	@Column(name="district")
	private String district;
	@Column(name="address_detail")
	private String addressDetail;
	@Column(name="status")
	private String status;
	@Column(name="image")
	private String image;
	@JsonIgnore
	@OneToMany(mappedBy="court",fetch=FetchType.LAZY)
	private List<BookingEntity> booking=new ArrayList<>();
	@JsonIgnore
	@OneToMany(mappedBy = "courts", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	private List<PriceConfigEntity> priceConfig = new ArrayList<>();
	@ManyToOne
	@JoinColumn(name="owner_id")
	private UsersEntity owner;
	public UsersEntity getOwner() {
		return owner;
	}
	public void setOwner(UsersEntity owner) {
		this.owner = owner;
	}
	public void setBooking(List<BookingEntity> booking) {
		this.booking = booking;
	}
	public void setPriceConfig(List<PriceConfigEntity> priceConfig) {
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
	public String getAddressDetail() {
		return addressDetail;
	}
	public void setAddressDetail(String addressDetail) {
		this.addressDetail = addressDetail;
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
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public List<BookingEntity> getBooking() {
		return booking;
	}
	public void setBooking(ArrayList<BookingEntity> booking) {
		this.booking = booking;
	}
	public List<PriceConfigEntity> getPriceConfig() {
		return priceConfig;
	}
	public void setPriceConfig(ArrayList<PriceConfigEntity> priceConfig) {
		this.priceConfig = priceConfig;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	
}
