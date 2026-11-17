package com.imageseal.config;
import org.springframework.context.annotation.*;import org.springframework.web.servlet.config.annotation.*;
@Configuration public class CorsConfig implements WebMvcConfigurer { @Override public void addCorsMappings(CorsRegistry registry){registry.addMapping("/api/**").allowedOrigins("http://localhost:5173","http://localhost:8082","http://localhost:3000","http://localhost:4173").allowedMethods("GET","POST","DELETE","OPTIONS").allowedHeaders("*");} }
