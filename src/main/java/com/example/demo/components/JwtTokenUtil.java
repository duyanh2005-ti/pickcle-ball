package com.example.demo.components;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.stereotype.Component;

import com.example.demo.Entity.UsersEntity;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtTokenUtil {
	// Khóa bí mật dùng để mã hóa chữ ký Token (phải bảo mật)
	private final String SECRET_KEY="mySuperSecretKeyForPickleballBookingSystemProject";
	private final Key key =Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
	// Thời gian sống của token: 1 ngày 
	private final long EXPIRATION_TIME=86400000;
	
	// 1. Hàm tạo ra chuỗi Token từ User
	public String generateToken(UsersEntity user) {
		Map<String,Object> claims = new HashMap<>();
		claims.put("role",user.getRole());
		claims.put("userId",user.getId());
		
		return Jwts.builder()
				.setClaims(claims)
				.setSubject(user.getName())
				.setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis()+EXPIRATION_TIME))
				.signWith(key, SignatureAlgorithm.HS256)
				.compact();
	}
	
	// 2. Lấy thông tin username từ Token
	public String extractUserName(String token) {
		return extractClaim(token,Claims::getSubject);
	}
	
	  // 3. Lấy thông tin Role từ Token
	public String extractRole(String token) {
		final Claims claims =extractAllClaims(token);
		return claims.get("role",String.class);
	}
	
	// Các hàm bổ trợ để đọc dữ liệu trong Token
	public<T> T extractClaim(String token, Function<Claims,T> claimsReslover) {
		final Claims claims =extractAllClaims(token);
		return claimsReslover.apply(claims);
	}
	
	private Claims extractAllClaims(String token) {
	        return Jwts.parserBuilder()
	                .setSigningKey(key)
	                .build()
	                .parseClaimsJws(token)
	                .getBody();
	}
	
	// 4. Kiểm tra Token có còn hạn dùng không
    public boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }
 // Hàm trích xuất trực tiếp userId (kiểu Long) từ Token
    public Long extractUserId(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("userId", Long.class);
    }

}
