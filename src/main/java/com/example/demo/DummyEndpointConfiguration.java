package com.example.demo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RequestPredicate;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
class DummyEndpointConfiguration {

    @Bean
    RouterFunction<ServerResponse> routes(DummyHandler handler) {
        return route(i(GET("/dummies")), handler::all)
            .andRoute(i(GET("/dummies/{id}")), handler::getById)
            .andRoute(i(DELETE("/dummies/{id}")), handler::deleteById)
            .andRoute(i(POST("/dummies")), handler::create)
            .andRoute(i(PUT("/dummies/{id}")), handler::updateById);
    }

    private static RequestPredicate i(RequestPredicate target) {
        return new CaseInsensitiveRequestPredicate(target);
    }
}