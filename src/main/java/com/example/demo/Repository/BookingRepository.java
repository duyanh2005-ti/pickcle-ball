package com.example.demo.Repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.Entity.BookingEntity;

public interface BookingRepository extends JpaRepository<BookingEntity,Long>{
	boolean existsByCourtIdAndStatusNotAndStartTimeLessThanAndEndTimeGreaterThan(
            Long courtId,          
            String status,         
            LocalDateTime endTime, 
            LocalDateTime startTime 
    );
	public List<BookingEntity> findByCourtOwnerId(Long OwnerId);
	public List<BookingEntity> findByUserId(Long UserId);
}
