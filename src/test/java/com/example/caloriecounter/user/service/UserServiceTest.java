package com.example.caloriecounter.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.caloriecounter.exception.CustomException;
import com.example.caloriecounter.user.controller.dto.request.LoginForm;
import com.example.caloriecounter.user.controller.dto.request.SignUpForm;
import com.example.caloriecounter.user.controller.dto.response.ResponseIssuedToken;
import com.example.caloriecounter.user.domain.User;
import com.example.caloriecounter.user.domain.UserStatus;
import com.example.caloriecounter.user.domain.UserType;
import com.example.caloriecounter.user.repository.UserRepository;
import com.example.caloriecounter.util.PasswordEncrypt;
import com.example.caloriecounter.util.StatusEnum;
import com.example.caloriecounter.util.TokenService;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

	@InjectMocks
	private UserService userService;

	@Mock
	private UserRepository userRepository;

	@Mock
	private UserCacheService userCacheService;

	@Mock
	private PasswordEncrypt passwordEncrypt;

	@Mock
	private TokenService tokenService;

	@ParameterizedTest
	@MethodSource("com.example.caloriecounter.user.source.TestUserSource#일반사용자_1가지_정보제공자_1가지")
	@DisplayName("회원가입 실패 : 중복된 아이디")
	void signUp_existEmail_fail(final SignUpForm signUpForm) {
		//given
		given(userRepository.hasDuplicatedId(signUpForm.getUserId())).willReturn(true);

		//then
		CustomException customException = assertThrows(CustomException.class, () -> userService.signUp(signUpForm));
		assertThat(StatusEnum.DUPLICATED_ID).isEqualTo(customException.getStatusEnum());
		verify(userRepository, never()).signUp(signUpForm);
	}

	@ParameterizedTest
	@MethodSource("com.example.caloriecounter.user.source.TestUserSource#일반사용자_1가지_정보제공자_1가지")
	@DisplayName("회원가입 성공")
	void signUp_success(final SignUpForm signUpForm) {
		//given
		given(userRepository.hasDuplicatedId(signUpForm.getUserId())).willReturn(false);

		//then
		assertDoesNotThrow(() -> userService.signUp(signUpForm));
		verify(userRepository).signUp(signUpForm);
	}

	@Test
	@DisplayName("로그인 성공 : redis에 유저를 캐시한다.")
	void login_success3() {
		// given
		LoginForm loginForm = makeLoginForm("testId", "asdf123456");

		given(userService.findByUserId(loginForm.userId())).willReturn(Optional.of(makeUser()));
		given(passwordEncrypt.isMatch(loginForm.userPassword(),
			passwordEncrypt.encrypt(loginForm.userPassword()))).willReturn(true);

		given(tokenService.issue(loginForm.userId())).willReturn(
			new ResponseIssuedToken("access_token", "Bearer", 100000));

		//when
		assertThat(userCacheService.getUser(loginForm.userId())).isEmpty();
		ResponseIssuedToken login = userService.login(loginForm);

		//then
		assertThat(login.accessToken()).isNotNull();
		assertThat(userCacheService.getUser(loginForm.userId())).isNotNull();
	}

	@Test
	@DisplayName("로그인 실패: 존재하지 않는 ID")
	void login_fail() {
		// given
		LoginForm loginForm = makeLoginForm("nonExistUserId", "asdf123456");
		given(userService.findByUserId(loginForm.userId())).willReturn(Optional.empty());

		// then
		CustomException customException = assertThrows(CustomException.class,
			() -> userService.login(loginForm));
		assertThat(StatusEnum.USER_NOT_FOUND).isEqualTo(customException.getStatusEnum());

		verify(userCacheService, never()).setUser(any());
		verify(tokenService, never()).issue(anyString());
	}

	@Test
	@DisplayName("로그인 실패: 일치하지 않는 비밀번호")
	void login_fail3() {
		// given
		LoginForm loginForm = makeLoginForm("testId", "asdf123456");
		given(userService.findByUserId(loginForm.userId())).willReturn(Optional.of(makeUser()));
		given(passwordEncrypt.isMatch(loginForm.userPassword(),
			passwordEncrypt.encrypt(loginForm.userPassword()))).willReturn(false);

		// then
		CustomException customException = assertThrows(CustomException.class,
			() -> userService.login(loginForm));
		assertThat(customException.getStatusEnum()).isEqualTo(StatusEnum.PASSWORD_NOT_MATCH);

		verify(userCacheService, never()).setUser(any());
		verify(tokenService, never()).issue(anyString());
	}

	private User makeUser() {
		return User.builder()
			.id(1)
			.userId("testId")
			.name("user1")
			.password("asdf123456")
			.email("asdfasd@naver.com")
			.joinDate(LocalDateTime.now())
			.userStatus(UserStatus.WITHDRAWAL)
			.userType(UserType.ORDINARY)
			.build();
	}

	private LoginForm makeLoginForm(String userId, String password) {
		return LoginForm.builder()
			.userId(userId)
			.userPassword(password)
			.build();
	}
}
