package io.keinix.timesync.reddit.model.comment;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Comment {
    //TODO: populate this model

//    @SerializedName("replies")
//    @Expose
//    private Replies mReplies;
    @SerializedName("link_id")
    @Expose
    private String linkId;
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("gilded")
    @Expose
    private int gilded;
    @SerializedName("author")
    @Expose
    private String author;
    @SerializedName("ups")
    @Expose
    private int ups;
    @SerializedName("score")
    @Expose
    private int score;
    @SerializedName("downs")
    @Expose
    private int downs;
    @SerializedName("body")
    @Expose
    private String body;
    @SerializedName("collapsed")
    @Expose
    private Boolean isCollasped;
    @SerializedName("is_submitter")
    @Expose
    private Boolean isSubmitter;
    @SerializedName("created_utc")
    @Expose
    private long createdUtc;
    @SerializedName("depth")
    @Expose
    private int depth;

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    //    public Object getReplies() {
//        return mReplies;
//    }
//
//    public void setReplies(Replies replies) {
//        mReplies = replies;
//    }

    public String getLinkId() {
        return linkId;
    }

    public void setLinkId(String linkId) {
        this.linkId = linkId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getGilded() {
        return gilded;
    }

    public void setGilded(int gilded) {
        this.gilded = gilded;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getUps() {
        return ups;
    }

    public void setUps(int ups) {
        this.ups = ups;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getDowns() {
        return downs;
    }

    public void setDowns(int downs) {
        this.downs = downs;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Boolean getCollasped() {
        return isCollasped;
    }

    public void setCollasped(Boolean collasped) {
        isCollasped = collasped;
    }

    public Boolean getSubmitter() {
        return isSubmitter;
    }

    public void setSubmitter(Boolean submitter) {
        isSubmitter = submitter;
    }

    public long getCreatedUtc() {
        return createdUtc;
    }

    public void setCreatedUtc(long createdUtc) {
        this.createdUtc = createdUtc;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "linkId='" + linkId + '\'' +
                ", id='" + id + '\'' +
                ", gilded=" + gilded +
                ", author='" + author + '\'' +
                ", ups=" + ups +
                ", score=" + score +
                ", downs=" + downs +
                ", body='" + body + '\'' +
                ", isCollasped=" + isCollasped +
                ", isSubmitter=" + isSubmitter +
                ", createdUtc=" + createdUtc +
                ", depth=" + depth +
                '}';
    }
}
