package com.popjub.gateway.jwt;

import java.util.List;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;

import com.popjub.gateway.config.GatewayConfigProperty;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

	private final JwtTokenValidator jwtTokenValidator;
	private final GatewayConfigProperty gatewayConfigProperty;
	private final AntPathMatcher pathMatcher = new AntPathMatcher();

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		ServerHttpRequest request = exchange.getRequest();
		String path = request.getURI().getPath();

		log.info("요청 경로: {} {}", request.getMethod(), path);

		// 1. 제외 경로 체크
		boolean isExcluded = gatewayConfigProperty.getExcludedPaths()
			.stream()
			.anyMatch(pattern -> pathMatcher.match(pattern, path));

		if (isExcluded) {
			log.debug("인증 제외 경로: {}", path);
			return chain.filter(exchange);
		}

		// 2. 토큰 추출
		String token = resolveToken(request);

		if (token == null || token.isEmpty()) {
			log.warn("토큰이 없습니다.");
			return onError(exchange, "토큰이 없습니다.", HttpStatus.UNAUTHORIZED);
		}

		// 3. 토큰 검증
		if (!jwtTokenValidator.validateToken(token)) {
			log.warn("유효하지 않은 토큰입니다.");
			return onError(exchange, "유효하지 않은 토큰입니다.", HttpStatus.UNAUTHORIZED);
		}

		try {
			// 4. Claims 추출
			Claims claims = jwtTokenValidator.getClaims(token);

			Long userId = Long.parseLong(claims.getSubject());
			String userName = claims.get("userName", String.class);
			List<String> roles = claims.get("roles", List.class);

			String rolesString = String.join(",", roles);

			log.info("인증 성공 - userId: {}, userName: {}, roles: {}", userId, userName, rolesString);

			// 5. 헤더 추가
			ServerHttpRequest newRequest = request.mutate()
				.header("X-USER-ID", userId.toString())
				.header("X-USER-NAME", userName)
				.header("X-USER-ROLES", rolesString)
				.build();

			return chain.filter(exchange.mutate().request(newRequest).build());

		} catch (Exception e) {
			log.error("토큰 처리 실패: {}", e.getMessage());
			return onError(exchange, "토큰 처리 실패", HttpStatus.UNAUTHORIZED);
		}
	}

	/**
	 * Authorization 헤더에서 토큰 추출
	 */
	private String resolveToken(ServerHttpRequest request) {
		String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

		if (authHeader != null && authHeader.startsWith("Bearer ")) {
			return authHeader.substring(7);
		}

		return null;
	}

	/**
	 * 에러 응답
	 */
	private Mono<Void> onError(ServerWebExchange exchange, String errMessage, HttpStatus httpStatus) {
		ServerHttpResponse response = exchange.getResponse();
		response.setStatusCode(httpStatus);
		log.error("인증 실패: {}", errMessage);
		return response.setComplete();
	}

	@Override
	public int getOrder() {
		return -1;
	}
}
