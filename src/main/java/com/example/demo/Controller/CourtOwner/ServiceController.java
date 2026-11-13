package com.example.demo.Controller.CourtOwner;

import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.Service.ServiceService;
import com.example.demo.dto.ServiceDTO;

@Tag(name = "Dịch vụ", description = "Quản lý dịch vụ đi kèm (chủ sân, admin)")
@RestController
@RequestMapping("/api/service")
public class ServiceController {
	@Autowired
	ServiceService serviceService;
	@PostMapping(value="/add")
	public void addService(@RequestBody ServiceDTO serviceDTO) {
		serviceService.addService(serviceDTO);
	}
	@PostMapping(value="/edit/{id}")
	public void upadate(@PathVariable("id") Long id,@RequestBody ServiceDTO serviceDTO) {
		serviceService.update(id,serviceDTO);
	}
	@DeleteMapping(value="/delete/{ids}")
	public void deleteService(@PathVariable("ids") List<Long> id){
		serviceService.deleteService(id);
	}
	
}
