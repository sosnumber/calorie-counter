package com.example.caloriecounter.util;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PasswordEncryptTest {

	@Test
	@DisplayName("패스워드 암호화 정상 테스트")
	void encryptTest() {
		String rawPw = "asdf1234";
		String encryptPw = PasswordEncrypt.encrypt("asdf1234");
		assertThat(PasswordEncrypt.isMatch(rawPw, encryptPw)).isTrue();
	}
}