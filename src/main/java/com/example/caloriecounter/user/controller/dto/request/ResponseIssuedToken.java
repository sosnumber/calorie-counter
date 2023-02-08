package com.example.caloriecounter.user.controller.dto.request;

public record ResponseIssuedToken(
	String accessToken,
	String tokenType,
	long expiresIn
) {
}
