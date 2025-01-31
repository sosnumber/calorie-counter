package com.example.caloriecounter.user.controller.dto.response;

import org.springframework.http.HttpStatus;

public record ResponseTokenDto(
	String message,
	String accessToken,
	String refreshToken,
	HttpStatus statusCode
) {
}
