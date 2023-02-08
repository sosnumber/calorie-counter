package com.example.caloriecounter.feed.controller.dto.request;

import java.time.LocalDateTime;

public record FeedListDto(
	long feedId,
	String contents,
	LocalDateTime writeDate,
	long userId
) {
}
