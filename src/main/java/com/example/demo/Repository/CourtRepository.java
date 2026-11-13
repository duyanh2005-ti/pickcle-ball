package com.example.demo.Repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.Entity.CourtsEntity;
import com.example.demo.Repository.customer.CourtRepositoryCustom;

import jakarta.persistence.LockModeType;

public interface CourtRepository extends JpaRepository<CourtsEntity,Long>, CourtRepositoryCustom{
	Page<CourtsEntity> findByownerId(Long ownerId,Pageable pageable);
	Page<CourtsEntity> findAll(Pageable pageable);

	/**
	 * Khoá dòng sân (SELECT ... FOR UPDATE) để các yêu cầu đặt cùng một sân
	 * được xử lý lần lượt, tránh hai người đặt trùng giờ cùng lúc.
	 */
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("select c from CourtsEntity c where c.id = :id")
	Optional<CourtsEntity> findByIdForUpdate(@Param("id") Long id);
}
