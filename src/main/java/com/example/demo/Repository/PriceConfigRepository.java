package com.example.demo.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.Entity.CourtsEntity;
import com.example.demo.Entity.PriceConfigEntity;

public interface PriceConfigRepository extends JpaRepository<PriceConfigEntity,Long>{
	List<PriceConfigEntity> findByCourtsId(Long CourtId);
}
