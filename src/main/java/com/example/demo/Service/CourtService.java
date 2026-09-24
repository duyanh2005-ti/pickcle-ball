package com.example.demo.Service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.example.demo.Entity.CourtsEntity;
import com.example.demo.dto.CourtDTO;
import com.example.demo.dto.CustomUserDetails;

public interface CourtService {
	public List<CourtDTO> searchCourts(String name,String city, String district);
	public void updateCourt(Long Id,CourtDTO Court);
	public void deleteCourts(List<Long> ids );
	public void addCourt(CourtDTO Court ,CustomUserDetails userDetails);
	public Page<CourtDTO> ownerGetAll(Long id,int size,int page);
	public Page<CourtDTO> getAll(int page, int size);
}
