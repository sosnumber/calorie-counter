package com.example.caloriecounter.comment.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import com.example.caloriecounter.comment.controller.request.CommentRequestDto;
import com.example.caloriecounter.comment.domain.Comment;
import com.example.caloriecounter.comment.repository.CommentRepository;
import com.example.caloriecounter.exception.CustomException;
import com.example.caloriecounter.feed.service.FeedService;
import com.example.caloriecounter.user.controller.dto.request.LoginForm;
import com.example.caloriecounter.user.controller.dto.request.SignUpForm;
import com.example.caloriecounter.user.service.UserService;
import com.example.caloriecounter.util.StatusEnum;

@SpringBootTest
@Sql("classpath:tableInit.sql")
class CommentServiceTest {

	@Autowired
	private UserService userService;

	@Autowired
	private FeedService feedService;

	@Autowired
	private CommentService commentService;

	@Autowired
	private CommentRepository commentRepository;

	private final SignUpForm alreadySignUpForm = new SignUpForm("mockUser", "이영진", "asdf1234", "dudwls0505@naver.com");
	private final LoginForm alreadyLoginForm = new LoginForm("mockUser", "asdf1234");

	CommentRequestDto commentRequestDto = new CommentRequestDto("댓글1");
	CommentRequestDto replyDto = new CommentRequestDto(2, "답글1", 1L, 1);

	@BeforeEach
	void setup() {
		this.userService.login(alreadyLoginForm);
		this.commentService.deleteAll();
	}

	@Test
	@DisplayName("댓글 작성 성공")
	void comment_insert_success() {
		//given
		commentService.insertComment(1, alreadySignUpForm.getId(), commentRequestDto);
		commentService.insertComment(1, alreadySignUpForm.getId(), commentRequestDto);

		assertAll(
			() -> assertEquals(commentRepository.findById(1L),
				Optional.of(
					new Comment(1, 1, commentRequestDto.getContents(), null, 0,
						alreadySignUpForm.getId(), 1))),
			() -> assertEquals(commentRepository.findById(2L),
				Optional.of(
					new Comment(2, 1, commentRequestDto.getContents(), null, 0,
						alreadySignUpForm.getId(), 2)))
		);
	}

	@Test
	@DisplayName("답글 작성 성공")
	void reply_insert_success() {
		//given
		commentService.insertComment(1, alreadySignUpForm.getId(), commentRequestDto);
		assertDoesNotThrow(() -> commentService.insertReply(1, alreadySignUpForm.getId(), replyDto));

		assertEquals(commentRepository.findById(2L),
			Optional.of(new Comment(2, 1, replyDto.getContents(),
				1L, 1, alreadySignUpForm.getId(), 1, 2, 0)));
	}

	@Test
	@DisplayName("답글 작성 실패 : 부모댓글이 존재하지않는경우")
	void reply_insert_fail() {
		CustomException customException = assertThrows(CustomException.class,
			() -> commentService.insertReply(1, alreadySignUpForm.getId(), replyDto));
		assertThat(StatusEnum.COMMENT_NOT_FOUND).isEqualTo(customException.getStatusEnum());
	}

	@Nested
	@DisplayName("피드가 없는경우")
	class FeedNotExistBlock {

		@BeforeEach
		void setup() {
			feedService.deleteAll();
		}

		@Test
		@DisplayName("댓글 작성 실패 : 피드가 존재하지않음 ")
		void comment_insert_fail() {
			CustomException customException = assertThrows(CustomException.class,
				() -> commentService.insertComment(1, alreadySignUpForm.getId(), commentRequestDto));
			assertThat(StatusEnum.FEED_NOT_FOUND).isEqualTo(customException.getStatusEnum());
		}

		@Test
		@DisplayName("답글 작성 실패 : 피드가 존재하지않음 ")
		void reply_insert_fail() {
			CustomException customException = assertThrows(CustomException.class,
				() -> commentService.insertReply(1, alreadySignUpForm.getId(), commentRequestDto));
			assertThat(StatusEnum.FEED_NOT_FOUND).isEqualTo(customException.getStatusEnum());
		}
	}

}