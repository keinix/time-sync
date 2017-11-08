package io.keinix.timesync;

public final class AccountConstants {

    //<------------AccountManager constants------------->
    public static final String ACCOUNT_TYPE = "account_type";
    public static final String AUTH_TOKEN_TYPE = "auth_token_type";
    public static final String IS_ADDING_NEW_ACCOUNT = "is_adding_new_account";



    //<----------------- REDDIT API --------------------->
    private static final String REDDIT_AUTH_URL =
            "https://www.reddit.com/api/v1/authorize.compact?client_id=%s" +
                    "&response_type=code&state=%s&redirect_uri=%s&" +
                    "duration=permanent&scope=identity";
    public static final String REDDIT_CLIENT_ID = "gX4PnW7oHz7dgQ";

    public static final String REDDIT_REDIRECT_URL = "https://www.keinix.io/timesync";

    //TODO: check if this needs to be changed to actual random string
    public static final String REDDIT_STATE = "RANDOM_STRING";

    public static final String REDDIT_ACCESS_TOKEN = "https://www.reddit.com/api/v1/access_token";

    public static final String REDDIT_URL = String.format(REDDIT_AUTH_URL, REDDIT_CLIENT_ID, REDDIT_STATE, REDDIT_REDIRECT_URL);
}
