package com.example.caloriecounter.feed.controller;

import static com.example.caloriecounter.user.source.TestUserSource.alreadyLoginForm;
import static com.example.caloriecounter.util.CustomResponse.ERROR;
import static com.example.caloriecounter.util.CustomResponse.SUCCESS;
import static org.hamcrest.Matchers.is;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.hamcrest.core.IsNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.web.multipart.MultipartFile;

import com.example.caloriecounter.comment.controller.request.CommentRequestDto;
import com.example.caloriecounter.comment.service.CommentService;
import com.example.caloriecounter.feed.controller.dto.request.FeedDto;
import com.example.caloriecounter.feed.service.FeedService;
import com.example.caloriecounter.like.service.LikeService;
import com.example.caloriecounter.photo.service.PhotoService;
import com.example.caloriecounter.user.controller.dto.response.ResponseIssuedToken;
import com.example.caloriecounter.user.service.UserService;
import com.example.caloriecounter.util.StatusEnum;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
public class FeedControllerTest {
	public static final String FEED_WRITE_URL = "/feeds";
	public static final String AUTHORIZATION_HEADER = "Authorization";
	public static final String AUTHORIZATION_BEARER = "Bearer ";

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private UserService userService;

	@Autowired
	private FeedService feedService;

	@Autowired
	private PhotoService photoService;

	@Autowired
	private LikeService likeService;

	@Autowired
	private CommentService commentService;

	@Autowired
	private ObjectMapper objectMapper;

	private ResponseIssuedToken responseIssuedToken;

	private final MockMultipartFile image1 = new MockMultipartFile("photos", "photos", "image/jpeg",
		"photos".getBytes());
	private final MockMultipartFile image2 = new MockMultipartFile("photos", "photos2", "image/jpeg",
		"photos2".getBytes());

	private final FeedDto notWriteFeed = new FeedDto("게시글내용1", List.of(this.image1, this.image2), 1);

	record FeedTestDto(String contents) {
	}

	private MockMultipartFile contents;

	@BeforeEach
	void setup() throws JsonProcessingException {
		this.responseIssuedToken = this.userService.login(alreadyLoginForm);

		contents = new MockMultipartFile("contents", "contents", "",
			objectMapper.writeValueAsString(new FeedTestDto("닭가슴살먹었다")).getBytes());

		feedService.deleteAll();
		commentService.deleteAll();
		photoService.deleteAll();
		likeService.deleteAll();
	}

	@Test
	@DisplayName("피드 수정 성공: 수정내용에 image, contents 둘다 있음")
	void feed_update_test() throws Exception {
		feedService.write(createFeed("게시글내용1", 1));

		MockMultipartHttpServletRequestBuilder builder = multipart("/feeds/1");
		builder.with(request -> {
			request.setMethod("PUT");
			return request;
		});

		mockMvc.perform(builder
				.file(contents)
				.file(image1)
				.file(image2)
				.header(AUTHORIZATION_HEADER, AUTHORIZATION_BEARER + responseIssuedToken.accessToken())
				.contentType(MULTIPART_FORM_DATA))
			.andDo(print())
			.andExpect(status().isCreated());
	}

	@Test
	@DisplayName("피드 수정 성공: 수정내용에 contents만 있음")
	void feed_update_test2() throws Exception {
		feedService.write(createFeed("게시글내용1", 1));

		MockMultipartHttpServletRequestBuilder builder = multipart("/feeds/1");
		builder.with(request -> {
			request.setMethod("PUT");
			return request;
		});

		mockMvc.perform(builder
				.file(contents)
				.header(AUTHORIZATION_HEADER, AUTHORIZATION_BEARER + responseIssuedToken.accessToken())
				.contentType(MULTIPART_FORM_DATA))
			.andDo(print())
			.andExpect(status().isCreated());
	}

