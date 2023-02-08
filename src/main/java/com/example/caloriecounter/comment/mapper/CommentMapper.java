package com.example.caloriecounter.comment.mapper;

import java.util.List;
import java.util.Optional;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.caloriecounter.comment.domain.Comment;
import com.example.caloriecounter.comment.controller.request.CommentRequestDto;
import com.example.caloriecounter.comment.controller.request.ReplyDto;

@Mapper
public interface CommentMapper {

	void insertComment(@Param("feedId") final long feedId, @Param("userId") final long userId,
		@Param("commentRequestDto") final CommentRequestDto commentRequestDto);

	void insertReply(@Param("replyDto") final ReplyDto replyDto);

	Optional<Comment> findCommentById(@Param("parentId") final Long parentId);

	List<Comment> comment(@Param("feedId") final long feedId, @Param("offset") final int offset,
		@Param("commentPerPage") final int commentPerPage);

	int countParent(final long feedId);

	int maxDepth(final int groupNumber);

	void updateRefOrder(@Param("parentOrderResult") final int parentOrderResult,
		@Param("groupNumber") final int groupNumber);

	void updateChild(final Long parentId);

	void delete(final long feedId);
}
