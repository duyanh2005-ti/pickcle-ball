package com.example.demo.dto;

import java.util.Collections;
import java.util.Collection;

import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.authority.SimpleGrantedAuthority;


import com.example.demo.Entity.UsersEntity;

public class CustomUserDetails implements UserDetails{
	private final UsersEntity user;
	public CustomUserDetails(UsersEntity user) {
		this.user=user;
	}
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole()));
	}
	@Override
    public String getPassword() { 
		return user.getPassWord(); 
	}
    @Override
    public String getUsername() { 
    	return user.getName(); 
    }
    @Override
    public boolean isAccountNonExpired() { 
    	return true; 
    }
    @Override
    public boolean isAccountNonLocked() { 
    	return true; 
    }
    @Override
    public boolean isCredentialsNonExpired() { 
    	return true; 
    }
    @Override
    public boolean isEnabled() { 
    	return true; 
    }
    
    public UsersEntity getUser() { 
    	return user; 
    }

}
