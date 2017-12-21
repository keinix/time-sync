package io.keinix.timesync.reddit.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RedditFeed {
    @SerializedName("kind")
    @Expose
    private String kind;
    @SerializedName("data")
    @Expose
    private Data data;

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "RedditFeed{" +
                "kind='" + kind + '\'' +
                ", data=" + data +
                '}';
    }
}
