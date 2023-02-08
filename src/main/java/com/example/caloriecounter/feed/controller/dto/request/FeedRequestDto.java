package com.example.caloriecounter.feed.controller.dto.request;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

public record FeedRequestDto(
	String contents,
	List<MultipartFile> photos
) {
}
