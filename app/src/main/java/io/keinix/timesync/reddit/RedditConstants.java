package io.keinix.timesync.reddit;

public final class RedditConstants {

    // for logging in
    //TODO: add mutiple scopes to the URL
    public static final String REDDIT_AUTH_URL =
            "https://www.reddit.com/api/v1/authorize.compact?client_id=%s" +
                    "&response_type=code&state=%s&redirect_uri=%s&" +
                    "duration=permanent&scope=identity+read+mysubreddits+vote+save";
    public static final String REDDIT_CLIENT_ID = "gX4PnW7oHz7dgQ";

    public static final String REDDIT_REDIRECT_URL = "io.keinix://www.keinix.io/timesync";

    //TODO: check if this needs to be changed to actual random string
    public static final String REDDIT_STATE = "RANDOM_STRING";

    public static final String REDDIT_BASE_URL = "https://www.reddit.com/api/v1/";

    public static final String REDDIT_URL = String.format(REDDIT_AUTH_URL, REDDIT_CLIENT_ID, REDDIT_STATE, REDDIT_REDIRECT_URL);

    // for AccountManager
    public static final String ACCOUNT_TYPE = "io.keinix";

    public static final String ACCOUNT_NAME = "TimeSync (Reddit)";

    public static final String KEY_AUTH_TOKEN = "KEY_AUTH_TOKEN";

    public static final String KEY_REFRESH_TOKEN = "KEY_REFRESH_TOKEN";

    public static final String KEY_EXPIRES_IN = "KEY_EXPIRES_IN";

    public static final String KEY_NAME = "KEY_NAME";


    // for Http Requests
    public static final String REDDIT_BASE_URL_OAUTH2 = "https://oauth.reddit.com/";

    public static final String REDDIT_USER_AGENT = "android:io.keinix.timeSync:v0.1 (by /u/keinix)";

    // for Api parameters
    public static final String UP_VOTE = String.valueOf(1);

    public static final String DOWN_VOTE = String.valueOf(-1);

    public static final String UN_VOTE = String.valueOf(0);

}
