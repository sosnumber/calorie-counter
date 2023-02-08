package com.example.caloriecounter.like.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.caloriecounter.feed.domain.Like;
import com.example.caloriecounter.feed.domain.LikeStatus;

@Mapper
public interface LikeMapper {

	void like(@Param("userId") final long userId, @Param("feedId") final long feedId,
		@Param("likeStatus") final LikeStatus likeStatus);

	int likeCount(@Param("feedId") final long feedId, @Param("likeStatus") final LikeStatus likeStatus);

	Like findByFeedAndUser(@Param("userId") final long userId, @Param("feedId") final long feedId);

	LikeStatus findLikeStatusByUserId(@Param("feedId") final long feedId, @Param("userId") final long userId);

	void changeStatus(@Param("userId") final long userId, @Param("feedId") final long feedId,
		@Param("likeStatus") final LikeStatus likeStatus);
}
