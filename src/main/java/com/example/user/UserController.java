package com.example.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("spaces/{spaceId}/users")
public class UserController {
	private final UserMapper userMapper;

	@Autowired
	public UserController(UserMapper userMapper) {
		this.userMapper = userMapper;
	}

	@RequestMapping(method = RequestMethod.POST)
	ResponseEntity<User> newUser(@PathVariable("spaceId") String spaceId,
			@RequestBody User user) {
		user.setPassword(UUID.randomUUID().toString());
		user.setSpaceId(spaceId);
		userMapper.insert(user);
		return ResponseEntity.status(HttpStatus.CREATED).body(user);
	}

	@RequestMapping(method = RequestMethod.GET)
	ResponseEntity<List<User>> listUsers(@PathVariable("spaceId") String spaceId) {
		return ResponseEntity.ok(userMapper.findBySpaceId(spaceId));
	}

	@RequestMapping(path = "{userId}", method = RequestMethod.GET)
	ResponseEntity<?> getUser(@PathVariable("spaceId") String spaceId,
			@PathVariable("userId") String userId) {
		User user = userMapper.findByUserIdAndSpaceId(userId, spaceId);
		if (user == null) {
			return ResponseEntity.notFound().build();
		}
		else {
			return ResponseEntity.ok(user);
		}

	}

	@RequestMapping(path = "{userId}", method = RequestMethod.DELETE)
	ResponseEntity<Void> deleteUsers(@PathVariable("spaceId") String spaceId,
			@PathVariable("userId") String userId) {
		userMapper.delete(userId, spaceId);
		return ResponseEntity.noContent().build();
	}
}
