package com.habibInc.issueTracker.issue;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;

@Entity
public class Issue {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    public Issue(){}

    public Issue(Long id) {
        this.id = id;
    }

    public Long getId() {
        return this.id;
    }
}
