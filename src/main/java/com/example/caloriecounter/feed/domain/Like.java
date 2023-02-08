package com.example.caloriecounter.feed.domain;

public record Like(
	long id,
	long feedId,
	long userId,
	LikeStatus likeStatus
) {

}
