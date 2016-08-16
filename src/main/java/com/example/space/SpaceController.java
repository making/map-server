package com.example.space;

import com.example.user.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "spaces")
public class SpaceController {
    private final SpaceMapper spaceMapper;
    private final UserMapper userMapper;
    private final ApplicationEventPublisher publisher;
    private final TransactionTemplate tx;

    @Autowired
    public SpaceController(SpaceMapper spaceMapper, UserMapper userMapper,
                           ApplicationEventPublisher publisher, TransactionTemplate tx) {
        this.spaceMapper = spaceMapper;
        this.userMapper = userMapper;
        this.publisher = publisher;
        this.tx = tx;
    }

    @RequestMapping(method = RequestMethod.POST)
    ResponseEntity<Space> newSpace(@RequestBody Space space) {
        spaceMapper.insert(space);
        return ResponseEntity.status(HttpStatus.CREATED).body(space);
    }

    @RequestMapping(path = "{spaceId}", method = RequestMethod.GET)
    ResponseEntity<?> getSpace(@PathVariable("spaceId") String spaceId) {
        Space space = spaceMapper.findById(spaceId);
        if (space == null) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(space);
        }
    }

    @RequestMapping(path = "{spaceId}", method = RequestMethod.DELETE)
    ResponseEntity<Void> deleteSpace(@PathVariable("spaceId") String spaceId) {
        tx.execute(s -> {
            userMapper.deleteBySpaceId(spaceId);
            spaceMapper.delete(spaceId);
            return null;
        });
        Space space = new Space();
        space.setSpaceId(spaceId);
        publisher.publishEvent(space);
        return ResponseEntity.noContent().build();
    }
}
