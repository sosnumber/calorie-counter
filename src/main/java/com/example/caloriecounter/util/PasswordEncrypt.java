package com.example.caloriecounter.util;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Component;

@Component
public class PasswordEncrypt {

	public String encrypt(final String password) {
		return BCrypt.hashpw(password, BCrypt.gensalt());
	}

	public boolean isMatch(final String password, final String hashedPassword) {
		return BCrypt.checkpw(password, hashedPassword);
	}
}
