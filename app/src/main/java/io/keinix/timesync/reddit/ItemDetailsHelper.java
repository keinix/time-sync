package io.keinix.timesync.reddit;

import io.keinix.timesync.reddit.model.Data_;

public abstract class ItemDetailsHelper {

    public static long getTimeSincePosted(long createdUtc) {
        long systemTime = System.currentTimeMillis() / 1000;
        return ((systemTime - createdUtc) / 60) / 60;
    }

    public static String getUserDetails(String author, Long createdUtc) {
        return "u/" + author + " \u2022 " + getTimeSincePosted(createdUtc) + "h";
    }

    public static String getPostDetails(Data_ post) {
        String domain = post.getDomain();
        if (domain.startsWith("self")) {
            domain = "self";
        }
        return post.getSubredditNamePrefixed() +
                " \u2022 " + getTimeSincePosted(post.getCreatedUtc()) + "h" + " \u2022 " +
                domain;
    }

    //parse the vote type before sending it to the RedditVoteHelper Class
    public static int parseVoteType(Boolean isLiked) {
        if (isLiked != null) {
            if (isLiked) {
                return RedditVoteHelper.VALUE_UPVOTED;
            } else {
                return RedditVoteHelper.VALUE_DOWNVOTED;
            }
        } else {
            return RedditVoteHelper.VALUE_NOT_VOTED; }
    }

    public static String getReplyDetails(String author, long createdUtc) {
        return "u/" + author + " \u2022 " + getTimeSincePosted(createdUtc);
    }
}
