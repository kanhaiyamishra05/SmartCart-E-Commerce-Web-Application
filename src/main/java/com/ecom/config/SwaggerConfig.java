package com.ecom.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;

/**
 * OpenAPI / Swagger UI Configuration for SmartCart REST APIs.
 */
@Configuration
public class SwaggerConfig {

	@Bean
	public OpenAPI customOpenAPI() {
		return new OpenAPI()
				.info(new Info()
						.title("SmartCart — Enterprise E-Commerce REST API Documentation")
						.version("1.0.0")
						.description("Interactive API Documentation for SmartCart E-Commerce Web Application. Includes endpoints for store catalog, orders, payments, coupons, gift cards, Pincode checker, and analytics.")
						.contact(new Contact()
								.name("Kanhaiya Kumar")
								.email("kanhaiyakumar993197@gmail.com")
								.url("https://github.com/kanhaiyamishra05/SmartCart-E-Commerce-Web-Application"))
						.license(new License().name("Apache 2.0").url("http://springdoc.org")))
				.addServersItem(new Server().url("/").description("Default Server URL"));
	}
}
