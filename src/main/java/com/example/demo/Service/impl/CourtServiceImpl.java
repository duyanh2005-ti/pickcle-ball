package com.example.demo.Service.impl;

import java.util.ArrayList;
import com.example.demo.conversion.conversCourt;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.example.demo.Entity.CourtsEntity;
import com.example.demo.Entity.PriceConfigEntity;
import com.example.demo.Repository.CourtRepository;
import com.example.demo.Service.CourtService;
import com.example.demo.dto.CourtDTO;
import com.example.demo.dto.CustomUserDetails;
import com.example.demo.dto.PriceConfigDTO;

@Service
public class CourtServiceImpl implements CourtService {
	@Autowired
	private CourtRepository courtRepository;
	@Autowired
	private ModelMapper modelMapper;
	
	@Override
	public List<CourtDTO> searchCourts(String name, String city, String district) {
		return courtRepository.searchCourts(name, city, district).stream().map(conversCourt::toDTO).toList();
	}
	@Override
	public void updateCourt(Long Id, CourtDTO Court) {
		CourtsEntity court = courtRepository.findById(Id)
				.orElseThrow(() -> new RuntimeException("Không tìm thấy sân có ID:" + Id));
		court.setName(Court.getName());
		court.setAddressDetail(Court.getAddressDetail());
		court.setType(Court.getType());
		court.setDescription(Court.getDescription());
		court.setCity(Court.getCity());
		court.setDistrict(Court.getDistrict());
		court.setStatus(Court.getStatus());
		court.setImage(Court.getImage());
//		modelMapper.map(Court, court);
		courtRepository.save(court);
	}
	public void deleteCourts(List<Long> ids) {
		courtRepository.deleteAllById(ids);
	}

	public void addCourt(CourtDTO Court, CustomUserDetails userDetails) {
		CourtsEntity court = new CourtsEntity();
		court.setAddressDetail(Court.getAddressDetail());
		court.setDistrict(Court.getDistrict());
		court.setCity(Court.getCity());
		court.setDescription(Court.getDescription());
		court.setImage(Court.getImage());
		court.setName(Court.getName());
		court.setStatus(Court.getStatus());
		court.setType(Court.getType());
		court.setOwner(userDetails.getUser());
		if (court.getPriceConfig() == null) {
			court.setPriceConfig(new ArrayList<>());
		}
		if (Court.getPriceConfig() != null && !Court.getPriceConfig().isEmpty()) {
			for (PriceConfigDTO priceConfig : Court.getPriceConfig()) {
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

	@Override
	public Page<CourtDTO> ownerGetAll(Long id, int size, int page) {
		Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
		Page<CourtsEntity> CourtsPage = courtRepository.findByownerId(id, pageable);
		Page<CourtDTO> dto = CourtsPage.map(item -> {
			CourtDTO i = new CourtDTO();
			i.setName(item.getName());
			i.setCity(item.getCity());
			i.setDescription(item.getDescription());
			i.setDistrict(item.getDistrict());
			i.setStatus(item.getStatus());
			i.setImage(item.getImage());
			i.setType(item.getType());
			i.setAddressDetail(item.getAddressDetail());
			return i;
		});
		return dto;
	}

	@Override
	public Page<CourtDTO> getAll(int page, int size) {
		Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
		Page<CourtsEntity> courtsPage = courtRepository.findAll(pageable);
		Page<CourtDTO> dtoPage = courtsPage.map(item -> {
			CourtDTO i = new CourtDTO();
			i.setName(item.getName());
			i.setCity(item.getCity());
			i.setDescription(item.getDescription());
			i.setDistrict(item.getDistrict());
			i.setImage(item.getImage());
			i.setStatus(item.getStatus());
			i.setType(item.getType());
			i.setAddressDetail(item.getAddressDetail());
			return i;
		});
		return dtoPage;
	}
}
