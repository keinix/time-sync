package io.keinix.timesync.reddit.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class VoteResult {
    @SerializedName("dir")
    @Expose
    private int voteType;
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("rank")
    @Expose
    private int rank;

    public int getVoteType() {
        return voteType;
    }

    public void setVoteType(int voteType) {
        this.voteType = voteType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    @Override
    public String toString() {
        return "VoteResult{" +
                "voteType=" + voteType +
                ", id='" + id + '\'' +
                ", rank=" + rank +
                '}';
    }
}
