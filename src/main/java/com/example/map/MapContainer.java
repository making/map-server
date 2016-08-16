package com.example.map;

import com.example.space.Space;
import org.springframework.context.event.EventListener;
import org.springframework.security.access.method.P;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
public class MapContainer {

	final ConcurrentMap<String, ConcurrentMap<String, String>> map = new ConcurrentHashMap<>();

	final ConcurrentMap<String, String> empty = new ConcurrentHashMap<>();

	@PreAuthorize("#spaceId == principal.user.spaceId")
	public void put(@P("spaceId") String spaceId, String key, String value) {
		map.computeIfAbsent(spaceId, k -> new ConcurrentHashMap<>()).put(key, value);
	}

	@PreAuthorize("#spaceId == principal.user.spaceId")
	public String get(@P("spaceId") String spaceId, String key) {
		return map.getOrDefault(spaceId, empty).get(key);
	}

	@PreAuthorize("#spaceId == principal.user.spaceId")
	public void delete(@P("spaceId") String spaceId, String key) {
		map.getOrDefault(spaceId, empty).remove(key);
	}

	@EventListener
	void deleteSpace(Space space) {
		map.remove(space.getSpaceId());
	}
}
