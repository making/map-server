package com.example;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Base64;
import java.util.Collections;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = MapServerApplication.class)
@WebIntegrationTest(randomPort = true, value = { "security.user.name=admin",
		"security.user.password=admin" })
public class MapServerApplicationTests {

	TestRestTemplate restTemplate = new TestRestTemplate();
	@Value("${local.server.port}")
	int port;
	String BASIC_ADMIN = basic("admin", "admin");

	String basic(String username, String password) {
		return "Basic " + Base64.getEncoder()
				.encodeToString((username + ":" + password).getBytes());
	}

	void createSpace(String spaceId) {
		RequestEntity<?> req1 = RequestEntity
				.post(UriComponentsBuilder.fromHttpUrl("http://localhost:" + port)
						.pathSegment("spaces").build().toUri())
				.header(HttpHeaders.AUTHORIZATION, BASIC_ADMIN)
				.body(Collections.singletonMap("spaceId", spaceId));
		ResponseEntity<JsonNode> res1 = restTemplate.exchange(req1, JsonNode.class);
		assertThat(res1.getStatusCode(), is(HttpStatus.CREATED));
		//
		RequestEntity<?> req2 = RequestEntity
				.get(UriComponentsBuilder.fromHttpUrl("http://localhost:" + port)
						.pathSegment("spaces", spaceId).build().toUri())
				.header(HttpHeaders.AUTHORIZATION, BASIC_ADMIN).build();
		ResponseEntity<JsonNode> res2 = restTemplate.exchange(req2, JsonNode.class);
		assertThat(res2.getStatusCode(), is(HttpStatus.OK));
	}

	JsonNode createUser(String spaceId) {
		RequestEntity<?> req3 = RequestEntity
				.post(UriComponentsBuilder.fromHttpUrl("http://localhost:" + port)
						.pathSegment("spaces", spaceId, "users").build().toUri())
				.header(HttpHeaders.AUTHORIZATION, BASIC_ADMIN).build();
		ResponseEntity<JsonNode> res3 = restTemplate.exchange(req3, JsonNode.class);
		assertThat(res3.getStatusCode(), is(HttpStatus.CREATED));
		return res3.getBody();
	}

	void deleteSpace(String spaceId) {
		RequestEntity<?> req2 = RequestEntity
				.delete(UriComponentsBuilder.fromHttpUrl("http://localhost:" + port)
						.pathSegment("spaces", spaceId).build().toUri())
				.header(HttpHeaders.AUTHORIZATION, BASIC_ADMIN).build();
		ResponseEntity<Void> res2 = restTemplate.exchange(req2, Void.class);
		assertThat(res2.getStatusCode(), is(HttpStatus.NO_CONTENT));
	}

	@Test
	public void createAndDeleteSpace() {
		String spaceId = UUID.randomUUID().toString();
		createSpace(spaceId);
		deleteSpace(spaceId);
		//
		RequestEntity<?> req3 = RequestEntity
				.get(UriComponentsBuilder.fromHttpUrl("http://localhost:" + port)
						.pathSegment("spaces", spaceId).build().toUri())
				.header(HttpHeaders.AUTHORIZATION, BASIC_ADMIN).build();
		ResponseEntity<JsonNode> res3 = restTemplate.exchange(req3, JsonNode.class);
		assertThat(res3.getStatusCode(), is(HttpStatus.NOT_FOUND));
	}

	@Test
	public void createSpaceAndCreateAndDeleteUser() {
		String spaceId = UUID.randomUUID().toString();
		createSpace(spaceId);
		JsonNode user = createUser(spaceId);
		String userId = user.get("userId").asText();
		String password = user.get("password").asText();
		assertThat(userId, is(notNullValue()));
		assertThat(password, is(notNullValue()));
		//
		RequestEntity<?> req2 = RequestEntity
				.delete(UriComponentsBuilder.fromHttpUrl("http://localhost:" + port)
						.pathSegment("spaces", spaceId, "users", userId).build().toUri())
				.header(HttpHeaders.AUTHORIZATION, BASIC_ADMIN).build();
		restTemplate.exchange(req2, Void.class);
		//
		RequestEntity<?> req4 = RequestEntity
				.get(UriComponentsBuilder.fromHttpUrl("http://localhost:" + port)
						.pathSegment("spaces", spaceId, "users", userId).build().toUri())
				.header(HttpHeaders.AUTHORIZATION, BASIC_ADMIN).build();
		ResponseEntity<JsonNode> res4 = restTemplate.exchange(req4, JsonNode.class);
		assertThat(res4.getStatusCode(), is(HttpStatus.NOT_FOUND));
	}

