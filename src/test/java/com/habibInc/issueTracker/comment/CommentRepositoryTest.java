package com.habibInc.issueTracker.comment;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

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

        comment.setContent("My comment");
        comment.setOwner("Me");

        comment.setCreationTime(LocalDateTime.now());
        comment.setUpdateTime(LocalDateTime.now());
    }

    @Test
    public void itShouldSaveComment(){
        Comment response = commentRepository.save(comment);

        assertThat(response).isEqualTo(comment);
        assertThat(response.getId()).isNotNull().isPositive();
    }
}
