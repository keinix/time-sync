package io.keinix.timesync;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;


public class RedditAccountAuthenticator extends AbstractAccountAuthenticator {

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


    private Context mContext;

    public RedditAccountAuthenticator(Context context) {
        super(context);
        mContext = context;
    }

    @Override
    public Bundle editProperties(AccountAuthenticatorResponse accountAuthenticatorResponse, String s) {
        return null;
    }

    @Override
    public Bundle addAccount(AccountAuthenticatorResponse accountAuthenticatorResponse,
                             String accountType, String authTokenType, String[] requiredFeatures,
                             Bundle options) throws NetworkErrorException {

        final Intent intent = new Intent(mContext, AuthenticatorActivity.class);
        intent.putExtra("ACCOUNT_TYPE", accountType);
        intent.putExtra("AUTH_TOKEN_TYPE", authTokenType);
        intent.putExtra("is_adding_new_account", true);
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, accountAuthenticatorResponse);

        final Bundle bundle = new Bundle();
        bundle.putParcelable(AccountManager.KEY_INTENT, intent);
        return bundle;
    }

    @Override
    public Bundle confirmCredentials(AccountAuthenticatorResponse accountAuthenticatorResponse, Account account, Bundle bundle) throws NetworkErrorException {
        return null;
    }

    @Override
    public Bundle getAuthToken(AccountAuthenticatorResponse accountAuthenticatorResponse,
                               Account account, String authTokenType, Bundle options) throws NetworkErrorException {

        // check for token
        final AccountManager am = AccountManager.get(mContext);
        String authToken = am.peekAuthToken(account, authTokenType);

        // AccountAuthenticator as the token
        if (!TextUtils.isEmpty(authToken)) {
            final Bundle results = new Bundle();
            results.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
            results.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type);
            results.putString(AccountManager.KEY_AUTHTOKEN, authToken);
            return results;
        }

        // no token saved previously
        final Intent intent = new Intent(mContext, AuthenticatorActivity.class);
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, accountAuthenticatorResponse);
        intent.putExtra("ACCOUNT_TYPE", account.type);
        intent.putExtra("AUTH_TOKEN_TYPE", authTokenType);
        final Bundle bundle = new Bundle();
        bundle.putParcelable(AccountManager.KEY_INTENT, intent);


    }

    @Override
    public String getAuthTokenLabel(String s) {
        return null;
    }

    @Override
    public Bundle updateCredentials(AccountAuthenticatorResponse accountAuthenticatorResponse, Account account, String s, Bundle bundle) throws NetworkErrorException {
        return null;
    }

    @Override
    public Bundle hasFeatures(AccountAuthenticatorResponse accountAuthenticatorResponse, Account account, String[] strings) throws NetworkErrorException {
        return null;
    }
}
