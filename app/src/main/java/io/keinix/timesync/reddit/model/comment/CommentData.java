package io.keinix.timesync.reddit.model.comment;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CommentData {
    @SerializedName("children")
    @Expose
    List<CommentChild> mCommentChildren;

    public List<CommentChild> getCommentChildren() {
        return mCommentChildren;
    }

    public void setCommentChildren(List<CommentChild> commentChildren) {
        mCommentChildren = commentChildren;
    }

    @Override
    public String toString() {
        return "CommentData{" +
                "mCommentChildren=" + mCommentChildren +
                '}';
    }
}
