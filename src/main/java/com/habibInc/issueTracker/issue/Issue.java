package com.habibInc.issueTracker.issue;

import com.habibInc.issueTracker.comment.Comment;
import org.hibernate.annotations.NaturalId;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;

@Entity
public class Issue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NaturalId
    private String IssueKey;

    private String description;
    private String summary;

    private IssueType type;
    private IssueResolution resolution;

    @OneToMany(mappedBy = "issue")
    private List<Comment> comments;

    private int votes;

    private String assignee;
    private String reporter;

    private LocalDateTime creationTime;
    private LocalDateTime updateTime;
    private LocalTime estimate;

    // constructors
    public Issue(){}

    public Issue(Long id, String IssueKey, String description, String summary,
                 IssueType type, IssueResolution resolution,
                 List<Comment> comments, int votes,
                 String assignee, String reporter,
                 LocalDateTime creationTime, LocalDateTime updateTime,
                 LocalTime estimate) {
        this.id = id;
        this.IssueKey = IssueKey;
        this.description = description;
        this.summary = summary;
        this.type = type;
        this.resolution = resolution;
        this.comments = comments;
        this.votes = votes;
        this.assignee = assignee;
        this.reporter = reporter;
        this.creationTime = creationTime;
        this.updateTime = updateTime;
        this.estimate = estimate;
    }

    // access methods
    public void setIssueKey(String issueKey) {
        this.IssueKey = issueKey;
    }

    public String getIssueKey() {
        return IssueKey;
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

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public List<Comment> getComments() {
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

    // equals and hashCode
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
