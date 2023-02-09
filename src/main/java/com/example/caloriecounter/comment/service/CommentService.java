package com.example.caloriecounter.comment.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.caloriecounter.comment.controller.request.CommentRequestDto;
import com.example.caloriecounter.comment.controller.request.ReplyDto;
import com.example.caloriecounter.comment.domain.Comment;
import com.example.caloriecounter.comment.repository.CommentRepository;
import com.example.caloriecounter.exception.CustomException;
import com.example.caloriecounter.feed.domain.Feed;
import com.example.caloriecounter.feed.repository.FeedRepository;
import com.example.caloriecounter.util.StatusEnum;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CommentService {

	private final FeedRepository feedRepository;
	private final CommentRepository commentRepository;

	@Transactional
	public void insertComment(final long feedId, final long userId, final CommentRequestDto commentRequestDto) {
		final Feed feed = this.feedRepository.findByFeedId(feedId)
			.orElseThrow(() -> new CustomException(StatusEnum.FEED_NOT_FOUND, String.format("%s not exist", feedId)));

		// 부모댓글의 group번호는 해당피드에 존재하는 부모댓글의 개수 +1로 설정
		this.commentRepository.insertComment(feedId, userId,
			new CommentRequestDto(commentRequestDto.getContents(), this.commentRepository.countParent(feedId) + 1));
	}

	public List<Comment> comment(final long feedId, final int offset, final int commentPerPage) {
		return this.commentRepository.comment(feedId, offset, commentPerPage);
	}

	@Transactional
	public void insertReply(final long feedId, final long userId, final CommentRequestDto commentRequestDto) {
		final Feed feed = this.feedRepository.findByFeedId(feedId)
			.orElseThrow(() -> new CustomException(StatusEnum.FEED_NOT_FOUND, String.format("%s not exist", feedId)));

		final Comment parentComment = this.commentRepository.findById(commentRequestDto.getParentId())
			.orElseThrow(
				() -> new CustomException(StatusEnum.COMMENT_NOT_FOUND,
					String.format("%s not exist", commentRequestDto.getParentId())));

		this.commentRepository.insertReply(
			new ReplyDto(feedId, userId, commentRequestDto.getContents(), commentRequestDto.getParentId(),
				parentComment.depth() + 1,
				parentComment.groupNumber(),
				referenceOrder(this.commentRepository.maxDepth(parentComment.groupNumber()), parentComment)));

		//자식댓글의 개수 업데이트
		this.commentRepository.updateChild(commentRequestDto.getParentId());
	}

	private int referenceOrder(final int maxDepth, final Comment parentComment) {
		if (parentComment.depth() + 1 == maxDepth) {
			// 그룹내의 댓글중 (부모댓글의 그룹내의 순서 + 자식댓글의개수)보다 큰  댓글들의 탐색순서는 모두 +1 해준다
			final int parentOrderResult = parentComment.groupRefOrder() + parentComment.childNumber();
			this.commentRepository.updateRefOrder(parentOrderResult, parentComment.groupNumber());
			return parentOrderResult + 1;
		} else if (parentComment.depth() + 1 > maxDepth) {
			// 그룹내의 댓글중 부모댓글의 그룹내의 순서보다 큰  댓글들의 탐색순서는 모두 +1 해준다
			this.commentRepository.updateRefOrder(parentComment.groupRefOrder(), parentComment.groupNumber());
			return parentComment.groupRefOrder() + 1;
		} else if (parentComment.depth() + 1 < maxDepth) {
			return parentComment.childNumber() + 1;
		}

		throw new RuntimeException("답글 작성 오류");
	}

	public void deleteAll() {
		this.commentRepository.deleteAll();
	}
}
