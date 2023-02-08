package com.example.caloriecounter.comment.controller.response;

public record CommentResponseDto(
	long commentId,
	String contents,
	int groupNumber
) {
}
