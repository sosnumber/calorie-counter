package com.example.caloriecounter.feed.controller;

import static com.example.caloriecounter.feed.controller.FeedControllerTest.*;
import static com.example.caloriecounter.util.CustomResponse.*;
import static org.springframework.http.MediaType.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.example.caloriecounter.comment.controller.request.CommentRequestDto;
import com.example.caloriecounter.comment.service.CommentService;
import com.example.caloriecounter.feed.controller.dto.request.FeedDto;
import com.example.caloriecounter.feed.service.FeedService;
import com.example.caloriecounter.user.controller.dto.request.LoginForm;
import com.example.caloriecounter.user.controller.dto.request.SignUpForm;
import com.example.caloriecounter.user.controller.dto.response.ResponseIssuedToken;
import com.example.caloriecounter.user.service.UserService;
import com.example.caloriecounter.util.StatusEnum;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class CommentControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private UserService userService;

	@Autowired
	private FeedService feedService;

	@Autowired
	private CommentService commentService;

	@Autowired
	private ObjectMapper objectMapper;

	private ResponseIssuedToken responseIssuedToken;

	private final SignUpForm wrongSignUpForm = new SignUpForm("wrongUser", "김영진", "asdf1234", "dudwls0505@nate.com");
	private final SignUpForm alreadySignUpForm = new SignUpForm("mockUser", "이영진", "asdf1234", "dudwls0505@naver.com");
	private final LoginForm alreadyLoginForm = new LoginForm("mockUser", "asdf1234");
	LoginForm wrongLoginForm = new LoginForm(wrongSignUpForm.getUserId(), wrongSignUpForm.getUserPassword());

	FeedDto feedWithContents = new FeedDto("게시글내용1", alreadySignUpForm.getId());

	CommentRequestDto comment = new CommentRequestDto("댓글1");
	CommentRequestDto replyDto = new CommentRequestDto("답글1", comment.getParentId());
	// todo 부모ID를 가진 댓글이 없는 가정인데, 부모ID에 어떤값을 가정해서 줘야할까?
	CommentRequestDto replyWithNotParent = new CommentRequestDto("답글1", Long.MAX_VALUE);

	@BeforeEach
	void setup() {
		System.out.println(alreadyLoginForm);
		this.responseIssuedToken = this.userService.login(alreadyLoginForm);
	}

	@Nested
	@DisplayName("피드가 존재하는 경우")
	class FeedExistBlock {

		@BeforeEach
		void setup() {
			feedService.write(feedWithContents);
		}

		@Test
		@DisplayName("댓글 작성 성공")
		void feed_comment_success() throws Exception {
			mockMvc.perform(post("/feeds/" + feedWithContents.getId() + "/comment")
					.header(AUTHORIZATION_HEADER, AUTHORIZATION_BEARER + responseIssuedToken.accessToken())
					.content(objectMapper.writeValueAsString(comment))
					.contentType(APPLICATION_JSON))
				.andExpect(jsonPath("result").value(SUCCESS))
				.andExpect(jsonPath("info.commentId").exists())
				.andExpect(jsonPath("info.contents").value(comment.getContents()))
				.andDo(print())
				.andExpect(status().isCreated());
		}

		@Test
		@DisplayName("대댓글 작성 성공: 부모댓글이 존재한다")
		void feed_reply_success() throws Exception {
			//given : 부모댓글
			commentService.insertComment(feedWithContents.getId(), alreadySignUpForm.getId(), comment);

			mockMvc.perform(post("/feeds/" + feedWithContents.getId() + "/comment")
					.header(AUTHORIZATION_HEADER, AUTHORIZATION_BEARER + responseIssuedToken.accessToken())
					.content(
						objectMapper.writeValueAsString(replyDto))
					.contentType(APPLICATION_JSON))
				.andExpect(jsonPath("result").value(SUCCESS))
				.andExpect(jsonPath("info.commentId").exists())
				.andExpect(jsonPath("info.contents").value(replyDto.getContents()))
				.andDo(print())
				.andExpect(status().isCreated());
		}

		@Test
		@DisplayName("대댓글 작성 실패: 부모댓글이 존재하지않음")
		void feed_reply_fail2() throws Exception {
			mockMvc.perform(post("/feeds/" + feedWithContents.getId() + "/comment")
					.header(AUTHORIZATION_HEADER, AUTHORIZATION_BEARER + responseIssuedToken.accessToken())
					.content(objectMapper.writeValueAsString(replyWithNotParent))
					.contentType(APPLICATION_JSON))
				.andExpect(jsonPath("result").value(ERROR))
				.andExpect(jsonPath("errorMessage").value(StatusEnum.COMMENT_NOT_FOUND.getMessage()))
				.andDo(print())
				.andExpect(status().isNotFound());
		}

	}

	@Test
	@DisplayName("댓글 작성 실패: 피드가 존재하지않음")
	void feed_comment_fail() throws Exception {
		mockMvc.perform(post("/feeds/" + feedWithContents.getId() + "/comment")
				.header(AUTHORIZATION_HEADER, AUTHORIZATION_BEARER + responseIssuedToken.accessToken())
				.content(objectMapper.writeValueAsString(comment))
				.contentType(APPLICATION_JSON))
			.andExpect(jsonPath("result").value(ERROR))
			.andExpect(jsonPath("errorMessage").value(StatusEnum.FEED_NOT_FOUND.getMessage()))
			.andDo(print())
			.andExpect(status().isNotFound());
	}

	@Test
	@DisplayName("대댓글 작성 실패: 피드가 존재하지않음")
	void feed_reply_fail() throws Exception {
		mockMvc.perform(post("/feeds/" + feedWithContents.getId() + "/comment")
				.header(AUTHORIZATION_HEADER, AUTHORIZATION_BEARER + responseIssuedToken.accessToken())
				.content(objectMapper.writeValueAsString(replyDto))
				.contentType(APPLICATION_JSON))
			.andExpect(jsonPath("result").value(ERROR))
			.andExpect(jsonPath("errorMessage").value(StatusEnum.FEED_NOT_FOUND.getMessage()))
			.andDo(print())
			.andExpect(status().isNotFound());
	}
}
