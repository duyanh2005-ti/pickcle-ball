package com.example.demo.Service;

import java.util.List;

import com.example.demo.Entity.CourtsEntity;
import com.example.demo.dto.CourtDTO;

public interface CourtService {
	public List<CourtsEntity> searchCourts(String name,String city, String district);
	public void updateCourt(Long Id,CourtDTO Court);
	public void deleteCourts(List<Long> ids );
	public void addCourt(CourtDTO Court );
}
