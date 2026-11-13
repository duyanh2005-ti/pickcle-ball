package com.example.demo.Repository.customer.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;

import com.example.demo.Entity.CourtsEntity;
import com.example.demo.Repository.customer.CourtRepositoryCustom;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.util.StringUtils;

@Repository
public class CourtRepositoryImpl implements CourtRepositoryCustom{
	@PersistenceContext
	private EntityManager entityManager;
	@Override
	public List<CourtsEntity> searchCourts(String name, String city,String district){
		StringBuilder jpql = new StringBuilder("select c from CourtsEntity c where 1 = 1");
		Map<String, Object> params = new HashMap<>();

		if (StringUtils.hasText(name)) {
			jpql.append(" and lower(c.name) like :name escape '!'");
			params.put("name", "%" + escapeLike(name.trim().toLowerCase(Locale.ROOT)) + "%");
		}
		if (StringUtils.hasText(city)) {
			jpql.append(" and c.city = :city");
			params.put("city", city.trim());
		}
		if (StringUtils.hasText(district)) {
			jpql.append(" and c.district = :district");
			params.put("district", district.trim());
		}
		jpql.append(" order by c.id desc");

		TypedQuery<CourtsEntity> query = entityManager.createQuery(jpql.toString(), CourtsEntity.class);
		for (Map.Entry<String, Object> p : params.entrySet()) {
			query.setParameter(p.getKey(), p.getValue());
		}
		return query.getResultList();
	}

	private static String escapeLike(String value) {
		return value.replace("!", "!!").replace("%", "!%").replace("_", "!_");
	}
}
