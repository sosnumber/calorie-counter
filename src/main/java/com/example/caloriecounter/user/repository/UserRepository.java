package com.example.caloriecounter.user.repository;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.example.caloriecounter.user.controller.dto.request.SignUpForm;
import com.example.caloriecounter.user.domain.User;
import com.example.caloriecounter.user.mapper.UserMapper;
import com.example.caloriecounter.util.PasswordEncrypt;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserRepository {

	private final UserMapper userMapper;

	public void signUp(final SignUpForm signUpForm) {
		signUpForm.setUserPassword(PasswordEncrypt.encrypt(signUpForm.getUserPassword()));
		this.userMapper.signUp(signUpForm);
	}

	public boolean hasDuplicatedId(final String userId) {
		return this.userMapper.hasDuplicatedId(userId);
	}

	public Optional<User> findByUserId(final String userId) {
		return this.userMapper.findByUserId(userId);
	}

	public String getPassword(final String userId) {
		return this.userMapper.getPassword(userId);
	}

	public void deleteAll() {
		this.userMapper.deleteAll();
	}
}
