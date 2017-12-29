package io.keinix.timesync.reddit.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Variants {

    @SerializedName("gif")
    @Expose
    private Gif gif;
    @SerializedName("mp4")
    @Expose
    private Mp4 mp4;

    public Gif getGif() {
        return gif;
    }

    public void setGif(Gif gif) {
        this.gif = gif;
    }

    public Mp4 getMp4() {
        return mp4;
    }

    public void setMp4(Mp4 mp4) {
        this.mp4 = mp4;
    }

    @Override
    public String toString() {
        return "Variants{" +
                "gif=" + gif +
                ", mp4=" + mp4 +
                '}';
    }
}
