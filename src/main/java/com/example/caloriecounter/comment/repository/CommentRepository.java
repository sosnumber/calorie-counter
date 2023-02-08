package com.example.caloriecounter.comment.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.example.caloriecounter.comment.domain.Comment;
import com.example.caloriecounter.comment.mapper.CommentMapper;
import com.example.caloriecounter.comment.controller.request.CommentRequestDto;
import com.example.caloriecounter.comment.controller.request.ReplyDto;

import lombok.RequiredArgsConstructor;

//todo 현재 이 프로젝트에서 repository 클래스들의 의미를 생각해보자.
@Component
@RequiredArgsConstructor
public class CommentRepository {

	private final CommentMapper commentMapper;

	public void insertComment(final long feedId, final long userId, final CommentRequestDto commentRequestDto) {
		this.commentMapper.insertComment(feedId, userId, commentRequestDto);
	}

	public void insertReply(final ReplyDto replyDto) {
		this.commentMapper.insertReply(replyDto);
	}

	public Optional<Comment> findCommentById(final Long parentId) {
		return this.commentMapper.findCommentById(parentId);
	}

	public List<Comment> comment(final long feedId, final int offset, final int commentPerPage) {
		return this.commentMapper.comment(feedId, offset, commentPerPage);
	}

	public int countParent(final long feedId) {
		return this.commentMapper.countParent(feedId);
	}

	public int maxDepth(final int groupNumber) {
		return this.commentMapper.maxDepth(groupNumber);
	}

	public void updateRefOrder(final int parentOrderResult, final int groupNumber) {
		this.commentMapper.updateRefOrder(parentOrderResult, groupNumber);
	}

	public void updateChild(final Long parentId) {
		this.commentMapper.updateChild(parentId);
	}
}
