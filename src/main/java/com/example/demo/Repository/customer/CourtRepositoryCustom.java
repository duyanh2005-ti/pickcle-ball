package com.example.demo.Repository.customer;

import java.util.List;

import com.example.demo.Entity.CourtsEntity;

public interface CourtRepositoryCustom {
	public List<CourtsEntity> searchCourts(String name, String city,String district);
}
