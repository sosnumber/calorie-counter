package com.example.flabcaloriecounter.feed.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import com.example.flabcaloriecounter.exception.FeedNotFoundException;
import com.example.flabcaloriecounter.exception.InvalidUserException;
import com.example.flabcaloriecounter.exception.UserNotFoundException;
import com.example.flabcaloriecounter.feed.application.port.in.dto.FeedRequestDto;
import com.example.flabcaloriecounter.feed.application.port.in.dto.ImageUploadDto;
import com.example.flabcaloriecounter.feed.application.port.out.FeedPort;
import com.example.flabcaloriecounter.feed.domain.Feed;
import com.example.flabcaloriecounter.user.adapter.out.persistence.UserRepository;
import com.example.flabcaloriecounter.user.application.port.in.response.SignUpForm;
import com.example.flabcaloriecounter.user.domain.JudgeStatus;
import com.example.flabcaloriecounter.user.domain.UserType;

@SpringBootTest
@Transactional
@Sql("classpath:tableInit.sql")
class FeedServiceTest {

	private static final String WRITE_CONTENT = "닭가슴살을 먹었다";
	private static final String UPDATE_CONTENT = "닭가슴살을 먹었다(수정된내용)";

	@Autowired
	private FeedService feedService;

	@Autowired
	private FeedPort feedPort;

	@Autowired
	private UserRepository userRepository;

	private FeedRequestDto contentsFeed;
	private SignUpForm signUpForm;
	private SignUpForm signUpForm2;
	private List<ImageUploadDto> imageInfos;
	private List<ImageUploadDto> onlyImageInfos;

	MockMultipartFile image1;
	MockMultipartFile image2;

	@BeforeEach
	void setup() {
		this.contentsFeed = new FeedRequestDto(
			WRITE_CONTENT,
			null
		);

		this.image1 = new MockMultipartFile(
			"feedDto",
			"photos",
			"image/jpeg",
			"photos".getBytes()
		);

		this.image2 = new MockMultipartFile(
			"feedDto",
			"photos2",
			"image/jpeg",
			"photos2".getBytes()
		);

		this.signUpForm = new SignUpForm(
			"user",
			"이영진",
			"12345678",
			"dudwls0505@naver.com",
			60.02,
			UserType.ORDINARY,
			JudgeStatus.getInitialJudgeStatusByUserType(UserType.ORDINARY)
		);

		this.signUpForm2 = new SignUpForm(
			"notMatchUser",
			"이영진2",
			"123456783",
			"dudwls05053@naver.com",
			60.32,
			UserType.ORDINARY,
			JudgeStatus.getInitialJudgeStatusByUserType(UserType.ORDINARY)
		);
	}

	//todo 로그인 유무 check
	@Test
	@DisplayName("피드 작성 성공 : contents만 있음 ")
	void feed_write_success() {
		this.userRepository.signUp(this.signUpForm);

		assertDoesNotThrow(() -> this.feedService.write(this.contentsFeed, 1));

		//수정필요
		assertThat(this.feedService.findByFeedId(1)).isEqualTo(
			Optional.of(new Feed(1, WRITE_CONTENT, null, 1, null)));
	}

	//todo 로그인 유무 check
	@Test
	@DisplayName("피드 작성 성공 : contents, Image 둘다 있음")
	void feed_write_success2() {
		this.userRepository.signUp(this.signUpForm);

		this.imageInfos = List.of(
			new ImageUploadDto("image1.png", "local/2022/1221/user12q3wqeqwe.png",
				this.feedPort.write(WRITE_CONTENT, 1)),
			new ImageUploadDto("image2.png", "local/2022/1221/user211231q3wqeqwe.png",
				this.feedPort.write(WRITE_CONTENT, 1)));

		assertDoesNotThrow(() -> this.feedPort.insertImage(this.imageInfos));

		//수정필요
		assertThat(this.feedService.findByFeedId(1)).isEqualTo(
			Optional.of(new Feed(1, WRITE_CONTENT, null, 1, null)));
	}

	//todo 로그인 유무 check
	@Test
	@DisplayName("피드 작성 성공 : Image만 있음")
	void feed_write_success3() {
		this.userRepository.signUp(this.signUpForm);

		this.onlyImageInfos = List.of(
			new ImageUploadDto("image3.png", "local/2022/1221/user12q3wqeqwe.png", this.feedPort.write("", 1)),
			new ImageUploadDto("image4.png", "local/2022/1221/user211231q3wqeqwe.png", this.feedPort.write("", 1)));

		assertDoesNotThrow(() -> this.feedPort.insertImage(this.onlyImageInfos));

		//수정필요
		assertThat(this.feedService.findByFeedId(1)).isEqualTo(
			Optional.of(new Feed(1, "", null, 1, null)));
	}

