package com.example.demo.Service;

import java.util.List;

import com.example.demo.dto.ServiceDTO;

public interface ServiceService {
	public void addService(ServiceDTO serviceDTO);
	public void update(Long id, ServiceDTO serviceDTO);
	public void deleteService(List<Long> id);
}
