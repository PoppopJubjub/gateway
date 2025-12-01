package com.popjub.gateway.config;

import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class SwaggerConfig {
	@Bean
	@Primary
	public SwaggerResourceProvider swaggerResourceProvider(RouteDefinitionLocator routeDefinitionLocator) {
		return new SwaggerResourceProvider(routeDefinitionLocator);
	}
}
