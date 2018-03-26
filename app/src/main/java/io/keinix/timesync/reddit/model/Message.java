package io.keinix.timesync.reddit.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Message {
    @SerializedName("was_comment")
    @Expose
    private boolean wasComment;

    @SerializedName("subject")
    @Expose
    private String subject;

    @SerializedName("author")
    @Expose String author;

    @SerializedName("parent_id")
    @Expose
    private String parentId;

    @SerializedName("subreddit_name_prefixed")
    @Expose
    private String subbreditNamePrefixed;

    @SerializedName("body")
    @Expose
    private String body;

    @SerializedName("link_title")
    @Expose
    private String linkTitle;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("created_utc")
    @Expose
    private long createdUtc;

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getSubbreditNamePrefixed() {
        return subbreditNamePrefixed;
    }

    public void setSubbreditNamePrefixed(String subbreditNamePrefixed) {
        this.subbreditNamePrefixed = subbreditNamePrefixed;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getLinkTitle() {
        return linkTitle;
    }

    public void setLinkTitle(String linkTitle) {
        this.linkTitle = linkTitle;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getCreatedUtc() {
        return createdUtc;
    }

    public void setCreatedUtc(long createdUtc) {
        this.createdUtc = createdUtc;
    }

    public boolean isWasComment() {
        return wasComment;
    }

    public void setWasComment(boolean wasComment) {
        this.wasComment = wasComment;
    }

    @Override
    public String toString() {
        return "Message{" +
                "wasComment=" + wasComment +
                ", subject='" + subject + '\'' +
                ", author='" + author + '\'' +
                ", parentId='" + parentId + '\'' +
                ", subbreditNamePrefixed='" + subbreditNamePrefixed + '\'' +
                ", body='" + body + '\'' +
                ", linkTitle='" + linkTitle + '\'' +
                ", name='" + name + '\'' +
                ", createdUtc=" + createdUtc +
                '}';
    }
}
