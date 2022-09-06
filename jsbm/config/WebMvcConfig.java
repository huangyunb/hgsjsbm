package com.jsbm.config;


import com.jsbm.utils.JacksonObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import java.util.List;

@Slf4j
@Configuration
public class WebMvcConfig extends WebMvcConfigurationSupport {
    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        log.info("开始进行静态资源映射...");
        registry.addResourceHandler("/userInfo/pages/**").addResourceLocations("classpath:/userInfo/pages/");
        registry.addResourceHandler("/userInfo/mycss/**").addResourceLocations("classpath:/userInfo/mycss/");
        registry.addResourceHandler("/userInfo/img/**").addResourceLocations("classpath:/userInfo/img/");
        registry.addResourceHandler("/userInfo/js/**").addResourceLocations("classpath:/userInfo/js/");
        registry.addResourceHandler("/userInfo/pages/teacherInfo/**").addResourceLocations("classpath:/userInfo/pages/teacherInfo/");
    }

    @Override
    protected void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        MappingJackson2HttpMessageConverter messageConverter = new MappingJackson2HttpMessageConverter();
        messageConverter.setObjectMapper(new JacksonObjectMapper());
        converters.add(0,messageConverter);
    }
}
