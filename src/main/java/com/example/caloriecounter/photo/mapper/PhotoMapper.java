package com.example.caloriecounter.photo.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.caloriecounter.photo.controller.request.ImageUploadDto;
import com.example.caloriecounter.photo.controller.request.UpdateImageInfo;
import com.example.caloriecounter.photo.domain.Photo;

@Mapper
public interface PhotoMapper {

	//todo 파라미터 1개일때도 @Param이 필요한가? 필요없을수있다.
	void insertImage(@Param("imagePathResult") final List<ImageUploadDto> imagePathResult);

	void updateImage(@Param("feedId") final long feedId,
		@Param("updateImageInfo") final UpdateImageInfo updateImageInfo);

	List<Photo> photos(final long feedId);

	void delete(final long feedId);

	List<Photo> findImageByFeedId(final long feedId);
}
