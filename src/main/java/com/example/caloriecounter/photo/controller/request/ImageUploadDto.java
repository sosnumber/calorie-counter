package com.example.caloriecounter.photo.controller.request;

public record ImageUploadDto(
	String imageName,
	String imagePath,
	long latestFeedId
) {
}
