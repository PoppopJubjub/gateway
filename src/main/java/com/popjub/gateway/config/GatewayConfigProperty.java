package com.popjub.gateway.config;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "gateway")
public class GatewayConfigProperty {

	/**
	 * 인증 불필요한 경로들
	 */
	private List<String> excludedPaths;
}
