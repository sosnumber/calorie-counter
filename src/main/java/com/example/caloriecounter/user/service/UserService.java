package com.example.caloriecounter.user.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.caloriecounter.exception.CustomException;
import com.example.caloriecounter.user.repository.UserRepository;
import com.example.caloriecounter.user.controller.dto.request.LoginForm;
import com.example.caloriecounter.user.controller.dto.request.ResponseIssuedToken;
import com.example.caloriecounter.user.controller.dto.request.SignUpForm;
import com.example.caloriecounter.user.domain.User;
import com.example.caloriecounter.util.PasswordEncrypt;
import com.example.caloriecounter.util.StatusEnum;
import com.example.caloriecounter.util.TokenService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;
	private final TokenService tokenService;
	private final UserCacheService userCacheService;

	@Transactional
	public void signUp(final SignUpForm signUpForm) {
		if (this.userRepository.hasDuplicatedId(signUpForm.getUserId())) {
			throw new CustomException(StatusEnum.DUPLICATED_ID,
				String.format("%s has duplicated Id", signUpForm.getUserId()));
		}
		this.userRepository.signUp(signUpForm);
	}

	@Transactional
	public ResponseIssuedToken login(final LoginForm loginForm) {
		final User user = this.findByUserId(loginForm.userId())
			.orElseThrow(() -> new CustomException(
				StatusEnum.USER_NOT_FOUND, String.format("userId %s not exist", loginForm.userId())));

		if (!PasswordEncrypt.isMatch(loginForm.userPassword(), this.userRepository.getPassword(loginForm.userId()))) {
			throw new CustomException(StatusEnum.PASSWORD_NOT_MATCH);
		}

		//todo 로그인 5회 실패시 lock, Response
		this.userCacheService.setUser(user);
		return this.tokenService.issue(loginForm.userId());
	}

	public Optional<User> findByUserId(final String userId) {
		return Optional.ofNullable(
			this.userCacheService.getUser(userId).orElseGet(() -> this.userRepository.findByUserId(userId).orElse(null)
			));
	}
}
