package io.keinix.timesync.reddit.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Media {

    @SerializedName("reddit_video")
    @Expose
    private RedditVideo RedditVideo;

    public io.keinix.timesync.reddit.model.RedditVideo getRedditVideo() {
        return RedditVideo;
    }

    public void setRedditVideo(io.keinix.timesync.reddit.model.RedditVideo redditVideo) {
        RedditVideo = redditVideo;
    }

    @Override
    public String toString() {
        return "Media{" +
                "RedditVideo=" + RedditVideo +
                '}';
    }
}
