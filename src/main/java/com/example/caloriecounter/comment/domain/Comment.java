package com.example.caloriecounter.comment.domain;

import java.time.LocalDateTime;

public record Comment(
	long id,
	long feedId,
	LocalDateTime writeDate,
	String contents,
	Long parentId,
	int depth,
	long userId,
	int groupNumber,
	int groupRefOrder,
	int childNumber

) {
	public Comment(long id, long feedId, String contents, Long parentId, int depth,
		long userId, int groupNumber, int groupRefOrder, int childNumber) {
		this(id, feedId, null, contents, parentId, depth, userId, groupNumber, groupRefOrder,
			childNumber);
	}

	public Comment(long id, long feedId, String contents, Long parentId, int depth, long userId, int groupNumber) {
		this(id, feedId, null, contents, parentId, depth, userId, groupNumber, 1, 0);
	}
}
