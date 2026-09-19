package com.example.demo.Controller.CourtOwner;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.Entity.CourtsEntity;
import com.example.demo.Service.CourtService;
import com.example.demo.dto.CourtDTO;
import com.example.demo.dto.CustomUserDetails;

import jakarta.validation.Valid;

@RestController(value="CourtControllerOfAdmin")
@RequestMapping("/api/court")
public class CourtController {
	@Autowired 
	private CourtService courtService;
	@GetMapping
	public  List<CourtsEntity> getCourt(@RequestParam(required=false) String name,
									    @RequestParam(required=false)String city,
									    @RequestParam(required=false) String district){
		List<CourtsEntity> Courts=courtService.searchCourts(name,city,district);
		return Courts;
	}
	@PostMapping(value="/edit-{id}")
	public void updateCourt(@PathVariable("id") Long Id,@RequestBody CourtDTO Court ) {
		courtService.updateCourt(Id,Court);
	}
	@PostMapping(value="/{ids}")
	public void deleteCourts(@PathVariable("ids") List<Long> ids) {
		courtService.deleteCourts(ids);
	}
	@PostMapping(value="/edit")
	public void addCourt(@Valid @RequestBody CourtDTO Court,@AuthenticationPrincipal CustomUserDetails userDetails) {
		courtService.addCourt(Court,userDetails);
	}
	// lấy dạnh sách sân theo chủ sân 
	@GetMapping("/owner/{owner_id}")
	public ResponseEntity<Page<CourtDTO>> getAll(@PathVariable ("owner_id") Long id,
			@RequestParam(defaultValue="0") int page,
			@RequestParam(defaultValue="5") int size){
		Page<CourtDTO> court =courtService.ownerGetAll(id,page,size);
		return ResponseEntity.ok(court);
		
	}
}
