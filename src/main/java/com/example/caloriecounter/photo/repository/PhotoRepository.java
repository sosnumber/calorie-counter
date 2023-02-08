package com.example.caloriecounter.photo.repository;

import java.util.List;

import org.springframework.stereotype.Component;

import com.example.caloriecounter.photo.controller.request.ImageUploadDto;
import com.example.caloriecounter.photo.controller.request.UpdateImageInfo;
import com.example.caloriecounter.photo.domain.Photo;
import com.example.caloriecounter.photo.mapper.PhotoMapper;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PhotoRepository {

	private final PhotoMapper photoMapper;

	public void insertImage(final List<ImageUploadDto> imagePathResult) {
		this.photoMapper.insertImage(imagePathResult);
	}

	public void updateImage(final long feedId, final UpdateImageInfo updateImageInfo) {
		this.photoMapper.updateImage(feedId, updateImageInfo);
	}

	public List<Photo> photos(final long feedId) {
		return this.photoMapper.photos(feedId);
	}

	public List<Photo> findImageByFeedId(final long feedId) {
		return this.photoMapper.findImageByFeedId(feedId);
	}
}
