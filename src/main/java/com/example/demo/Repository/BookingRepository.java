package com.example.demo.Repository;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.Entity.BookingEntity;

public interface BookingRepository extends JpaRepository<BookingEntity,Long>{
	boolean existsByCourtIdAndStatusNotAndStartTimeLessThanAndEndTimeGreaterThan(
            Long courtId,          
            String status,         
            LocalDateTime endTime, 
            LocalDateTime startTime 
    );
}
