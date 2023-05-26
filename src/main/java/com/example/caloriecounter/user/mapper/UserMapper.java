package com.example.caloriecounter.user.mapper;

import java.util.Optional;

import org.apache.ibatis.annotations.Mapper;

import com.example.caloriecounter.user.controller.dto.request.SignUpForm;
import com.example.caloriecounter.user.domain.User;

@Mapper
public interface UserMapper {

	void signUp(final SignUpForm signUpForm);

	boolean hasDuplicatedId(final String userId);

	Optional<User> findByUserId(final String userId);

	String getPassword(final String userId);

	void deleteAll();

	Optional<User> findById(final long id);
}