	@Test
	public void createSpaceAndCreateUserAndPutAndGetAndDeleteMap() {
		String spaceId = UUID.randomUUID().toString();
		createSpace(spaceId);
		JsonNode user = createUser(spaceId);
		String userId = user.get("userId").asText();
		String password = user.get("password").asText();
		assertThat(userId, is(notNullValue()));
		assertThat(password, is(notNullValue()));

		RequestEntity<?> req3 = RequestEntity
				.post(UriComponentsBuilder.fromHttpUrl("http://localhost:" + port)
						.pathSegment("spaces", spaceId, "map", "key1").build().toUri())
				.header(HttpHeaders.AUTHORIZATION, basic(userId, password))
				.body("value1");
		ResponseEntity<JsonNode> res3 = restTemplate.exchange(req3, JsonNode.class);
		assertThat(res3.getStatusCode(), is(HttpStatus.NO_CONTENT));
		//
		RequestEntity<?> req4 = RequestEntity
				.get(UriComponentsBuilder.fromHttpUrl("http://localhost:" + port)
						.pathSegment("spaces", spaceId, "map", "key1").build().toUri())
				.header(HttpHeaders.AUTHORIZATION, basic(userId, password)).build();
		ResponseEntity<String> res4 = restTemplate.exchange(req4, String.class);
		assertThat(res4.getStatusCode(), is(HttpStatus.OK));
		assertThat(res4.getBody(), is("value1"));
		//
		RequestEntity<?> req5 = RequestEntity
				.post(UriComponentsBuilder.fromHttpUrl("http://localhost:" + port)
						.pathSegment("spaces", spaceId, "map", "key2").build().toUri())
				.header(HttpHeaders.AUTHORIZATION, basic(userId, password))
				.body("value2");
		ResponseEntity<JsonNode> res5 = restTemplate.exchange(req5, JsonNode.class);
		assertThat(res5.getStatusCode(), is(HttpStatus.NO_CONTENT));
		//
		RequestEntity<?> req6 = RequestEntity
				.get(UriComponentsBuilder.fromHttpUrl("http://localhost:" + port)
						.pathSegment("spaces", spaceId, "map", "key2").build().toUri())
				.header(HttpHeaders.AUTHORIZATION, basic(userId, password)).build();
		ResponseEntity<String> res6 = restTemplate.exchange(req6, String.class);
		assertThat(res6.getStatusCode(), is(HttpStatus.OK));
		assertThat(res6.getBody(), is("value2"));
		//
		RequestEntity<?> req_ = RequestEntity
				.delete(UriComponentsBuilder.fromHttpUrl("http://localhost:" + port)
						.pathSegment("spaces", spaceId, "map", "key1").build().toUri())
				.header(HttpHeaders.AUTHORIZATION, basic(userId, password)).build();
		ResponseEntity<Void> res_ = restTemplate.exchange(req_, Void.class);
		assertThat(res_.getStatusCode(), is(HttpStatus.NO_CONTENT));
		//
		RequestEntity<?> req7 = RequestEntity
				.get(UriComponentsBuilder.fromHttpUrl("http://localhost:" + port)
						.pathSegment("spaces", spaceId, "map", "key1").build().toUri())
				.header(HttpHeaders.AUTHORIZATION, basic(userId, password)).build();
		ResponseEntity<String> res7 = restTemplate.exchange(req7, String.class);
		assertThat(res7.getStatusCode(), is(HttpStatus.NOT_FOUND));
		//
		RequestEntity<?> req8 = RequestEntity
				.get(UriComponentsBuilder.fromHttpUrl("http://localhost:" + port)
						.pathSegment("spaces", spaceId, "map", "key2").build().toUri())
				.header(HttpHeaders.AUTHORIZATION, basic(userId, password)).build();
		ResponseEntity<String> res8 = restTemplate.exchange(req8, String.class);
		assertThat(res8.getStatusCode(), is(HttpStatus.OK));
		assertThat(res8.getBody(), is("value2"));
	}

