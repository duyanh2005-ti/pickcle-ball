package com.example.demo.Service.impl;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.Entity.CourtsEntity;
import com.example.demo.Entity.PriceConfigEntity;
import com.example.demo.Repository.CourtRepository;
import com.example.demo.Service.CourtService;
import com.example.demo.dto.CourtDTO;
import com.example.demo.dto.PriceConfigDTO;

@Service
public class CourtServiceImpl implements CourtService {
	@Autowired
	private CourtRepository courtRepository;
	@Autowired
	private ModelMapper modelMapper;
	@Override
	public List<CourtsEntity> searchCourts(String name,String city, String district){
		List<CourtsEntity> courts = courtRepository.searchCourts(name,city,district);
		return courts;
	}
	public void updateCourt(Long Id,CourtDTO Court){
		CourtsEntity court = courtRepository.findById(Id).get();
		court.setName(Court.getName());
		court.setAddressDetail(Court.getAddressDetail());
		court.setType(Court.getType());
		court.setDescription(Court.getDescription());
		court.setCity(Court.getCity());
		court.setDistrict(Court.getDistrict());
		court.setStatus(Court.getStatus());
		court.setImage(Court.getImage());
		courtRepository.save(court);

	}
	public void deleteCourts(List<Long> ids) {
		courtRepository.deleteAllById(ids);
	}
	public void addCourt(CourtDTO Court ) {
		CourtsEntity court = new CourtsEntity();
//		Court.setAddressDetail(court.getAddressDetail());
//		Court.setCity(court.getCity());
//		Court.setDescription(court.getDescription());
//		Court.setDistrict(court.getDistrict());
//		Court.setImage(court.getImage());
//		Court.setName(court.getName());
//		Court.setStatus(court.getStatus());
//		Court.setType(court.getType());
		court.setAddressDetail(Court.getAddressDetail());
		court.setDistrict(Court.getDistrict());
		court.setCity(Court.getCity());
		court.setDescription(Court.getDescription());
		court.setImage(Court.getImage());
		court.setName(Court.getName());
		court.setStatus(Court.getStatus());
		court.setType(Court.getType());
		if (court.getPriceConfig() == null) {
	        court.setPriceConfig(new ArrayList<>());
	    }
		if(Court.getPriceConfig() != null && !Court.getPriceConfig().isEmpty()) {
			for(PriceConfigDTO priceConfig: Court.getPriceConfig()) {
				PriceConfigEntity item = new PriceConfigEntity();
				item.setStartTime(priceConfig.getStartTime());
				item.setEndTime(priceConfig.getEndTime());
				item.setDayOfWeek(priceConfig.getDayOfWeek());
				item.setPricePerHour(priceConfig.getPricePerHour());
				item.setCourt(court); 
				court.getPriceConfig().add(item); 
			}
		}
		courtRepository.save(court);
	}
}
