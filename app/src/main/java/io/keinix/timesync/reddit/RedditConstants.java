package io.keinix.timesync.reddit;

public final class RedditConstants {

    // constants used while loging in and making queries

    public static final String REDDIT_AUTH_URL =
            "https://www.reddit.com/api/v1/authorize.compact?client_id=%s" +
                    "&response_type=code&state=%s&redirect_uri=%s&" +
                    "duration=permanent&scope=identity";
    public static final String REDDIT_CLIENT_ID = "gX4PnW7oHz7dgQ";

    public static final String REDDIT_REDIRECT_URL = "https://www.keinix.io/timesync";

    //TODO: check if this needs to be changed to actual random string
    public static final String REDDIT_STATE = "RANDOM_STRING";

    public static final String REDDIT_BASE_URL = "https://www.reddit.com/api/v1/";

    public static final String REDDIT_URL = String.format(REDDIT_AUTH_URL, REDDIT_CLIENT_ID, REDDIT_STATE, REDDIT_REDIRECT_URL);

}
