package com.example.caloriecounter.photo.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.caloriecounter.feed.controller.dto.request.FeedDto;
import com.example.caloriecounter.photo.controller.request.ImageUploadDto;
import com.example.caloriecounter.photo.controller.request.ImageUploadPath;
import com.example.caloriecounter.photo.controller.request.UpdateImageInfo;
import com.example.caloriecounter.photo.domain.Photo;
import com.example.caloriecounter.photo.repository.PhotoRepository;
import com.example.caloriecounter.util.FileHandler;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PhotoService {

	private final FileHandler fileHandler;
	private final PhotoRepository photoRepository;

	public List<ImageUploadPath> uploadFile(final List<MultipartFile> photos, final long userId) {
		return fileHandler.storeImages(photos, userId).stream()
			.map(photoDto -> new ImageUploadPath(photoDto.fileName(), photoDto.filePath()))
			.toList();
	}

	public void insertImage(final FeedDto feedDto) {
		this.photoRepository.insertImage(imageInfos(feedDto));
	}

	public void updateImage(final long feedId, final List<UpdateImageInfo> updateImageInfos) {
		for (UpdateImageInfo updateImageInfo : updateImageInfos) {
			this.photoRepository.updateImage(feedId, updateImageInfo);
		}
	}

	public List<Photo> photos(final long feedId) {
		return this.photoRepository.photos(feedId);
	}

	private List<ImageUploadDto> imageInfos(final FeedDto feedDto) {
		return uploadFile(feedDto.getPhotos(), feedDto.getUserId()).stream()
			.map(imageUploadPath -> new ImageUploadDto(imageUploadPath.imageName(), imageUploadPath.imagePath(),
				feedDto.getId()))
			.toList();
	}

	public List<Photo> findImageByFeedId(final long feedId) {
		return this.photoRepository.findImageByFeedId(feedId);
	}

	public void deleteAll() {
		this.photoRepository.deleteAll();
	}
}