	@Test
	@DisplayName("피드 수정 성공: 수정내용에 image만 있음")
	void feed_update_test5() throws Exception {
		feedService.write(createFeed("게시글내용1", 1));

		MockMultipartHttpServletRequestBuilder builder = multipart("/feeds/1");
		builder.with(request -> {
			request.setMethod("PUT");
			return request;
		});

		mockMvc.perform(builder
				.file(image1)
				.file(image2)
				.header(AUTHORIZATION_HEADER, AUTHORIZATION_BEARER + responseIssuedToken.accessToken())
				.contentType(MULTIPART_FORM_DATA))
			.andDo(print())
			.andExpect(status().isCreated());
	}

	@Test
	@DisplayName("피드 수정 실패 : 수정내용에 내용, 이미지 모두 비어있는경우")
	void feed_update_test4() throws Exception {
		feedService.write(createFeed("게시글내용1", 1));

		MockMultipartHttpServletRequestBuilder builder = multipart("/feeds/1");
		builder.with(request -> {
			request.setMethod("PUT");
			return request;
		});

		mockMvc.perform(builder
				.header(AUTHORIZATION_HEADER, AUTHORIZATION_BEARER + responseIssuedToken.accessToken())
				.contentType(MULTIPART_FORM_DATA))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("result").value(ERROR))
			.andExpect(jsonPath("errorMessage").value(StatusEnum.EMPTY_FEED.getMessage()))
			.andDo(print());
	}

	@Test
	@DisplayName("피드 삭제 성공 : 로그인 한 유저")
	void feed_delete_success() throws Exception {
		feedService.write(createFeed("게시글내용1", 1));

		mockMvc.perform(delete("/feeds/1")
				.header(AUTHORIZATION_HEADER, AUTHORIZATION_BEARER + responseIssuedToken.accessToken())
				.contentType(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("피드 조회 성공: cursor가 0보다 작으면 maxId값을 준다.")
	void feed_read_fail() throws Exception {
		final FeedDto feedWithContents = createFeed("게시글내용1", 1);
		final FeedDto feedWithPhoto = createFeed(List.of(this.image1, this.image2), 1);
		feedService.write(feedWithContents);
		feedService.write(feedWithPhoto);
		feedService.write(feedWithPhoto);

		mockMvc.perform(get("/feeds?cursorNo=-1&displayPerPage=3")
				.header(AUTHORIZATION_HEADER, AUTHORIZATION_BEARER + responseIssuedToken.accessToken())
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.info.length()", is(3)))
			.andExpect(jsonPath("$.info[0].contents").value(feedWithPhoto.getContents()))
			.andExpect(jsonPath("$.info[1].contents").value(feedWithPhoto.getContents()))
			.andExpect(jsonPath("$.info[2].contents").value(feedWithContents.getContents()))
			.andDo(print())
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("피드 조회 성공: 좋아요개수,좋아요상태, 사진, 글내용,댓글, 대댓글")
	void feed_read_success() throws Exception {
		final FeedDto feedWithContents = createFeed("게시글내용1", 1);
		final FeedDto feedWithPhoto = createFeed(List.of(this.image1, this.image2), 1);
		feedService.write(feedWithContents);
		feedService.write(feedWithPhoto);
		feedService.write(feedWithPhoto);

		likeService.like(feedWithContents.getId(), 1);
		commentService.insertComment(feedWithContents.getId(), 1, new CommentRequestDto("댓글1"));
		commentService.insertComment(feedWithContents.getId(), 1, new CommentRequestDto("댓글2"));
		commentService.insertComment(feedWithContents.getId(), 1, new CommentRequestDto("댓글3"));
		commentService.insertReply(feedWithContents.getId(), 1,
			new CommentRequestDto(4, "댓글1의답글1", 1L, 1));
		commentService.insertReply(feedWithContents.getId(), 1,
			new CommentRequestDto(5, "댓글1의답글2", 1L, 1));
		commentService.insertReply(feedWithContents.getId(), 1,
			new CommentRequestDto(6, "댓글1의답글1의답글1", 4L, 1));
		commentService.insertReply(feedWithContents.getId(), 1,
			new CommentRequestDto(7, "댓글2의답글1", 2L, 2));

		mockMvc.perform(get("/feeds?cursorNo=50&displayPerPage=3&commentPageNum=1&commentPerPage=30")
				.header(AUTHORIZATION_HEADER, AUTHORIZATION_BEARER + responseIssuedToken.accessToken())
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("result").value(SUCCESS))
			.andExpect(jsonPath("$.info.length()", is(3)))

			.andExpect(jsonPath("$.info[0].contents").value(feedWithPhoto.getContents()))
			.andExpect(jsonPath("$.info[1].contents").value(feedWithPhoto.getContents()))
			.andExpect(jsonPath("$.info[2].contents").value(feedWithContents.getContents()))

			.andExpect(jsonPath("$.info[0].photos.length()", is(2)))
			.andExpect(jsonPath("$.info[1].photos.length()", is(2)))
			.andExpect(jsonPath("$.info[2].photos.length()", is(0)))

			.andExpect(jsonPath("$.info[0].likeCount").value(0))
			.andExpect(jsonPath("$.info[1].likeCount").value(0))
			.andExpect(jsonPath("$.info[2].likeCount").value(1))

			.andExpect(jsonPath("$.info[0].likeStatus").value(IsNull.nullValue()))
			.andExpect(jsonPath("$.info[1].likeStatus").value(IsNull.nullValue()))
			.andExpect(jsonPath("$.info[2].likeStatus").value("ACTIVATE"))

			.andExpect(jsonPath("$.info[0].comments.length()", is(0)))
			.andExpect(jsonPath("$.info[1].comments.length()", is(0)))

			.andExpect(jsonPath("$.info[2].comments[0].contents").value("댓글1"))
			.andExpect(jsonPath("$.info[2].comments[0].depth").value(0))
			.andExpect(jsonPath("$.info[2].comments[0].groupRefOrder").value(1))
			.andExpect(jsonPath("$.info[2].comments[0].childNumber").value(2))

			.andExpect(jsonPath("$.info[2].comments[1].contents").value("댓글1의답글1"))
			.andExpect(jsonPath("$.info[2].comments[1].depth").value(1))
			.andExpect(jsonPath("$.info[2].comments[1].groupRefOrder").value(2))
			.andExpect(jsonPath("$.info[2].comments[1].childNumber").value(1))

			.andExpect(jsonPath("$.info[2].comments[2].contents").value("댓글1의답글1의답글1"))
			.andExpect(jsonPath("$.info[2].comments[2].depth").value(2))
			.andExpect(jsonPath("$.info[2].comments[2].groupRefOrder").value(3))
			.andExpect(jsonPath("$.info[2].comments[2].childNumber").value(0))

			.andExpect(jsonPath("$.info[2].comments[3].contents").value("댓글1의답글2"))
			.andExpect(jsonPath("$.info[2].comments[3].depth").value(1))
			.andExpect(jsonPath("$.info[2].comments[3].groupRefOrder").value(4))
			.andExpect(jsonPath("$.info[2].comments[3].childNumber").value(0))

			.andExpect(jsonPath("$.info[2].comments[4].contents").value("댓글2"))
			.andExpect(jsonPath("$.info[2].comments[4].depth").value(0))
			.andExpect(jsonPath("$.info[2].comments[4].groupRefOrder").value(1))
			.andExpect(jsonPath("$.info[2].comments[4].childNumber").value(1))

			.andExpect(jsonPath("$.info[2].comments[5].contents").value("댓글2의답글1"))
			.andExpect(jsonPath("$.info[2].comments[5].depth").value(1))
			.andExpect(jsonPath("$.info[2].comments[5].groupRefOrder").value(2))
			.andExpect(jsonPath("$.info[2].comments[5].childNumber").value(0))

			.andExpect(jsonPath("$.info[2].comments[6].contents").value("댓글3"))
			.andExpect(jsonPath("$.info[2].comments[6].depth").value(0))
			.andExpect(jsonPath("$.info[2].comments[6].groupRefOrder").value(1))
			.andExpect(jsonPath("$.info[2].comments[6].childNumber").value(0))
			.andDo(print())
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("피드 작성 성공 : 이미지, 내용 둘다 있는경우")
	void feed_write_test() throws Exception {
		mockMvc.perform(multipart(FEED_WRITE_URL)
				.file(contents)
				.file(image1)
				.file(image2)
				.header(AUTHORIZATION_HEADER, AUTHORIZATION_BEARER + responseIssuedToken.accessToken())
				.contentType(MULTIPART_FORM_DATA))
			.andDo(print())
			.andExpect(status().isCreated());
	}

	@Test
	@DisplayName("피드 작성 성공 : 이미지만 있는경우")
	void feed_write_test2() throws Exception {
		mockMvc.perform(multipart(FEED_WRITE_URL)
				.file(image1)
				.file(image2)
				.header(AUTHORIZATION_HEADER, AUTHORIZATION_BEARER + responseIssuedToken.accessToken())
				.contentType(MULTIPART_FORM_DATA))
			.andDo(print())
			.andExpect(status().isCreated());
	}

	@Test
	@DisplayName("피드 작성 성공 : 내용만 있는경우")
	void feed_write_test3() throws Exception {
		mockMvc.perform(multipart(FEED_WRITE_URL)
				.file(contents)
				.header(AUTHORIZATION_HEADER, AUTHORIZATION_BEARER + responseIssuedToken.accessToken())
				.contentType(MULTIPART_FORM_DATA))
			.andDo(print())
			.andExpect(status().isCreated());
	}

	@Test
	@DisplayName("피드 작성 실패 : 내용, 이미지 모두 비어있는경우")
	void feed_write_test4() throws Exception {
		mockMvc.perform(multipart(FEED_WRITE_URL)
				.header(AUTHORIZATION_HEADER, AUTHORIZATION_BEARER + responseIssuedToken.accessToken())
				.contentType(MULTIPART_FORM_DATA))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("result").value(ERROR))
			.andExpect(jsonPath("errorMessage").value(StatusEnum.EMPTY_FEED.getMessage()))
			.andDo(print());
	}

	@Test
	@DisplayName("피드가 하나도 없는경우 maxCursor는 0을반환한다")
	void maxCursor() throws Exception {
		mockMvc.perform(get("/feeds?cursorNo=-1&displayPerPage=3")
				.header(AUTHORIZATION_HEADER, AUTHORIZATION_BEARER + responseIssuedToken.accessToken())
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.info.length()", is(0)))
			.andDo(print())
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("피드 수정 실패 : 피드가 존재하지않는다")
	void feed_update_test3() throws Exception {
		MockMultipartHttpServletRequestBuilder builder = multipart("/feeds/" + notWriteFeed.getId());
		builder.with(request -> {
			request.setMethod("PUT");
			return request;
		});

		mockMvc.perform(builder
				.file(image1)
				.file(image2)
				.header(AUTHORIZATION_HEADER, AUTHORIZATION_BEARER + responseIssuedToken.accessToken())
				.contentType(MULTIPART_FORM_DATA))
			.andDo(print())
			.andExpect(status().isNotFound());
	}

	@Test
	@DisplayName("피드 삭제 실패: 피드가 존재하지 않는다")
	void feed_delete_fail3() throws Exception {
		mockMvc.perform(delete("/feeds/" + notWriteFeed.getId())
				.header(AUTHORIZATION_HEADER, AUTHORIZATION_BEARER + responseIssuedToken.accessToken())
				.contentType(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isNotFound());
	}

	private FeedDto createFeed(final List<MultipartFile> images, final long userId) {
		return new FeedDto(images, userId);
	}

	private FeedDto createFeed(final String contents, final long userId) {
		return new FeedDto(contents, userId);
	}
}