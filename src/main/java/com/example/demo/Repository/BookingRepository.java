package com.example.demo.Repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.Entity.BookingEntity;
import com.example.demo.enums.BookingStatus;

public interface BookingRepository extends JpaRepository<BookingEntity,Long>{
	/**
	 * Có đơn nào (chưa huỷ) của sân này giao với khoảng [startTime, endTime) không.
	 */
	@Query("select count(b) > 0 from BookingEntity b "
			+ "where b.court.id = :courtId and b.status <> :excluded "
			+ "and b.startTime < :endTime and b.endTime > :startTime")
	boolean existsOverlap(@Param("courtId") Long courtId,
			@Param("excluded") BookingStatus excluded,
			@Param("startTime") LocalDateTime startTime,
			@Param("endTime") LocalDateTime endTime);

	public List<BookingEntity> findByCourtOwnerId(Long OwnerId);
	public List<BookingEntity> findByUserId(Long UserId);
}
