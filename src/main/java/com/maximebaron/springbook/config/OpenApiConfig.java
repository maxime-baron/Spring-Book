package com.maximebaron.springbook.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI springBookOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Spring Book API")
                        .version("1.0")
                        .description("API for managing books"))
                .tags(List.of(
                        new Tag().name("Books").description("Everything about books")
                ));
    }
}
