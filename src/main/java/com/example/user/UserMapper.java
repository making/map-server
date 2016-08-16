package com.example.user;

import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserMapper {
	@Insert("INSERT INTO user(user_id, password, space_id) VALUES(#{userId}, #{password}, #{spaceId})")
	void insert(User user);

	@Select("SELECT * FROM user WHERE space_id = #{value}")
	List<User> findBySpaceId(String spaceId);

	@Select("SELECT * FROM user WHERE user_id = #{userId}")
	User findByUserId(@Param("userId") String userId);

	@Select("SELECT * FROM user WHERE user_id = #{userId} AND space_id = #{spaceId}")
	User findByUserIdAndSpaceId(@Param("userId") String userId,
			@Param("spaceId") String spaceId);

	@Delete("DELETE FROM user WHERE user_id = #{userId} AND space_id = #{spaceId}")
	void delete(@Param("userId") String userId, @Param("spaceId") String spaceId);

	@Delete("DELETE FROM user WHERE space_id = #{spaceId}")
	void deleteBySpaceId(@Param("spaceId") String spaceId);
}
