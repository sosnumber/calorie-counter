package com.example.caloriecounter.feed.controller.dto.request;

import java.time.LocalDateTime;
import java.util.List;

import com.example.caloriecounter.comment.domain.Comment;
import com.example.caloriecounter.feed.domain.LikeStatus;
import com.example.caloriecounter.photo.domain.Photo;

public record GetFeedListDto(
	long id,
	String contents,
	LocalDateTime writeDate,
	long userId,
	List<Photo> photos,
	int likeCount,
	LikeStatus likeStatus,
	List<Comment> comments
) {
}
