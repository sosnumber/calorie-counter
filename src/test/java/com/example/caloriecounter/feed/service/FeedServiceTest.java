package com.example.caloriecounter.feed.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.jdbc.Sql;

import com.example.caloriecounter.comment.service.CommentService;
import com.example.caloriecounter.exception.CustomException;
import com.example.caloriecounter.feed.controller.dto.request.FeedDto;
import com.example.caloriecounter.feed.controller.dto.request.FeedListDto;
import com.example.caloriecounter.feed.controller.dto.request.GetFeedListDto;
import com.example.caloriecounter.feed.controller.dto.request.Paging;
import com.example.caloriecounter.feed.domain.Feed;
import com.example.caloriecounter.like.service.LikeService;
import com.example.caloriecounter.photo.service.PhotoService;
import com.example.caloriecounter.user.controller.dto.request.LoginForm;
import com.example.caloriecounter.user.controller.dto.request.SignUpForm;
import com.example.caloriecounter.user.service.UserService;
import com.example.caloriecounter.util.StatusEnum;

@SpringBootTest
@Sql("classpath:tableInit.sql")
class FeedServiceTest {

	private static final String UPDATE_CONTENT = "닭가슴살을 먹었다(수정된내용)";

	@Autowired
	private FeedService feedService;

	@Autowired
	private UserService userService;

	@Autowired
	private PhotoService photoService;

	@Autowired
	private CommentService commentService;

	@Autowired
	private LikeService likeService;

	private final SignUpForm wrongSignUpForm = new SignUpForm(2, "wrongUser", "김영진", "asdf1234",
		"dudwls0505@nate.com");
	private final LoginForm wrongLoginForm = new LoginForm(wrongSignUpForm.getUserId(),
		wrongSignUpForm.getUserPassword());

	private final SignUpForm wrongSignUpForm2 = new SignUpForm("otherUser2", "외부인2", "asdf1234", "dudwls0504@daum.net");
	private final LoginForm wrongLoginForm2 = new LoginForm(wrongSignUpForm2.getUserId(),
		wrongSignUpForm2.getUserPassword());

	private final SignUpForm alreadySignUpForm = new SignUpForm("mockUser", "이영진", "asdf1234", "dudwls0505@naver.com");
	private final LoginForm alreadyLoginForm = new LoginForm("mockUser", "asdf1234");

	private final FeedDto feedByWrongUser = new FeedDto("게시글내용1", alreadySignUpForm.getId());

	MockMultipartFile image1 = new MockMultipartFile("feedDto", "photos", "image/jpeg", "photos".getBytes());
	MockMultipartFile image2 = new MockMultipartFile("feedDto", "photos2", "image/jpeg", "photos2".getBytes());

	FeedDto notWriteFeed = new FeedDto("게시글내용1", List.of(this.image1, this.image2),
		alreadySignUpForm.getId());
	FeedDto feedWithContents = new FeedDto("게시글내용1", alreadySignUpForm.getId());
	FeedDto feedWithPhoto = new FeedDto(List.of(this.image1, this.image2), alreadySignUpForm.getId());

	//todo 피드를 작성하고, 삭제해야할텐데 @AfterEach로 삭제하나? 근데 만약, 테스트도중 빌드서버가 중단하거나 디버거에서 테스트를 종료하면..? @AfterEach를 생략하는경우가 생길텐데?
	// todo 이 부분이 생략이 안된다고 해서 어떤 문제가 생길수있는것인가? 일단 놔둔다.
	@BeforeEach
	void setup() {
		this.userService.login(alreadyLoginForm);
		this.feedService.deleteAll();
	}

	@Test
	@DisplayName("피드 수정 성공: 수정내용에 contents만 있음")
	void feed_update_success() {
		feedService.write(feedWithContents);

		assertDoesNotThrow(() -> feedService.update(feedWithContents.getContents(), null, alreadySignUpForm.getId(),
			feedWithContents.getId()));
		assertThat(feedService.findByFeedId(feedWithContents.getId()).orElseThrow()).isEqualTo(
			new Feed(feedWithContents.getId(), feedWithContents.getContents(), null, alreadySignUpForm.getId(),
				null,
				null));
	}

	@Test
	@DisplayName("피드 수정 성공: 수정내용에 image만 있음")
	void feed_update_success2() {
		feedService.write(feedWithContents);

		assertDoesNotThrow(
			() -> feedService.update(null, List.of(image1, image2), alreadySignUpForm.getId(),
				feedWithContents.getId()));
		//todo 이미지 수정확인..?
	}

	@Test
	@DisplayName("피드 수정 성공: 수정내용에 image, contents 둘다 있음")
	void feed_update_success3() {
		feedService.write(feedWithContents);

		assertDoesNotThrow(
			() -> feedService.update(UPDATE_CONTENT, List.of(image1, image2), alreadySignUpForm.getId(),
				feedWithContents.getId()));

		//todo 이미지 수정확인..?
	}

	@Test
	@DisplayName("피드 삭제 성공 : 사진,댓글,좋아요도 삭제된다.")
	void feed_delete_success() {
		feedService.write(feedWithContents);

		assertDoesNotThrow(() -> feedService.delete(alreadySignUpForm.getId(), feedWithContents.getId()));
		assertThat(feedService.findByFeedId(feedWithContents.getId())).isEqualTo(Optional.empty());
		assertThat(photoService.findImageByFeedId(feedWithContents.getId()).size()).isEqualTo(0);
	}

