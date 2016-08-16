package com.example.user;

import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.*;

import java.util.Collections;

public class LoginUser extends org.springframework.security.core.userdetails.User {
	private final User user;

	public LoginUser(User user) {
		super(user.getUserId(), user.getPassword(),
				AuthorityUtils.createAuthorityList("ROLE_USER"));
		this.user = user;
	}

	public User getUser() {
		return user;
	}
}
