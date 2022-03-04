package com.cvc.consullthotels.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@Configuration
//@Profile(value = {"dev"})
public class SwaggerConfig {

    @Bean
    public Docket getDocket() {
        return new Docket(DocumentationType.SWAGGER_2)
//                .globalRequestParameters(globalParameterList())
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any())
                .build();
    }

//    private List<RequestParameter> globalParameterList() {
//
//        val appKey =
//                new RequestParameterBuilder()
//                        .name("X-ILY-API-appKey")
//                        .description("Key for the Auth")
//                        .required(true)
//                        .in(ParameterType.HEADER)
//                        .build();
//
//        val appToken =
//                new RequestParameterBuilder()
//                        .name("X-ILY-API-appToken")
//                        .description("Token for the key Auth")
//                        .required(true)
//                        .in(ParameterType.HEADER)
//                        .build();
//
//        return Arrays.asList(appKey, appToken);
//    }
}