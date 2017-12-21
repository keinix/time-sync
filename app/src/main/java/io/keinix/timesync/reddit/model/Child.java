package io.keinix.timesync.reddit.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Child {

    @SerializedName("kind")
    @Expose
    private String kind;
    @SerializedName("data")
    @Expose
    private Data_ data;

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public Data_ getData() {
        return data;
    }

    public void setData(Data_ data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Child{" +
                "kind='" + kind + '\'' +
                ", data=" + data +
                '}';
    }
}