	@Test
	@DisplayName("피드 조회 성공")
	void feed_read_success() {
		feedService.write(feedWithContents);
		feedService.write(feedWithPhoto);
		feedService.write(feedWithPhoto);

		List<FeedListDto> feedList = feedService.getFeedList(new Paging(feedService.maxCursor(), 5));

		//then
		assertThat(feedList.size()).isEqualTo(3);
		assertThat(feedService.feedListWithPhoto(feedList, alreadySignUpForm.getId(), 1, 30).size()).isEqualTo(3);
		assertThat(feedService.feedListWithPhoto(feedList, alreadySignUpForm.getId(), 1, 30).get(0)).isEqualTo(
			new GetFeedListDto(
				feedList.get(0).feedId(),
				feedList.get(0).contents(),
				feedList.get(0).writeDate(),
				feedList.get(0).userId(),
				photoService.photos(feedList.get(0).feedId()),
				likeService.likeCount(feedList.get(0).feedId()),
				likeService.findLikeStatusByUserId(feedList.get(0).feedId(), alreadySignUpForm.getId()),
				commentService.comment(feedList.get(0).feedId(), 0, 30)));

		assertThat(feedService.feedListWithPhoto(feedList, alreadySignUpForm.getId(), 1, 30).get(2)).isEqualTo(
			new GetFeedListDto(
				feedList.get(2).feedId(),
				feedList.get(2).contents(),
				feedList.get(2).writeDate(),
				feedList.get(2).userId(),
				photoService.photos(feedList.get(2).feedId()),
				likeService.likeCount(feedList.get(2).feedId()),
				likeService.findLikeStatusByUserId(feedList.get(2).feedId(), alreadySignUpForm.getId()),
				commentService.comment(feedList.get(0).feedId(), 0, 30)
			));
	}

	@Test
	@DisplayName("maxCursor는 피드의 최대값을 반환한다 ")
	void maxCursor_withFeed() {
		//given
		feedService.write(feedWithContents);
		feedService.write(feedWithPhoto);
		feedService.write(feedWithPhoto);

		assertEquals(feedService.maxCursor(), 3);
	}

	@Test
	@DisplayName("피드 작성 성공 : contents만 있음 ")
	void feed_write_success() {
		//when
		assertDoesNotThrow(() -> feedService.write(notWriteFeed));

		assertThat(feedService.findByFeedId(notWriteFeed.getId())).isEqualTo(
			Optional.of(new Feed(notWriteFeed.getId(), notWriteFeed.getContents(), null,
				alreadySignUpForm.getId(), null, null)));
	}

	@Test
	@DisplayName("피드 작성 성공 : contents, Image 둘다 있음")
	void feed_write_success2() {
		//when
		assertDoesNotThrow(() -> feedService.write(notWriteFeed));

		FeedDto feedDto = new FeedDto(notWriteFeed.getId(), List.of(image1, image2), alreadySignUpForm.getId());

		assertDoesNotThrow(() -> photoService.insertImage(feedDto));
		assertThat(feedService.findByFeedId(notWriteFeed.getId())).isEqualTo(
			Optional.of(new Feed(notWriteFeed.getId(), notWriteFeed.getContents(), null,
				alreadySignUpForm.getId(), null, null)));
	}

	@Test
	@DisplayName("피드 작성 성공 : Image만 있음")
	void feed_write_success3() {
		//when
		assertDoesNotThrow(() -> feedService.write(feedWithPhoto));

		FeedDto feedDto = new FeedDto(notWriteFeed.getId(), List.of(image1, image2), alreadySignUpForm.getId());

		assertDoesNotThrow(() -> photoService.insertImage(feedDto));
		assertThat(feedService.findByFeedId(feedWithPhoto.getId())).isEqualTo(
			Optional.of(
				new Feed(feedWithPhoto.getId(), feedWithPhoto.getContents(), null, alreadySignUpForm.getId(),
					null,
					null)));
	}

	@Test
	@DisplayName("피드 수정 실패: 존재하지 않는 피드")
	void feed_update_fail() {
		CustomException customException = assertThrows(CustomException.class,
			() -> feedService.update(UPDATE_CONTENT, null, alreadySignUpForm.getId(), notWriteFeed.getId()));
		assertThat(StatusEnum.FEED_NOT_FOUND).isEqualTo(customException.getStatusEnum());
	}

	@Test
	@DisplayName("피드 삭제 실패: 존재하지 않는 피드")
	void feed_delete_fail() {
		CustomException customException = assertThrows(CustomException.class,
			() -> feedService.delete(alreadySignUpForm.getId(), notWriteFeed.getId()));
		assertThat(StatusEnum.FEED_NOT_FOUND).isEqualTo(customException.getStatusEnum());
	}

	@Test
	@DisplayName("피드가 하나도 없는경우 maxCursor는 0이된다")
	void maxCursor_withoutFeed() {
		assertEquals(feedService.maxCursor(), 0);
	}

	@Test
	@DisplayName("피드 수정 실패: 권한이 없는 유저")
	void feed_update_fail3() {
		//given
		userService.login(wrongLoginForm);
		feedService.write(feedByWrongUser);

		CustomException customException = assertThrows(CustomException.class,
			() -> feedService.update("수정할 내용", null, wrongSignUpForm.getId(), feedByWrongUser.getId()));
		assertThat(StatusEnum.INVALID_USER).isEqualTo(customException.getStatusEnum());
	}

	@Test
	@DisplayName("피드 삭제 실패: 권한이 없는 유저")
	void feed_delete_fail3() {
		//given
		userService.login(wrongLoginForm);
		feedService.write(feedByWrongUser);

		CustomException customException = assertThrows(CustomException.class,
			() -> feedService.delete(wrongSignUpForm.getId(), feedByWrongUser.getId()));
		assertThat(StatusEnum.INVALID_USER).isEqualTo(customException.getStatusEnum());
	}

}