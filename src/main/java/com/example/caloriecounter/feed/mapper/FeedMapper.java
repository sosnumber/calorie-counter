package com.example.caloriecounter.feed.mapper;

import java.util.List;
import java.util.Optional;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.caloriecounter.feed.controller.dto.request.FeedDto;
import com.example.caloriecounter.feed.controller.dto.request.FeedListDto;
import com.example.caloriecounter.feed.controller.dto.request.Paging;
import com.example.caloriecounter.feed.domain.Feed;

@Mapper
public interface FeedMapper {

	void write(final FeedDto feedDto);

	void update(@Param("feedId") final long feedId, @Param("contents") final String contents);

	Optional<Feed> findByFeedId(final long feedId);

	void delete(final long feedId);

	List<FeedListDto> getFeedList(@Param("paging") final Paging paging);

	Long maxCursor();

	void deleteAll();
}
