package com.example.caloriecounter.feed.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.caloriecounter.comment.service.CommentService;
import com.example.caloriecounter.config.UserAuthentication;
import com.example.caloriecounter.exception.CustomException;
import com.example.caloriecounter.comment.controller.request.CommentRequestDto;
import com.example.caloriecounter.feed.controller.dto.request.FeedDto;
import com.example.caloriecounter.feed.controller.dto.request.FeedListDto;
import com.example.caloriecounter.feed.controller.dto.request.GetFeedListDto;
import com.example.caloriecounter.feed.controller.dto.request.Paging;
import com.example.caloriecounter.comment.controller.response.CommentResponseDto;
import com.example.caloriecounter.feed.service.FeedService;
import com.example.caloriecounter.like.service.LikeService;
import com.example.caloriecounter.util.CustomResponse;
import com.example.caloriecounter.util.StatusEnum;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/feeds")
@RequiredArgsConstructor
public class FeedController {

	private final FeedService feedService;
	private final LikeService likeService;
	private final CommentService commentService;

	@PostMapping
	public ResponseEntity<CustomResponse<Void>> write(final UserAuthentication userAuthentication,
		@RequestPart(required = false) final List<MultipartFile> photos,
		@RequestPart(required = false) final String contents) {
		if ((contents == null || "".equals(contents)) && (photos == null || photos.stream()
			.anyMatch(MultipartFile::isEmpty))) {
			throw new CustomException(StatusEnum.EMPTY_FEED);
		}

		this.feedService.write(new FeedDto(contents, photos, userAuthentication.id()));
		return CustomResponse.created();
	}

	@PutMapping("/{feedId}")
	public ResponseEntity<CustomResponse<Void>> update(final UserAuthentication userAuthentication,
		@RequestPart(required = false) final List<MultipartFile> photos,
		@RequestPart(required = false) final String contents,
		@PathVariable final long feedId) {
		if ((contents == null || "".equals(contents)) && (photos == null || photos.stream()
			.anyMatch(MultipartFile::isEmpty))) {
			throw new CustomException(StatusEnum.EMPTY_FEED);
		}

		this.feedService.update(contents, photos, userAuthentication.id(), feedId);
		return CustomResponse.created();
	}

	@GetMapping
	public ResponseEntity<CustomResponse<List<GetFeedListDto>>> feedList(final UserAuthentication authentication,
		@RequestParam final long cursorNo,
		@RequestParam(required = false, defaultValue = "5") final int displayPerPage,
		@RequestParam(required = false, defaultValue = "1") final int commentPageNum,
		@RequestParam(required = false, defaultValue = "30") final int commentPerPage) {
		if (cursorNo <= 0) {
			final List<FeedListDto> feedList = this.feedService.getFeedList(
				new Paging(this.feedService.maxCursor(), displayPerPage));
			return CustomResponse.ok(this.feedService.feedListWithPhoto(feedList, authentication.id(),
				commentPageNum, commentPerPage));
		}

		final List<FeedListDto> feedList = this.feedService.getFeedList(
			new Paging(cursorNo, displayPerPage));
		return CustomResponse.ok(this.feedService.feedListWithPhoto(feedList, authentication.id(),
			commentPageNum, commentPerPage));
	}

	@PostMapping("/{feedId}/like")
	public ResponseEntity<CustomResponse<Void>> like(final UserAuthentication authentication,
		@PathVariable final long feedId) {
		this.likeService.like(feedId, authentication.id());
		return CustomResponse.created();
	}

	@DeleteMapping("/{feedId}")
	public ResponseEntity<CustomResponse<Void>> delete(final UserAuthentication authentication,
		@PathVariable final long feedId) {
		this.feedService.delete(authentication.id(), feedId);
		return CustomResponse.ok();
	}

	@PostMapping("/{feedId}/comment")
	public ResponseEntity<CustomResponse<CommentResponseDto>> comment(final UserAuthentication userAuthentication,
		@PathVariable final long feedId, @RequestBody final CommentRequestDto commentRequestDto) {
		if (commentRequestDto.getParentId() == null) {
			this.commentService.insertComment(feedId, userAuthentication.id(), commentRequestDto);
			return CustomResponse.created(
				new CommentResponseDto(commentRequestDto.getCommentId(), commentRequestDto.getContents(),
					commentRequestDto.getGroupNumber()));
		}
		this.commentService.reply(feedId, userAuthentication.id(), commentRequestDto);
		return CustomResponse.created(
			new CommentResponseDto(commentRequestDto.getCommentId(), commentRequestDto.getContents(),
				commentRequestDto.getGroupNumber()));
	}

	//todo 수정, 삭제
}
