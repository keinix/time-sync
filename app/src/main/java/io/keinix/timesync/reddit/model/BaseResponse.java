package io.keinix.timesync.reddit.model;

public class BaseResponse<T> {

    private RedditAccessToken redditAccessToken;
    private VoteResult voteResult;

    public RedditAccessToken getRedditAccessToken() {
        return redditAccessToken;
    }

    public void setRedditAccessToken(RedditAccessToken redditAccessToken) {
        this.redditAccessToken = redditAccessToken;
    }

    public VoteResult getVoteResult() {
        return voteResult;
    }

    public void setVoteResult(VoteResult voteResult) {
        this.voteResult = voteResult;
    }

    @Override
    public String toString() {
        return "BaseResponse{" +
                "redditAccessToken=" + redditAccessToken +
                ", voteResult=" + voteResult +
                '}';
    }
}
