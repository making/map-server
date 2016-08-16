package com.example.space;

import java.io.Serializable;

public class Space implements Serializable {
	private String spaceId;

	public String getSpaceId() {
		return spaceId;
	}

	public void setSpaceId(String spaceId) {
		this.spaceId = spaceId;
	}

	@Override
	public String toString() {
		return "Space{" +
				"spaceId='" + spaceId + '\'' +
				'}';
	}
}
