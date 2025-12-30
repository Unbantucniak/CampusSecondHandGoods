package com.example.campussecondhandgoods.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {//自定义Spring MVC的配置（这里是把图片等静态资源放在uploads里）
    @Value("${file.upload-dir:uploads}") //配置文件赋值，没有就直接uploads
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize(); //转绝对路径+规范化
        String location = uploadPath.toUri().toString(); //路径转URI字符串
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(location);
    }
}
