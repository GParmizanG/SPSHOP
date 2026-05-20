package com.mdtalalwasim.ecommerce.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class VideoResourceConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Expose the target/videos directory directly to localhost:8080/videos/
        registry.addResourceHandler("/videos/**")
                .addResourceLocations("file:target/videos/");
    }
}
