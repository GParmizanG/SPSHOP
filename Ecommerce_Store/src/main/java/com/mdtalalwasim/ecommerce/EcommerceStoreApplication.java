package com.mdtalalwasim.ecommerce;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@PropertySource(value = "classpath:application.properties", ignoreResourceNotFound = true)
public class EcommerceStoreApplication extends SpringBootServletInitializer {

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		return builder.sources(EcommerceStoreApplication.class);
	}

	public static void main(String[] args) {
		org.springframework.context.ApplicationContext context = SpringApplication.run(EcommerceStoreApplication.class,
				args);
		System.out.println("Property: " + context.getEnvironment().getProperty("spring.datasource.url"));
		System.out.println("Ecommerce Store Application Started Successfully!");
	}
}
