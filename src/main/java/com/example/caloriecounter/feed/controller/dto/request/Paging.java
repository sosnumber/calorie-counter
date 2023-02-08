package com.example.caloriecounter.feed.controller.dto.request;

public record Paging(
	long cursorNo,
	int displayPerPage
) {
}