	@Test
	public void createSpaceAndCreateUserAndPutMapAndDeleteSpace() {
		String spaceId = UUID.randomUUID().toString();
		createSpace(spaceId);
		JsonNode user = createUser(spaceId);
		String userId = user.get("userId").asText();
		String password = user.get("password").asText();
		assertThat(userId, is(notNullValue()));
		assertThat(password, is(notNullValue()));

		RequestEntity<?> req3 = RequestEntity
				.post(UriComponentsBuilder.fromHttpUrl("http://localhost:" + port)
						.pathSegment("spaces", spaceId, "map", "key1").build().toUri())
				.header(HttpHeaders.AUTHORIZATION, basic(userId, password))
				.body("value1");
		ResponseEntity<JsonNode> res3 = restTemplate.exchange(req3, JsonNode.class);
		assertThat(res3.getStatusCode(), is(HttpStatus.NO_CONTENT));
		//
		RequestEntity<?> req4 = RequestEntity
				.get(UriComponentsBuilder.fromHttpUrl("http://localhost:" + port)
						.pathSegment("spaces", spaceId, "map", "key1").build().toUri())
				.header(HttpHeaders.AUTHORIZATION, basic(userId, password)).build();
		ResponseEntity<String> res4 = restTemplate.exchange(req4, String.class);
		assertThat(res4.getStatusCode(), is(HttpStatus.OK));
		assertThat(res4.getBody(), is("value1"));
		//
		RequestEntity<?> req5 = RequestEntity
				.post(UriComponentsBuilder.fromHttpUrl("http://localhost:" + port)
						.pathSegment("spaces", spaceId, "map", "key2").build().toUri())
				.header(HttpHeaders.AUTHORIZATION, basic(userId, password))
				.body("value2");
		ResponseEntity<JsonNode> res5 = restTemplate.exchange(req5, JsonNode.class);
		assertThat(res5.getStatusCode(), is(HttpStatus.NO_CONTENT));
		//
		RequestEntity<?> req6 = RequestEntity
				.get(UriComponentsBuilder.fromHttpUrl("http://localhost:" + port)
						.pathSegment("spaces", spaceId, "map", "key2").build().toUri())
				.header(HttpHeaders.AUTHORIZATION, basic(userId, password)).build();
		ResponseEntity<String> res6 = restTemplate.exchange(req6, String.class);
		assertThat(res6.getStatusCode(), is(HttpStatus.OK));
		assertThat(res6.getBody(), is("value2"));
		//
		deleteSpace(spaceId);
		//
		RequestEntity<?> req7 = RequestEntity
				.get(UriComponentsBuilder.fromHttpUrl("http://localhost:" + port)
						.pathSegment("spaces", spaceId, "map", "key1").build().toUri())
				.header(HttpHeaders.AUTHORIZATION, basic(userId, password)).build();
		ResponseEntity<String> res7 = restTemplate.exchange(req7, String.class);
		assertThat(res7.getStatusCode(), is(HttpStatus.UNAUTHORIZED));
		//
		RequestEntity<?> req8 = RequestEntity
				.get(UriComponentsBuilder.fromHttpUrl("http://localhost:" + port)
						.pathSegment("spaces", spaceId, "map", "key2").build().toUri())
				.header(HttpHeaders.AUTHORIZATION, basic(userId, password)).build();
		ResponseEntity<String> res8 = restTemplate.exchange(req8, String.class);
		assertThat(res8.getStatusCode(), is(HttpStatus.UNAUTHORIZED));
	}

