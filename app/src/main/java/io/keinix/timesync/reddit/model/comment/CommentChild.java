package io.keinix.timesync.reddit.model.comment;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CommentChild {

    @SerializedName("kind")
    @Expose
    private String kind;

    @SerializedName("data")
    @Expose
    private Comment mComment;

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public Comment getComment() {
        return mComment;
    }

    public void setComment(Comment comment) {
        mComment = comment;
    }

    @Override
    public String toString() {
        return "CommentChild{" +
                "kind='" + kind + '\'' +
                ", mComment=" + mComment +
                '}';
    }
}

