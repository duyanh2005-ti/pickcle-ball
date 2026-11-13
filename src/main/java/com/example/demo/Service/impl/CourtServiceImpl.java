package com.example.demo.Service.impl;

import java.util.ArrayList;
import com.example.demo.conversion.conversCourt;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.exception.ForbiddenException;
import com.example.demo.exception.NotFoundException;

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
	public void updateCourt(Long Id, CourtDTO Court, CustomUserDetails currentUser) {
		CourtsEntity court = courtRepository.findById(Id)
				.orElseThrow(() -> new NotFoundException("Không tìm thấy sân có ID:" + Id));
		checkOwner(court, currentUser);
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
	@Override
	@Transactional
	public void deleteCourts(List<Long> ids, CustomUserDetails currentUser) {
		List<CourtsEntity> courts = courtRepository.findAllById(ids);
		if (courts.size() != new HashSet<>(ids).size()) {
			throw new NotFoundException("Có sân không tồn tại trong danh sách cần xoá!");
		}
		// Chỉ xoá khi tất cả các sân đều thuộc người đang đăng nhập (hoặc là admin)
		for (CourtsEntity court : courts) {
			checkOwner(court, currentUser);
		}
		courtRepository.deleteAll(courts);
	}

	private void checkOwner(CourtsEntity court, CustomUserDetails currentUser) {
		if ("ADMIN".equalsIgnoreCase(currentUser.getUser().getRole())) {
			return;
		}
		Long ownerId = court.getOwner() == null ? null : court.getOwner().getId();
		if (!Objects.equals(ownerId, currentUser.getUser().getId())) {
			throw new ForbiddenException("Bạn chỉ được thao tác trên sân của mình (ID sân: " + court.getId() + ")");
		}
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
	public Page<CourtDTO> ownerGetAll(Long id, int page, int size) {
		Pageable pageable = PageRequest.of(page,size,Sort.by(Sort.Direction.DESC,"id"));
		Page<CourtsEntity> courtsEntities =courtRepository.findByownerId(id,pageable);
		return courtsEntities.map(conversCourt::toDTO);
	}

	@Override
	public Page<CourtDTO> getAll(int page, int size) {
		Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
		Page<CourtsEntity> courtsPage = courtRepository.findAll(pageable);
		return courtsPage.map(conversCourt::toDTO);
	}
}
