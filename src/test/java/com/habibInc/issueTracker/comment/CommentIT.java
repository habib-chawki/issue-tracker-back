package com.habibInc.issueTracker.comment;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class CommentIT {
    @Autowired
    TestRestTemplate restTemplate;

    @Autowired
    ObjectMapper mapper;

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
    public void itShouldCreateComment() throws Exception{
        String requestBody = mapper.writeValueAsString(comment);

        ResponseEntity<Comment> response = restTemplate.postForEntity("/comments", requestBody, Comment.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isEqualTo(comment);
    }
}
