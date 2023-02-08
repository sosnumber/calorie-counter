package com.example.caloriecounter.user.controller.dto.request;

import org.springframework.http.HttpStatus;

public record ResponseTokenAuthFail(
	String error,
	String message,
	HttpStatus statusCode
) {

}