	@Test
	public void accessMapByUserInTheSameSpace() {
		String spaceId = UUID.randomUUID().toString();
		createSpace(spaceId);
		JsonNode user1 = createUser(spaceId);
		String userId1 = user1.get("userId").asText();
		String password1 = user1.get("password").asText();
		JsonNode user2 = createUser(spaceId);
		String userId2 = user2.get("userId").asText();
		String password2 = user2.get("password").asText();

		RequestEntity<?> req3 = RequestEntity
				.post(UriComponentsBuilder.fromHttpUrl("http://localhost:" + port)
						.pathSegment("spaces", spaceId, "map", "key1").build().toUri())
				.header(HttpHeaders.AUTHORIZATION, basic(userId1, password1))
				.body("value1");
		ResponseEntity<JsonNode> res3 = restTemplate.exchange(req3, JsonNode.class);
		assertThat(res3.getStatusCode(), is(HttpStatus.NO_CONTENT));
		//
		RequestEntity<?> req4 = RequestEntity
				.get(UriComponentsBuilder.fromHttpUrl("http://localhost:" + port)
						.pathSegment("spaces", spaceId, "map", "key1").build().toUri())
				.header(HttpHeaders.AUTHORIZATION, basic(userId2, password2)).build();
		ResponseEntity<String> res4 = restTemplate.exchange(req4, String.class);
		assertThat(res4.getStatusCode(), is(HttpStatus.OK));
		assertThat(res4.getBody(), is("value1"));
	}

	@Test
	public void accessMapByUserInTheDifferentSpace() {
		String spaceId1 = UUID.randomUUID().toString();
		createSpace(spaceId1);
		String spaceId2 = UUID.randomUUID().toString();
		createSpace(spaceId2);
		JsonNode user1 = createUser(spaceId1);
		String userId1 = user1.get("userId").asText();
		String password1 = user1.get("password").asText();
		JsonNode user2 = createUser(spaceId2);
		String userId2 = user2.get("userId").asText();
		String password2 = user2.get("password").asText();

		RequestEntity<?> req3 = RequestEntity
				.post(UriComponentsBuilder.fromHttpUrl("http://localhost:" + port)
						.pathSegment("spaces", spaceId1, "map", "key1").build().toUri())
				.header(HttpHeaders.AUTHORIZATION, basic(userId1, password1))
				.body("value1");
		ResponseEntity<JsonNode> res3 = restTemplate.exchange(req3, JsonNode.class);
		assertThat(res3.getStatusCode(), is(HttpStatus.NO_CONTENT));
		//
		RequestEntity<?> req4 = RequestEntity
				.get(UriComponentsBuilder.fromHttpUrl("http://localhost:" + port)
						.pathSegment("spaces", spaceId1, "map", "key1").build().toUri())
				.header(HttpHeaders.AUTHORIZATION, basic(userId2, password2)).build();
		ResponseEntity<String> res4 = restTemplate.exchange(req4, String.class);
		assertThat(res4.getStatusCode(), is(HttpStatus.FORBIDDEN));
		//
		RequestEntity<?> req5 = RequestEntity
				.delete(UriComponentsBuilder.fromHttpUrl("http://localhost:" + port)
						.pathSegment("spaces", spaceId1, "map", "key1").build().toUri())
				.header(HttpHeaders.AUTHORIZATION, basic(userId2, password2)).build();
		ResponseEntity<Void> res5 = restTemplate.exchange(req5, Void.class);
		assertThat(res5.getStatusCode(), is(HttpStatus.FORBIDDEN));
		//
		RequestEntity<?> req6 = RequestEntity
				.post(UriComponentsBuilder.fromHttpUrl("http://localhost:" + port)
						.pathSegment("spaces", spaceId1, "map", "key2").build().toUri())
				.header(HttpHeaders.AUTHORIZATION, basic(userId2, password2))
				.body("value2");
		ResponseEntity<JsonNode> res6 = restTemplate.exchange(req6, JsonNode.class);
		assertThat(res6.getStatusCode(), is(HttpStatus.FORBIDDEN));
	}
}
