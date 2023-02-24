package com.example.caloriecounter.user.controller.dto.response;

public record ResponseIssuedToken(
	String accessToken,
	String tokenType,
	long expiresIn
) {
}
