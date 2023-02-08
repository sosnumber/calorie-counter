package com.example.caloriecounter.comment.controller.response;

import java.time.LocalDateTime;
import java.util.List;

import com.example.caloriecounter.comment.domain.Comment;

import lombok.Getter;

@Getter
public class CommentDto {
	long id;
	long userId;
	LocalDateTime writeDate;
	String contents;
	Long parentId;
	int depth;
	List<CommentDto> list;

	public CommentDto(Comment firstComment) {
		this(firstComment.id(), firstComment.userId(), firstComment.writeDate(), firstComment.contents(),
			firstComment.parentId(), firstComment.depth(), null);
	}

	public CommentDto(long id, long userId, LocalDateTime writeDate, String contents, Long parentId, int depth,
		List<CommentDto> list) {
		this.id = id;
		this.userId = userId;
		this.writeDate = writeDate;
		this.contents = contents;
		this.parentId = parentId;
		this.depth = depth;
		this.list = list;
	}
}
