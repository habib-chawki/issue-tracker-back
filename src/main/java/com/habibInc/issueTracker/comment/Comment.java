package com.habibInc.issueTracker.comment;

import com.habibInc.issueTracker.issue.Issue;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Issue issue;

    private String owner;
    private String content;

    private LocalDateTime creationTime;
    private LocalDateTime updateTime;


    public Comment() {
    }

    public Comment(String owner, String content) {
        this.owner = owner;
        this.content = content;
    }

    public Comment(Long id, Issue issue, String owner, String content, LocalDateTime creationTime, LocalDateTime updateTime) {
        this.id = id;
        this.issue = issue;
        this.owner = owner;
        this.content = content;
        this.creationTime = creationTime;
        this.updateTime = updateTime;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getOwner() {
        return owner;
    }

    public void setCreationTime(LocalDateTime creationTime) {
        this.creationTime = creationTime;
    }

    public LocalDateTime getCreationTime() {
        return creationTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public Issue getIssue() {
        return issue;
    }

    public void setIssue(Issue issue) {
        this.issue = issue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Comment comment = (Comment) o;
        return id.equals(comment.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
