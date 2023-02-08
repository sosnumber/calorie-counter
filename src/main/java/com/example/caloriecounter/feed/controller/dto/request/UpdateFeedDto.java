package com.example.caloriecounter.feed.controller.dto.request;

import java.util.List;

import com.example.caloriecounter.photo.controller.request.ImageUploadDto;

public record UpdateFeedDto(
	String contents,
	List<ImageUploadDto> imageUploadDtos
) {
	public UpdateFeedDto(final String contents) {
		this(contents, null);
	}
}
