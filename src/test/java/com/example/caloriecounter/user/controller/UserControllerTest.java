package com.example.caloriecounter.user.controller;

import static com.example.caloriecounter.util.CustomResponse.ERROR;
import static com.example.caloriecounter.util.CustomResponse.SUCCESS;
import static org.hamcrest.Matchers.is;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import com.example.caloriecounter.user.controller.dto.request.LoginForm;
import com.example.caloriecounter.user.controller.dto.request.SignUpForm;
import com.example.caloriecounter.user.service.UserService;
import com.example.caloriecounter.util.StatusEnum;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@Sql("classpath:tableInit.sql")
class UserControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private UserService userService;

	@Autowired
	private ObjectMapper objectMapper;

	private final SignUpForm rightSignUpForm = new SignUpForm(
		"joinUserId",
		"회원가입용유저",
		"asdf1234",
		"dudwls0505@nate.com"
	);

	private final SignUpForm alreadySignUpForm = new SignUpForm(
		"mockUser",
		"이영진",
		"asdf1234",
		"dudwls0505@naver.com"
	);

	private final LoginForm alreadyLoginForm = new LoginForm(
		alreadySignUpForm.getUserId(),
		alreadySignUpForm.getUserPassword()
	);

	private final SignUpForm wrongInputForm = new SignUpForm(
		"wrongUserId",
		"잘못된유저",
		"1",
		"2"
	);

	@Test
	@DisplayName("회원가입 성공")
	void signUp_success() throws Exception {
		mockMvc.perform(post("/users")
				.contentType(APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(rightSignUpForm)))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("result").value(SUCCESS))
			//todo 꼭 ID값까지 확인해야하는것인가?
			// .andExpect(jsonPath("$.info.id").value(rightSignUpForm.getId()))
			.andExpect(jsonPath("$.info.userName").value(rightSignUpForm.getUserName()))
			.andExpect(jsonPath("$.info.email").value(rightSignUpForm.getEmail()))
			.andDo(print());
	}

	@Test
	@DisplayName("회원가입 실패 : 입력하지 않은 필드값 존재")
	void signUp_wrong_input_fail() throws Exception {
		mockMvc.perform(post("/users")
				.contentType(APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(wrongInputForm)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("result").value(ERROR))
			.andExpect(jsonPath("errorMessage").value(StatusEnum.INVALID_FORM_INPUT.getMessage()))
			.andDo(print());
	}

	@Test
	@DisplayName("회원가입 실패 : 중복 회원가입 시도시 예외를 던진다.")
	void signUp_AlreadyExistUserIdException_fail() throws Exception {
		mockMvc.perform(post("/users")
				.contentType(APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(alreadySignUpForm)))
			.andDo(print())
			.andExpect(jsonPath("result").value(ERROR))
			.andExpect(jsonPath("errorMessage").value(StatusEnum.DUPLICATED_ID.getMessage()))
			.andExpect(status().isConflict());
	}

	private final LoginForm wrongLoginForm = new LoginForm(
		"join_wrongUserId",
		null
	);
	private final LoginForm wrongPasswordLoginForm = new LoginForm(
		alreadySignUpForm.getUserId(),
		"wrongPassword234"
	);
	private final LoginForm notExistIdLoginForm = new LoginForm(
		"notExistId",
		rightSignUpForm.getUserPassword()
	);

	@Test
	@DisplayName("로그인 성공")
	void login_success_test() throws Exception {
		mockMvc.perform(post("/login")
				.contentType(APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(this.alreadyLoginForm)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("result").value(SUCCESS))
			.andExpect(jsonPath("$.info.length()", is(3)))
			.andDo(print());
	}

	@Test
	@DisplayName("로그인 실패: 입력하지 않은 필드값 존재")
	void login_fail_test() throws Exception {
		mockMvc.perform(post("/login")
				.contentType(APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(this.wrongLoginForm)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("result").value(ERROR))
			.andExpect(jsonPath("errorMessage").value(StatusEnum.INVALID_FORM_INPUT.getMessage()))
			.andDo(print());
	}

	@Test
	@DisplayName("로그인 실패: 존재하지 않는 ID")
	void login_fail_test2() throws Exception {
		mockMvc.perform(post("/login")
				.contentType(APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(this.notExistIdLoginForm)))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("result").value(ERROR))
			.andExpect(jsonPath("errorMessage").value(StatusEnum.USER_NOT_FOUND.getMessage()))
			.andDo(print());
	}

	@Test
	@DisplayName("로그인 실패: 비밀번호 일치하지 않음")
	void login_fail_test3() throws Exception {
		mockMvc.perform(post("/login")
				.contentType(APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(this.wrongPasswordLoginForm)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("result").value(ERROR))
			.andExpect(jsonPath("errorMessage").value(StatusEnum.PASSWORD_NOT_MATCH.getMessage()))
			.andDo(print());
	}
}
