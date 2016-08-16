package com.example.space;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface SpaceMapper {

	@Insert("INSERT INTO space(space_id) VALUES(#{spaceId})")
	void insert(Space space);

	@Select("SELECT * FROM space WHERE space_id = #{value}")
	Space findById(String spaceId);

	@Delete("DELETE FROM space WHERE space_id = #{value}")
	void delete(String spaceId);
}
