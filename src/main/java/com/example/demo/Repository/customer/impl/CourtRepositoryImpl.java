package com.example.demo.Repository.customer.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.example.demo.Entity.CourtsEntity;
import com.example.demo.Repository.customer.CourtRepositoryCustom;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
@Repository
public class CourtRepositoryImpl implements CourtRepositoryCustom{
	@PersistenceContext
	private EntityManager entityManager;
	@Override
	public List<CourtsEntity> searchCourts(String name, String city,String district){
		
		StringBuilder sql = new StringBuilder();
		sql.append("select * from court c where 1=1 ");
		if(name!=" "&& name!=null) {
			sql.append("and c.court_name like '%" + name + "%' ");
		}
		if(city!=""&&city!=null) {
			sql.append(" and c.city = '"+city+"' ");
		}
		if(district!=null&&district!="") {
			sql.append(" and c.district = '"+district+"' " );
		}
		Query query = entityManager.createNativeQuery(sql.toString(), CourtsEntity.class);
		
		return query.getResultList();
	}
}
