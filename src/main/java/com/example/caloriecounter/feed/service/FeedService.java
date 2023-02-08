package com.example.caloriecounter.feed.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.caloriecounter.comment.service.CommentService;
import com.example.caloriecounter.exception.CustomException;
import com.example.caloriecounter.feed.controller.dto.request.FeedDto;
import com.example.caloriecounter.feed.controller.dto.request.FeedListDto;
import com.example.caloriecounter.feed.controller.dto.request.GetFeedListDto;
import com.example.caloriecounter.feed.controller.dto.request.Paging;
import com.example.caloriecounter.feed.controller.dto.request.UpdateFeedDto;
import com.example.caloriecounter.photo.controller.request.UpdateImageInfo;
import com.example.caloriecounter.feed.domain.Feed;
import com.example.caloriecounter.feed.repository.FeedRepository;
import com.example.caloriecounter.like.service.LikeService;
import com.example.caloriecounter.photo.service.PhotoService;
import com.example.caloriecounter.util.StatusEnum;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FeedService {

	private final PhotoService photoService;
	private final LikeService likeService;
	private final CommentService commentService;
	private final FeedRepository feedRepository;

	@Transactional
	public void write(final FeedDto feedDto) {
		this.feedRepository.write(feedDto);
		if (isExist(feedDto.getPhotos())) {
			this.photoService.insertImage(feedDto);
		}
	}

	// todo 개선필요: 테스트용도로 쓰이는중
	public Optional<Feed> findByFeedId(final long feedId) {
		return this.feedRepository.findByFeedId(feedId);
	}

	@Transactional
	public void update(final String contents, final List<MultipartFile> photos, final long userId, final long feedId) {
		final Feed feed = this.feedRepository.findByFeedId(feedId)
			.orElseThrow(() -> new CustomException(StatusEnum.FEED_NOT_FOUND, String.format("%s not exist", feedId)));

		if (userId != feed.userId()) {
			throw new CustomException(StatusEnum.INVALID_USER, String.format("%s is not match feedWriter", userId));
		}

		if (isExist(photos)) {
			final List<UpdateImageInfo> updateImageInfos = photoService.uploadFile(photos, userId).stream()
				.map(imageUploadPath -> new UpdateImageInfo(imageUploadPath.imageName(), imageUploadPath.imagePath()))
				.toList();

			this.photoService.updateImage(feedId, updateImageInfos);
		}

		this.feedRepository.update(feedId, new UpdateFeedDto(contents));
	}

	@Transactional
	public void delete(final long userId, final long feedId) {
		final Feed feed = this.feedRepository.findByFeedId(feedId)
			.orElseThrow(() -> new CustomException(StatusEnum.FEED_NOT_FOUND, String.format("%s not exist", feedId)));

		if (userId != feed.userId()) {
			throw new CustomException(StatusEnum.INVALID_USER, String.format("%s is not match feedWriter", userId));
		}

		this.feedRepository.delete(feedId);
	}

	public List<FeedListDto> getFeedList(final Paging paging) {
		return this.feedRepository.getFeedList(paging);
	}

	public long maxCursor() {
		return this.feedRepository.maxCursor();
	}

	public List<GetFeedListDto> feedListWithPhoto(final List<FeedListDto> feedList, final long userId,
		final int commentPageNum, final int commentPerPage) {
		return feedList.stream()
			.map(
				feedListDto -> new GetFeedListDto(
					feedListDto.feedId(),
					feedListDto.contents(),
					feedListDto.writeDate(),
					feedListDto.userId(),
					this.photoService.photos(feedListDto.feedId()),
					this.likeService.likeCount(feedListDto.feedId()),
					this.likeService.findLikeStatusByUserId(feedListDto.feedId(), userId),
					this.commentService.comment(feedListDto.feedId(), (commentPageNum - 1) * commentPerPage,
						commentPerPage)))
			.toList();
	}

	private boolean isExist(List<MultipartFile> feedDto) {
		return feedDto != null && feedDto.stream().noneMatch(MultipartFile::isEmpty);
	}
}
