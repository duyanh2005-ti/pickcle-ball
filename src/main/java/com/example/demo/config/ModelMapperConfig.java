package com.example.demo.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "example.demo")
public class ModelMapperConfig {
	@Bean
    public ModelMapper modelMapper(){
        return new ModelMapper();
    }
}
