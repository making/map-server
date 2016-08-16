package com.example.map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("spaces/{spaceId}/map")
public class MapController {
	private final MapContainer container;

	@Autowired
	public MapController(MapContainer container) {
		this.container = container;
	}

	@RequestMapping(path = "{key}", method = RequestMethod.POST)
	ResponseEntity<?> setValue(@PathVariable("spaceId") String spaceId,
			@PathVariable("key") String key, @RequestBody String value) {
		container.put(spaceId, key, value);
		return ResponseEntity.noContent().build();
	}

	@RequestMapping(path = "{key}", method = RequestMethod.GET)
	ResponseEntity<?> getValue(@PathVariable("spaceId") String spaceId,
			@PathVariable("key") String key) {
		String value = container.get(spaceId, key);
		if (value == null) {
			return ResponseEntity.notFound().build();
		}
		else {
			return ResponseEntity.ok(value);
		}
	}

	@RequestMapping(path = "{key}", method = RequestMethod.DELETE)
	ResponseEntity<?> delete(@PathVariable("spaceId") String spaceId,
			@PathVariable("key") String key) {
		container.delete(spaceId, key);
		return ResponseEntity.noContent().build();
	}

}
