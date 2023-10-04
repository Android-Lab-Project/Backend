package com.healthtechbd.backend.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info =@Info(
                title = "HealTechBD Rest API Documentation",
                version = "1.0",
                description = "rest api documentation",
                contact = @Contact(
                        name="Abdullah Al Mahmud",
                        email="almahmudaraf@gmail.com"
                )
        )

)
public class SwaggerConfig {


}
