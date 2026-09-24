package com.example.demo.Entity;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name="users")
public class UsersEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(name="nameuser")
	private String name;
	@JsonIgnore
	@Column(name="password")
	private String passWord;
	@Column(name="full_name")
	private String fullName;
	@Column(name="email")
	private String email;
	@Column(name="phone")
	private String phone;
	@Column(name="role")
	private String role;
	@JsonIgnore
	@OneToMany(mappedBy="user",fetch=FetchType.LAZY)
	private List<BookingEntity> booking=new ArrayList<>();
	@JsonIgnore
	@OneToMany(mappedBy="owner",fetch=FetchType.LAZY)
	private List<CourtsEntity> courts=new ArrayList<>();
	public List<BookingEntity> getBooking() {
		return booking;
	}
	public void setBooking(List<BookingEntity> booking) {
		this.booking = booking;
	}
	public List<CourtsEntity> getCourts() {
		return courts;
	}
	public void setCourts(List<CourtsEntity> courts) {
		this.courts = courts;
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
	public String getPassWord() {
		return passWord;
	}
	public void setPassWord(String passWord) {
		this.passWord = passWord;
	}
	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	
}
