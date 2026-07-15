package com.example.demo.Service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.Entity.BookingServiceEntity;
import com.example.demo.Entity.ServiceEntity;
import com.example.demo.Repository.BookingServiceRepository;
import com.example.demo.Repository.ServiceRepository;
import com.example.demo.Service.ServiceService;
import com.example.demo.dto.ServiceDTO;
@Service
public class ServiceServiceImpl implements ServiceService{
	@Autowired
	ServiceRepository serviceRepository;
	@Autowired 
	BookingServiceRepository bookingServiceRepository; 
	public void addService(ServiceDTO serviceDTO) {
		ServiceEntity service=new ServiceEntity();
		service.setName(serviceDTO.getName());
		service.setUnit(serviceDTO.getUnit());
		service.setImage(serviceDTO.getImage());
		service.setPrice(serviceDTO.getPrice());
		serviceRepository.save(service);
	}
	public void update(Long id, ServiceDTO serviceDTO) {
		ServiceEntity service= serviceRepository.findById(id).get();
		service.setName(serviceDTO.getName());
		service.setPrice(serviceDTO.getPrice());
		service.setUnit(serviceDTO.getUnit());
		service.setImage(serviceDTO.getImage());
		serviceRepository.save(service);
		// chinh sửa bookingService khi Price cập nhật
		List<BookingServiceEntity> bookingService= bookingServiceRepository.findByserviceId(id);
		for(BookingServiceEntity item :bookingService) {
			double quantity = 0;
			quantity = item.getQuantity();
			double unitPrice =0;
			unitPrice =serviceDTO.getPrice();
			item.setUnitPrice(serviceDTO.getPrice());
			item.setTotal(quantity*unitPrice);
			bookingServiceRepository.save(item);
		}
	}
	public void deleteService(List<Long> id) {
		serviceRepository.deleteAllById(id);;
	}
}
