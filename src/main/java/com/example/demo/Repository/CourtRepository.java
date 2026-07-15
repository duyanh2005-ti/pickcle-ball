package com.example.demo.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.Entity.CourtsEntity;
import com.example.demo.Repository.customer.CourtRepositoryCustom;

public interface CourtRepository extends JpaRepository<CourtsEntity,Long>, CourtRepositoryCustom{
	
}
