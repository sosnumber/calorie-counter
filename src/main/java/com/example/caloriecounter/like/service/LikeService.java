package com.example.caloriecounter.like.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.caloriecounter.exception.CustomException;
import com.example.caloriecounter.feed.domain.Feed;
import com.example.caloriecounter.feed.domain.Like;
import com.example.caloriecounter.feed.domain.LikeStatus;
import com.example.caloriecounter.feed.repository.FeedRepository;
import com.example.caloriecounter.like.repository.LikeRepository;
import com.example.caloriecounter.util.StatusEnum;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class LikeService {

	private final FeedRepository feedRepository;
	private final LikeRepository likeRepository;

	@Transactional
	public void like(final long feedId, final long userId) {
		final Feed feed = this.feedRepository.findByFeedId(feedId)
			.orElseThrow(() -> new CustomException(StatusEnum.FEED_NOT_FOUND, String.format("%s not exist", feedId)));

		final Like like = this.likeRepository.findByFeedAndUser(userId, feedId);

		if (like == null) {
			this.likeRepository.like(userId, feedId, LikeStatus.ACTIVATE);
		} else if (like.likeStatus() == LikeStatus.ACTIVATE) {
			this.likeRepository.changeStatus(userId, feedId, LikeStatus.NOT_ACTIVATE);
		} else {
			this.likeRepository.changeStatus(userId, feedId, LikeStatus.ACTIVATE);
		}
	}

	public LikeStatus findLikeStatusByUserId(final long feedId, final long mockUserId) {
		return this.likeRepository.findLikeStatusByUserId(feedId, mockUserId);
	}

	public int likeCount(final long feedId) {
		final Feed feed = this.feedRepository.findByFeedId(feedId)
			.orElseThrow(() -> new CustomException(StatusEnum.FEED_NOT_FOUND, String.format("%s not exist", feedId)));

		return this.likeRepository.likeCount(feed.id(), LikeStatus.ACTIVATE);
	}

	public void deleteAll() {
		this.likeRepository.deleteAll();
	}
}
