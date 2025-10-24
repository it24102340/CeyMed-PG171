package com.healthinsurancemanagementsystem.CeyMedInsurance.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("index");
        registry.addViewController("/policies").setViewName("policies");
        registry.addViewController("/admin").setViewName("admin");
        registry.addRedirectViewController("/login", "/user/login");
        registry.addRedirectViewController("/signup", "/user/signup");
        registry.addRedirectViewController("/policy/admin", "/admin");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:uploads/");
    }
}


