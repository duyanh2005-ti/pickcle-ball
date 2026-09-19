package com.example.demo.filters;

import com.example.demo.Service.impl.CustomUserDetailsService;
import com.example.demo.components.JwtTokenUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

@Component
public class JwtTokenFilter extends OncePerRequestFilter {
	@Autowired
	private JwtTokenUtil jwtTokenUtil;
	
	@Autowired
	private CustomUserDetailsService userDetailsService;
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		final String authHeader = request.getHeader("Authorization");
		if(authHeader == null || !authHeader.startsWith("Bearer ")) {
			filterChain.doFilter(request,response);
			return;
		}
		final String token = authHeader.substring(7);
		final String username = jwtTokenUtil.extractUserName(token);
		
		// 2. Nếu tìm thấy username và SecurityContext chưa thiết lập đăng nhập
		if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
			UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
			// 3. Nếu token còn hạn
			if (!jwtTokenUtil.isTokenExpired(token)) {
				// Tạo đối tượng đại diện cho đăng nhập thành công
				UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
						userDetails, null, userDetails.getAuthorities()
				);
				authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				
				// Lưu trạng thái đăng nhập vào Hệ thống
				SecurityContextHolder.getContext().setAuthentication(authToken);
			}
		}
		filterChain.doFilter(request, response);
	}
}
