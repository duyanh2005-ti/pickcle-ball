package com.example.demo.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.Entity.BookingServiceEntity;

public interface BookingServiceRepository extends JpaRepository<BookingServiceEntity,Long>{
	public List<BookingServiceEntity> findByserviceId(Long serviceId);
}
