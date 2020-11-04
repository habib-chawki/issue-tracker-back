package com.habibInc.issueTracker.issue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class IssueRepositoryTest {

    @Autowired
    IssueRepository issueRepository;

    @Test
    public void itShouldSaveIssue(){
        Issue issue = new Issue(1L);
        Issue savedIssue = issueRepository.save(issue);

        assertThat(savedIssue.getId()).isEqualTo(1L);
    }
}
