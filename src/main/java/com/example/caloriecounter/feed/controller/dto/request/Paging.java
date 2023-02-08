package com.example.caloriecounter.feed.controller.dto.request;

public record Paging(
	Long cursorNo,
	int displayPerPage
) {
}
