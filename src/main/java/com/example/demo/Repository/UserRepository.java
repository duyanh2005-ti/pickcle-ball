package com.example.demo.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.Entity.UsersEntity;

public interface UserRepository extends JpaRepository<UsersEntity,Long>{
	public Optional<UsersEntity> findByName(String Name); 
}
