package com.habibInc.issueTracker.comment;

import com.habibInc.issueTracker.issue.Issue;
import com.habibInc.issueTracker.issue.IssueRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class CommentRepositoryTest {

    @Autowired
    CommentRepository commentRepository;

    @Autowired
    IssueRepository issueRepository;

    Comment comment;

    @BeforeEach
    public void setup(){
        // set up a new comment
        comment = new Comment();

        comment.setContent("My comment");

        comment.setCreationTime(LocalDateTime.now());
        comment.setUpdateTime(LocalDateTime.now());
    }

    @Test
    public void itShouldSaveComment(){
        // given a new saved comment
        Comment response = commentRepository.save(comment);

        // the saved comment should be returned with a positive autogenerated id
        assertThat(response).isEqualTo(comment);
        assertThat(response.getId()).isNotNull().isPositive();
    }

    @Test
    public void itShouldDeleteCommentById() {
        // given a saved comment
        Comment savedComment = commentRepository.save(comment);

        // the comment should be present before deleting
        Optional<Comment> before = commentRepository.findById(savedComment.getId());
        assertThat(before.isPresent()).isTrue();

        // when the comment is deleted
        commentRepository.deleteById(savedComment.getId());

        // then it should not be present afterwards
        Optional<Comment> after = commentRepository.findById(savedComment.getId());
        assertThat(after.isPresent()).isFalse();
    }

    @Test
    public void itShouldFindCommentByIssueId() {
        // create and save an issue
        Issue issue = new Issue();
        issue = issueRepository.save(issue);

        // save the comment after setting the issue
        comment.setIssue(issue);
        commentRepository.save(comment);

        // when attempting to find a comment by its issue id
        Optional<Comment> issueOptional = commentRepository.findByIssueId(issue.getId());

        // expect the comment to have been found successfully
        assertThat(issueOptional.isPresent()).isTrue();
    }
}
