package com.habibInc.issueTracker.issue;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
public class Issue {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String key;

    private String description;
    private String summary;

    private IssueType type;
    private IssueResolution resolution;

    private List<String> comments;
    private int votes;

    private String assignee;
    private String reporter;

    private LocalDateTime creationTime;
    private LocalDateTime updateTime;
    private LocalTime estimate;


    public Issue(){}

    public Issue(Long id) {
        this.id = id;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return this.id;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getSummary(){
        return summary;
    }

    public void setType(IssueType type) {
        this.type = type;
    }

    public IssueType getType() {
        return type;
    }

    public void setResolution(IssueResolution resolution) {
        this.resolution = resolution;
    }

    public IssueResolution getResolution() {
        return resolution;
    }

    public void setComments(List<String> comments) {
        this.comments = comments;
    }

    public List<String> getComments() {
        return comments;
    }

    public void setVotes(int votes) {
        this.votes = votes;
    }

    public int getVotes() {
        return votes;
    }

    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }

    public String getAssignee() {
        return assignee;
    }

    public void setReporter(String reporter) {
        this.reporter = reporter;
    }

    public String getReporter() {
        return reporter;
    }

    public void setEstimate(LocalTime estimate) {
        this.estimate = estimate;
    }

    public LocalTime getEstimate() {
        return estimate;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setCreationTime(LocalDateTime creationTime) {
        this.creationTime = creationTime;
    }

    public LocalDateTime getCreationTime() {
        return creationTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Issue issue = (Issue) o;
        return Objects.equals(id, issue.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
