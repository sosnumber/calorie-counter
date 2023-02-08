package com.example.caloriecounter.feed.domain;

import java.time.LocalDateTime;
import java.util.List;

import com.example.caloriecounter.photo.domain.Photo;

public record Feed(
	long id,
	String contents,
	LocalDateTime writeDate,
	long userId,
	List<Photo> photos,
	List<Like> likes
) {
	//Mybatis FeedMapper.xml 매핑을 위한 생성자들
	public Feed(long id, String contents, long userId) {
		this(id, contents, null, userId, null, null);
	}

	public Feed(long id, String contents, LocalDateTime writeDate, long userId) {
		this(id, contents, writeDate, userId, null, null);
	}
}
