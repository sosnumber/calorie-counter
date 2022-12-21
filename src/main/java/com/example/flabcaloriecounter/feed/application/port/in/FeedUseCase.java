package com.example.flabcaloriecounter.feed.application.port.in;

import java.util.Optional;

import com.example.flabcaloriecounter.feed.application.port.in.dto.FeedDto;
import com.example.flabcaloriecounter.feed.domain.Feed;

public interface FeedUseCase {

	void write(final FeedDto feedDto, final long userId);

	Optional<Feed> findByFeedId(final long feedId);
}
