package com.example.caloriecounter.like.repository;

import org.springframework.stereotype.Component;

import com.example.caloriecounter.feed.domain.Like;
import com.example.caloriecounter.feed.domain.LikeStatus;
import com.example.caloriecounter.like.mapper.LikeMapper;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class LikeRepository {

	private final LikeMapper likeMapper;

	public void like(final long userId, final long feedId, final LikeStatus likeStatus) {
		this.likeMapper.like(userId, feedId, likeStatus);
	}

	public int likeCount(final long feedId, final LikeStatus likeStatus) {
		return this.likeMapper.likeCount(feedId, likeStatus);
	}

	public Like findByFeedAndUser(final long userId, final long feedId) {
		return this.likeMapper.findByFeedAndUser(userId, feedId);
	}

	public LikeStatus findLikeStatusByUserId(final long feedId, final long mockUserId) {
		return this.likeMapper.findLikeStatusByUserId(feedId, mockUserId);
	}

	public void changeStatus(final long userId, final long feedId, final LikeStatus likeStatus) {
		this.likeMapper.changeStatus(userId, feedId, likeStatus);
	}

	public void deleteAll() {
		this.likeMapper.deleteAll();
	}
}
