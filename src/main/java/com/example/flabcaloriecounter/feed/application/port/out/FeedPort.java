package com.example.flabcaloriecounter.feed.application.port.out;

import java.util.List;
import java.util.Optional;

import com.example.flabcaloriecounter.feed.adapter.in.web.FeedInfoDto;
import com.example.flabcaloriecounter.feed.application.port.in.response.FeedUpdateDto;
import com.example.flabcaloriecounter.feed.application.service.ImageUploadPath;
import com.example.flabcaloriecounter.feed.domain.Feed;

public interface FeedPort {

	void write(final FeedInfoDto feedInfoDto);

	void update(final Feed feed, final FeedUpdateDto feedUpdateDto);

	Optional<Feed> findByFeedId(final Long feedId);

	void insertImage(final List<ImageUploadPath> uploadFile);
}