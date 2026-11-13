package com.example.demo.Service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.Entity.ServiceEntity;
import com.example.demo.Repository.ServiceRepository;
import com.example.demo.Service.ServiceService;
import com.example.demo.dto.ServiceDTO;
import com.example.demo.exception.NotFoundException;
@Service
public class ServiceServiceImpl implements ServiceService{
	@Autowired
	ServiceRepository serviceRepository;
	public void addService(ServiceDTO serviceDTO) {
		ServiceEntity service=new ServiceEntity();
		service.setName(serviceDTO.getName());
		service.setUnit(serviceDTO.getUnit());
		service.setImage(serviceDTO.getImage());
		service.setPrice(serviceDTO.getPrice());
		serviceRepository.save(service);
	}
	public void update(Long id, ServiceDTO serviceDTO) {
		ServiceEntity service = serviceRepository.findById(id)
				.orElseThrow(() -> new NotFoundException("Không tìm thấy dịch vụ!"));
		service.setName(serviceDTO.getName());
		service.setPrice(serviceDTO.getPrice());
		service.setUnit(serviceDTO.getUnit());
		service.setImage(serviceDTO.getImage());
		serviceRepository.save(service);
		// Không sửa các đơn đã đặt: booking_service giữ nguyên đơn giá tại thời điểm đặt,
		// giá mới chỉ áp dụng cho các đơn đặt sau này.
	}
	public void deleteService(List<Long> id) {
		serviceRepository.deleteAllById(id);;
	}
	@Override
	public List<ServiceDTO> getAll(){
		List<ServiceDTO> dto= new ArrayList<>();
		for(ServiceEntity e : serviceRepository.findAll()) {
			ServiceDTO p= new ServiceDTO();
			p.setId(e.getId());
			p.setName(e.getName());
			p.setImage(e.getImage());
			p.setPrice(e.getPrice());
			p.setUnit(e.getUnit());
			dto.add(p);
		}
		return dto;
	}
}
