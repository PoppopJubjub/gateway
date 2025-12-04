package com.popjub.gateway.swagger;

import java.util.ArrayList;
import java.util.List;

import org.springdoc.core.properties.SwaggerUiConfigProperties;
import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import org.springframework.stereotype.Component;

@Component
public class SwaggerResourceProvider {

	private final RouteDefinitionLocator routeDefinitionLocator;

	public SwaggerResourceProvider(RouteDefinitionLocator routeDefinitionLocator) {
		this.routeDefinitionLocator = routeDefinitionLocator;
	}

	public List<SwaggerUiConfigProperties.SwaggerUrl> getSwaggerUrls() {
		List<SwaggerUiConfigProperties.SwaggerUrl> urls = new ArrayList<>();

		// User Service
		urls.add(createSwaggerUrl("user-service", "/user-service/api-docs"));

		// Store Service
		urls.add(createSwaggerUrl("store-service", "/store-service/api-docs"));

		// Reservation Service
		urls.add(createSwaggerUrl("reservation-service", "/reservation-service/api-docs"));

		// Review Service
		urls.add(createSwaggerUrl("review-service", "/review-service/api-docs"));

		// Notification Service
		urls.add(createSwaggerUrl("notification-service", "/notification-service/api-docs"));

		// Ai-service
		urls.add(createSwaggerUrl("ai-service", "/ai-service/api-docs"));

		return urls;
	}

	private SwaggerUiConfigProperties.SwaggerUrl createSwaggerUrl(String name, String url) {
		SwaggerUiConfigProperties.SwaggerUrl swaggerUrl = new SwaggerUiConfigProperties.SwaggerUrl();
		swaggerUrl.setName(name);
		swaggerUrl.setUrl(url);
		return swaggerUrl;
	}
}
