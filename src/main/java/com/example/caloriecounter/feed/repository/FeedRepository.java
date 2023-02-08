package com.example.caloriecounter.feed.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.example.caloriecounter.comment.mapper.CommentMapper;
import com.example.caloriecounter.feed.controller.dto.request.FeedDto;
import com.example.caloriecounter.feed.controller.dto.request.FeedListDto;
import com.example.caloriecounter.feed.controller.dto.request.Paging;
import com.example.caloriecounter.feed.controller.dto.request.UpdateFeedDto;
import com.example.caloriecounter.feed.domain.Feed;
import com.example.caloriecounter.feed.mapper.FeedMapper;
import com.example.caloriecounter.photo.mapper.PhotoMapper;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class FeedRepository {

	private final FeedMapper feedMapper;
	private final PhotoMapper photoMapper;
	private final CommentMapper commentMapper;

	public void write(final FeedDto feedDto) {
		this.feedMapper.write(feedDto);
	}

	public void update(final long feedId, final UpdateFeedDto feedDto) {
		if (feedDto.contents() == null || "".equals(feedDto.contents())) {
			return;
		}
		this.feedMapper.update(feedId, feedDto.contents());
	}

	public Optional<Feed> findByFeedId(final long feedId) {
		return this.feedMapper.findByFeedId(feedId);
	}

	public void delete(final long feedId) {
		this.feedMapper.delete(feedId);
		this.photoMapper.delete(feedId);
		this.commentMapper.delete(feedId);
	}

	public List<FeedListDto> getFeedList(final Paging paging) {
		return this.feedMapper.getFeedList(paging);
	}

	public long maxCursor() {
		return this.feedMapper.maxCursor();
	}
}
