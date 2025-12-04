package com.popjub.gateway.jwt;

import java.nio.charset.StandardCharsets;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JwtTokenValidator {

	private final SecretKey secretKey;

	public JwtTokenValidator(JwtProperties jwtProperties) {
		this.secretKey = Keys.hmacShaKeyFor(
			jwtProperties.getSecret()
				.getBytes(StandardCharsets.UTF_8)
		);
	}

	/**
	 * 토큰 검증
	 */
	public boolean validateToken(String token) {
		try {
			Jwts.parser()
				.verifyWith(secretKey)
				.build()
				.parseSignedClaims(token);
			return true;
		} catch (JwtException | IllegalArgumentException e) {
			log.warn("유효하지 않은 토큰입니다: {}", e.getMessage());
			return false;
		}
	}

	/**
	 * Claims 추출
	 */
	public Claims getClaims(String token) {
		return Jwts.parser()
			.verifyWith(secretKey)
			.build()
			.parseSignedClaims(token)
			.getPayload();
	}
}
