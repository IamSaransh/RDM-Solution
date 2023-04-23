package com.saransh.dynamicentity.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class AutowiredBeans {
    @Bean
    @Qualifier("yamlMapper")
    public ObjectMapper getJacksonMapperForYaml() {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        return mapper;
    }
    @Bean
    @Primary
    public ObjectMapper getJacksonMapper() {
        return new ObjectMapper();
    }
}