	//todo 피드 작성 실패 : 유저가 로그인하지않음

	//todo 피드 작성 실패 : 유저가 존재하지않음

	//    @Test
	//    @DisplayName("피드 작성 실패: 유저가 존재하지않는다")
	//    void feed_write_fail_notExistUser() {
	//        assertThatThrownBy(() -> this.feedService.write(this.rightFeed))
	//                .isInstanceOf(UserNotFoundException.class);
	//    }

	@Test
	@DisplayName("피드 수정 성공: contents만 있음")
	void feed_update_success() {
		this.userRepository.signUp(this.signUpForm);
		this.feedService.write(this.contentsFeed, 1);

		assertDoesNotThrow(() -> this.feedService.update(UPDATE_CONTENT, null, "user", 1));
		//수정 내용 확인하기위해 update타입을 변경?
	}

	@Test
	@DisplayName("피드 수정 성공: image만 있음")
	void feed_update_success2() {
		this.userRepository.signUp(this.signUpForm);
		this.feedService.write(this.contentsFeed, 1);

		assertDoesNotThrow(() -> this.feedService.update(null, List.of(image1, image2), "user", 1));
		//수정 내용 확인하기위해 update타입을 변경?
	}

	@Test
	@DisplayName("피드 수정 성공: image, contents둘다 있음")
	void feed_update_success3() {
		this.userRepository.signUp(this.signUpForm);
		this.feedService.write(this.contentsFeed, 1);

		assertDoesNotThrow(() -> this.feedService.update(UPDATE_CONTENT, List.of(image1, image2), "user", 1));
	}

	@Test
	@DisplayName("피드 수정 실패: 존재하지 않는 피드")
	void feed_update_fail() {
		this.userRepository.signUp(this.signUpForm);

		assertThatThrownBy(() -> this.feedService.update(UPDATE_CONTENT, null, "user", 1));

	}

	@Test
	@DisplayName("피드 수정 실패: 존재하지 않는 유저")
	void feed_update_fail2() {
		assertThatThrownBy(() -> this.feedService.update(UPDATE_CONTENT, null, "user", 1))
			.isInstanceOf(UserNotFoundException.class);

	}

	@Test
	@DisplayName("피드 수정 실패: 권한이 없는 유저")
	void feed_update_fail3() {
		this.userRepository.signUp(this.signUpForm);
		this.userRepository.signUp(this.signUpForm2);
		this.feedService.write(this.contentsFeed, 1);

		assertThatThrownBy(() -> this.feedService.update(UPDATE_CONTENT, null, "notMatchUser", 1))
			.isInstanceOf(InvalidUserException.class);

	}

	@Test
	@DisplayName("피드 삭제 성공")
	void feed_delete_success() {
		this.userRepository.signUp(this.signUpForm);
		this.feedService.write(this.contentsFeed, 1);

		assertDoesNotThrow(() -> this.feedService.delete(this.signUpForm.userId(), 1));

		//수정 내용 확인하기위해 update타입을 변경?
	}

	@Test
	@DisplayName("피드 삭제 실패: 존재하지 않는 피드")
	void feed_delete_fail() {
		this.userRepository.signUp(this.signUpForm);

		assertThatThrownBy(() -> this.feedService.delete(this.signUpForm.userId(), 1)).isInstanceOf(
			FeedNotFoundException.class);
	}

	@Test
	@DisplayName("피드 삭제 실패: 존재하지 않는 유저")
	void feed_delete_fail2() {
		assertThatThrownBy(() -> this.feedService.delete(this.signUpForm.userId(), 1))
			.isInstanceOf(UserNotFoundException.class);
	}

	@Test
	@DisplayName("피드 삭제 실패: 권한이 없는 유저")
	void feed_delete_fail3() {
		this.userRepository.signUp(this.signUpForm);
		this.userRepository.signUp(this.signUpForm2);
		this.feedService.write(this.contentsFeed, 1);

		assertThatThrownBy(() -> this.feedService.delete(this.signUpForm2.userId(), 1))
			.isInstanceOf(InvalidUserException.class);
	}
}