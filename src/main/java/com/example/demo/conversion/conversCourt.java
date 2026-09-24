package com.example.demo.conversion;

import java.util.ArrayList;
import java.util.List;

import com.example.demo.Entity.CourtsEntity;
import com.example.demo.Entity.PriceConfigEntity;
import com.example.demo.dto.CourtDTO;
import com.example.demo.dto.PriceConfigDTO;

public  class  conversCourt {
	public static CourtDTO toDTO(CourtsEntity courtEntity) {
		CourtDTO dto= new CourtDTO();
		dto.setName(courtEntity.getName());
		dto.setCity(courtEntity.getCity());
		dto.setDistrict(courtEntity.getDistrict());
		dto.setDescription(courtEntity.getDescription());
		dto.setAddressDetail(courtEntity.getAddressDetail());
		dto.setImage(courtEntity.getImage());
		dto.setDistrict(courtEntity.getDistrict());
		dto.setStatus(courtEntity.getStatus());
		dto.setType(courtEntity.getType());
		dto.setOwnerId(courtEntity.getOwner() != null ? courtEntity.getOwner().getId() : null);
		List<PriceConfigDTO> price =new ArrayList<>();
		for(PriceConfigEntity p: courtEntity.getPriceConfig()) {
			PriceConfigDTO d =new PriceConfigDTO();
			d.setId(p.getId());
			d.setDayOfWeek(p.getDayOfWeek());
			d.setStartTime(p.getStartTime());
			d.setEndTime(p.getEndTime());
			d.setPricePerHour(p.getPricePerHour());
			price.add(d);
		}
		dto.setPriceConfig(price);
		return dto;
	}
}
