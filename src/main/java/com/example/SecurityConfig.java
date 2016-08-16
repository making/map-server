package com.example;

import com.example.user.LoginUser;
import com.example.user.User;
import com.example.user.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

	@Order(1)
	@EnableWebSecurity
	public static class UserSecurityConfig extends WebSecurityConfigurerAdapter {
		@Autowired
		UserMapper userMapper;

		@Override
		protected void configure(HttpSecurity http) throws Exception {
			http.antMatcher("/spaces/*/map/**").authorizeRequests().antMatchers("/**")
					.authenticated().and().httpBasic().and().sessionManagement()
					.sessionCreationPolicy(SessionCreationPolicy.STATELESS).and().csrf()
					.disable();
		}

		@Override
		protected void configure(AuthenticationManagerBuilder auth) throws Exception {
			auth.userDetailsService(s -> {
				User user = userMapper.findByUserId(s);
				if (user == null) {
					throw new UsernameNotFoundException("not found");
				}
				return new LoginUser(user);
			});
		}
	}

	@Order(2)
	@EnableWebSecurity
	public static class AdminSecurityConfig extends WebSecurityConfigurerAdapter {
		@Autowired
		SecurityProperties security;

		@Override
		protected void configure(HttpSecurity http) throws Exception {
			http.authorizeRequests().antMatchers("/**").authenticated().and().httpBasic()
					.and().sessionManagement()
					.sessionCreationPolicy(SessionCreationPolicy.STATELESS).and().csrf()
					.disable();
		}

		@Override
		protected void configure(AuthenticationManagerBuilder auth) throws Exception {
			auth.inMemoryAuthentication().withUser(security.getUser().getName())
					.password(security.getUser().getPassword())
					.roles(security.getUser().getRole().toArray(new String[0]));
		}
	}

}
