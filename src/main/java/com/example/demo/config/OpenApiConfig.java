package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

/**
 * Tài liệu API (Swagger UI): http://localhost:8082/swagger-ui.html
 * Gọi API cần đăng nhập: chạy POST /api/user/login lấy token, bấm nút "Authorize"
 * rồi dán token (không cần gõ chữ "Bearer").
 */
@Configuration
public class OpenApiConfig {
	private static final String BEARER = "bearerAuth";

	@Bean
	public OpenAPI pickleballOpenApi() {
		return new OpenAPI()
				.info(new Info()
						.title("Pickleball Booking API")
						.version("v1")
						.description("API đặt sân pickleball: tìm sân, đặt sân, dịch vụ đi kèm, duyệt/huỷ đơn."))
				.components(new Components().addSecuritySchemes(BEARER, new SecurityScheme()
						.type(SecurityScheme.Type.HTTP)
						.scheme("bearer")
						.bearerFormat("JWT")))
				// Gửi kèm token cho mọi API (API công khai vẫn chạy bình thường khi có token)
				.addSecurityItem(new SecurityRequirement().addList(BEARER));
	}
}
