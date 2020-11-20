package com.habibInc.issueTracker.comment;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class CommentRepositoryTest {
    @Autowired
    CommentRepository commentRepository;

    Comment comment;

    @BeforeEach
    public void setup(){
        // set up a new comment
        comment = new Comment();

        comment.setId(1L);

        comment.setContent("My comment");
        comment.setOwner("Me");

        comment.setCreationTime(LocalDateTime.now());
        comment.setUpdateTime(LocalDateTime.now());
    }
}
