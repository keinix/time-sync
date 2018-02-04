package io.keinix.timesync.reddit.model.comment;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Replies {

    @SerializedName("kind")
    @Expose
    private String kind;
    @SerializedName("data")
    private CommentData mCommentData;

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public CommentData getCommentData() {
        return mCommentData;
    }

    public void setCommentData(CommentData commentData) {
        mCommentData = commentData;
    }

    @Override
    public String toString() {
        return "CommentBase{" +
                "kind='" + kind + '\'' +
                ", mCommentData=" + mCommentData +
                '}';
    }

}
