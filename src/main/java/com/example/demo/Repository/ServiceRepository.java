package com.example.demo.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.Entity.ServiceEntity;

public interface ServiceRepository extends JpaRepository<ServiceEntity,Long>{

}
