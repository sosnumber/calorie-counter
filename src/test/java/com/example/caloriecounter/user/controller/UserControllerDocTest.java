package com.example.caloriecounter.user.controller;

import static com.example.caloriecounter.feed.controller.FeedControllerTest.AUTHORIZATION_BEARER;
import static com.example.caloriecounter.feed.controller.FeedControllerTest.AUTHORIZATION_HEADER;
import static com.example.caloriecounter.user.source.TestUserSource.alreadyLoginForm;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.test.web.servlet.MockMvc;

import com.example.caloriecounter.user.controller.dto.response.ResponseIssuedToken;
import com.example.caloriecounter.user.service.UserService;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs(uriScheme = "https", uriHost = "calorie.counter.com", uriPort = 443)
@ExtendWith(RestDocumentationExtension.class)
class UserControllerDocTest {

	@Autowired
	private MockMvc mockMvc;

	private ResponseIssuedToken responseIssuedToken;

	@Autowired
	private UserService userService;

	@BeforeEach
	void setUp() {
		this.responseIssuedToken = this.userService.login(alreadyLoginForm);
	}

	@Test
	@DisplayName("피드 조회 테스트 ")
	void test1() throws Exception {
		FieldDescriptor[] comment = new FieldDescriptor[] {};

		FieldDescriptor[] feed = new FieldDescriptor[] {
			fieldWithPath("id").description("피드 ID"),
			fieldWithPath("contents").description("피드 글내용"),
			fieldWithPath("writeDate").description("피드 글작성날짜"),
			fieldWithPath("userId").description("피드작성 유저ID"),
			fieldWithPath("photos").description("피드 사진"),
			fieldWithPath("likeCount").description("피드 사진"),
			fieldWithPath("likeStatus").description("피드 사진")};

		this.mockMvc.perform(get("/feeds?cursorNo=-1&displayPerPage=3")
				.header(AUTHORIZATION_HEADER, AUTHORIZATION_BEARER + responseIssuedToken.accessToken())
				.accept(APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isOk())
			.andDo(document("index", requestParameters(
					parameterWithName("cursorNo").description("피드ID의 커서번호"),
					parameterWithName("displayPerPage").description("페이지당 보여줄 피드개수")
				),

				responseFields(
					fieldWithPath("result").description("성공/실패여부"),
					fieldWithPath("info[]").description("피드 리스트들"),
					fieldWithPath("info[].id").description("피드 ID"),
					fieldWithPath("info[].contents").description("피드 내용"),
					fieldWithPath("info[].writeDate").description("피드 작성날짜"),
					fieldWithPath("info[].userId").description("피드작성 유저ID"),
					fieldWithPath("info[].photos").description("피드 사진"),
					fieldWithPath("info[].likeCount").description("좋아요 개수"),
					fieldWithPath("info[].likeStatus").description("좋아요 상태(활성화 / 비활성화)"),
					fieldWithPath("info[].comments").description("댓글")
				)
			));
	}
}
