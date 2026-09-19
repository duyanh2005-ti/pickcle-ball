package com.example.demo.Controller.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.Service.CourtService;
import com.example.demo.dto.CourtDTO;

@RestController
public class HomeController {
	@Autowired
	private CourtService courtService;
	@GetMapping("/court")
	public ResponseEntity<Page<CourtDTO>> getCourts(@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size){
		Page<CourtDTO> court =courtService.getAll( page, size);
		return ResponseEntity.ok(court);
	}
}
