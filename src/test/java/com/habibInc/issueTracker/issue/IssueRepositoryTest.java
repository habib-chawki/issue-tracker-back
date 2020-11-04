package com.habibInc.issueTracker.issue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class IssueRepositoryTest {

    @Autowired
    IssueRepository issueRepository;

    @Test
    public void itShouldSaveIssue(){
        Issue savedIssue = issueRepository.save(new Issue());

        assertThat(savedIssue.getId()).isEqualTo(savedIssue.getId());
    }
}
