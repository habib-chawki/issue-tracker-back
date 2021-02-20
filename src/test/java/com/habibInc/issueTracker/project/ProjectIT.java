package com.habibInc.issueTracker.project;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ProjectIT {

    Project project;

    @BeforeEach
    public void setup() {

    }

}
