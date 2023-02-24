package com.example.caloriecounter.user.controller.dto.response;

import org.springframework.http.HttpStatus;

public record ResponseTokenAuthFail(
	String error,
	String message,
	HttpStatus statusCode
) {

}
