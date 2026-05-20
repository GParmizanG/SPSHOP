package com.mdtalalwasim.ecommerce.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

@Configuration
public class VideoResourceConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Use absolute path based on JVM working directory so it works regardless of launch location
        String absoluteVideosPath = Paths.get(System.getProperty("user.dir"), "target", "videos")
                .toAbsolutePath().toString().replace("\\", "/");

        registry.addResourceHandler("/videos/**")
                .addResourceLocations("file:///" + absoluteVideosPath + "/");
    }
}
