package com.example.caloriecounter.user.controller;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.caloriecounter.user.controller.dto.request.LoginForm;
import com.example.caloriecounter.user.controller.dto.request.SignUpForm;
import com.example.caloriecounter.user.controller.dto.response.ResponseIssuedToken;
import com.example.caloriecounter.user.controller.dto.response.ResponseSignUpForm;
import com.example.caloriecounter.user.service.UserService;
import com.example.caloriecounter.util.CustomResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;

	@PostMapping("/users")
	public ResponseEntity<CustomResponse<ResponseSignUpForm>> signUpSubmit(
		@RequestBody @Valid final SignUpForm signUpForm) {
		this.userService.signUp(signUpForm);
		return CustomResponse.created(new ResponseSignUpForm(signUpForm.getId(), signUpForm.getUserName(),
			signUpForm.getEmail()));
	}

	@PostMapping("/login")
	public ResponseEntity<CustomResponse<ResponseIssuedToken>> login(@RequestBody @Valid LoginForm loginForm) {
		return CustomResponse.ok(this.userService.login(loginForm));
	}
}