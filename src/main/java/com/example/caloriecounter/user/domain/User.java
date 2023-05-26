package com.example.caloriecounter.user.domain;

import java.time.LocalDateTime;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import lombok.Builder;

public record User(
	long id,
	String userId,
	String name,
	String password,
	String email,
	Double weight,
	String withdrawalReason,
	@JsonSerialize(using = LocalDateTimeSerializer.class)
	@JsonDeserialize(using = LocalDateTimeDeserializer.class)
	LocalDateTime joinDate,
	UserStatus userStatus,
	UserType userType,
	JudgeStatus judgeStatus,
	String photoLink
) {

	@Builder
	public User {
	}

}
