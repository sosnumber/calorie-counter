package com.example.caloriecounter.feed.controller;

import static com.example.caloriecounter.feed.controller.FeedControllerTest.AUTHORIZATION_BEARER;
import static com.example.caloriecounter.feed.controller.FeedControllerTest.AUTHORIZATION_HEADER;
import static com.example.caloriecounter.user.source.TestUserSource.alreadyLoginForm;
import static com.example.caloriecounter.util.CustomResponse.ERROR;
import static com.example.caloriecounter.util.CustomResponse.SUCCESS;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import com.example.caloriecounter.comment.controller.request.CommentRequestDto;
import com.example.caloriecounter.comment.service.CommentService;
import com.example.caloriecounter.feed.controller.dto.request.FeedDto;
import com.example.caloriecounter.feed.service.FeedService;
import com.example.caloriecounter.user.controller.dto.response.ResponseIssuedToken;
import com.example.caloriecounter.user.service.UserService;
import com.example.caloriecounter.util.StatusEnum;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@Sql("classpath:tableInit.sql")
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

	@BeforeEach
	void setup() {
		this.responseIssuedToken = this.userService.login(alreadyLoginForm);
		this.feedService.deleteAll();
		this.commentService.deleteAll();
	}

	@Test
	@DisplayName("댓글 작성 성공")
	void comment_write_success() throws Exception {
		feedService.write(createFeed("게시글내용1", 1));

		mockMvc.perform(post("/feeds/1/comment")
				.header(AUTHORIZATION_HEADER, AUTHORIZATION_BEARER + responseIssuedToken.accessToken())
				.content(objectMapper.writeValueAsString(createComment("댓글1")))
				.contentType(APPLICATION_JSON))
			.andExpect(jsonPath("result").value(SUCCESS))
			.andExpect(jsonPath("info.commentId").value(1))
			.andExpect(jsonPath("info.contents").value("댓글1"))
			.andDo(print())
			.andExpect(status().isCreated());
	}

	@Test
	@DisplayName("대댓글 작성 성공: 부모댓글이 존재한다")
	void feed_reply_success() throws Exception {
		//given : 부모댓글
		feedService.write(createFeed("게시글내용1", 1));
		final CommentRequestDto comment = createComment("댓글1");
		commentService.insertComment(1, 1, comment);

		mockMvc.perform(post("/feeds/1/comment")
				.header(AUTHORIZATION_HEADER, AUTHORIZATION_BEARER + responseIssuedToken.accessToken())
				.content(objectMapper.writeValueAsString(createReply("답글1", comment.getParentId())))
				.contentType(APPLICATION_JSON))
			.andExpect(jsonPath("result").value(SUCCESS))
			//todo 단지 ID값 존재하는지확인한다고? 너무이상한데?
			.andExpect(jsonPath("info.commentId").exists())
			.andExpect(jsonPath("info.contents").value("답글1"))
			.andDo(print())
			.andExpect(status().isCreated());
	}

	@Test
	@DisplayName("대댓글 작성 실패: 부모댓글이 존재하지않음")
	void feed_reply_fail2() throws Exception {
		feedService.write(createFeed("게시글내용1", 1));

		mockMvc.perform(post("/feeds/1/comment")
				.header(AUTHORIZATION_HEADER, AUTHORIZATION_BEARER + responseIssuedToken.accessToken())
				// todo 부모ID를 가진 댓글이 없는 가정인데, 부모ID에 어떤값을 가정해서 줘야할까?
				.content(objectMapper.writeValueAsString(createReply("부모없는답글", Long.MAX_VALUE)))
				.contentType(APPLICATION_JSON))
			.andExpect(jsonPath("result").value(ERROR))
			.andExpect(jsonPath("errorMessage").value(StatusEnum.COMMENT_NOT_FOUND.getMessage()))
			.andDo(print())
			.andExpect(status().isNotFound());
	}

	@Test
	@DisplayName("댓글 작성 실패: 피드가 존재하지않음")
	void feed_comment_fail() throws Exception {
		mockMvc.perform(post("/feeds/1/comment")
				.header(AUTHORIZATION_HEADER, AUTHORIZATION_BEARER + responseIssuedToken.accessToken())
				.content(objectMapper.writeValueAsString(createComment("댓글1")))
				.contentType(APPLICATION_JSON))
			.andExpect(jsonPath("result").value(ERROR))
			.andExpect(jsonPath("errorMessage").value(StatusEnum.FEED_NOT_FOUND.getMessage()))
			.andDo(print())
			.andExpect(status().isNotFound());
	}

	@Test
	@DisplayName("대댓글 작성 실패: 피드가 존재하지않음")
	void feed_reply_fail() throws Exception {
		final CommentRequestDto comment = createComment("댓글1");

		mockMvc.perform(post("/feeds/1/comment")
				.header(AUTHORIZATION_HEADER, AUTHORIZATION_BEARER + responseIssuedToken.accessToken())
				.content(objectMapper.writeValueAsString(createReply("답글1", comment.getParentId())))
				.contentType(APPLICATION_JSON))
			.andExpect(jsonPath("result").value(ERROR))
			.andExpect(jsonPath("errorMessage").value(StatusEnum.FEED_NOT_FOUND.getMessage()))
			.andDo(print())
			.andExpect(status().isNotFound());
	}

	private CommentRequestDto createComment(final String contents) {
		return new CommentRequestDto(contents);
	}

	private CommentRequestDto createReply(final String contents, final Long parentId) {
		return new CommentRequestDto(contents, parentId);
	}

	private FeedDto createFeed(final String contents, final long userId) {
		return new FeedDto(contents, userId);
	}
}
