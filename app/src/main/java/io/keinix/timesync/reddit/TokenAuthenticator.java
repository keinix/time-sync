package io.keinix.timesync.reddit;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.support.annotation.Nullable;
import android.util.Base64;
import android.util.Log;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import io.keinix.timesync.reddit.model.RedditAccessToken;
import okhttp3.Authenticator;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class TokenAuthenticator implements Authenticator {

    public static final String TAG = TokenAuthenticator.class.getSimpleName();
    AccountManager mAccountManager;
    private Account mRedditAccount;
    private boolean previouslyAttemptedRefresh = false;

    public TokenAuthenticator(AccountManager accountManager) {
        mAccountManager = accountManager;
    }

    @Nullable
    @Override
    public Request authenticate(Route route, Response originalResponse) throws IOException {
        Log.d(TAG, "TokenAuthenticatorCalled");

        if (!previouslyAttemptedRefresh) {
            retrofit2.Response<RedditAccessToken> refreshResponse = getTokenRefreshCall().execute();
            if (refreshResponse.isSuccessful()) {
                Log.d(TAG, "Response successful:  RedditAccessToken: " + refreshResponse.body().toString());
                updateAccountManager(refreshResponse);
                previouslyAttemptedRefresh = false;
                return getNewRequest(originalResponse);
            }
        }
        Log.d(TAG, "Response  NOT successful");
        return null;
    }


    private retrofit2.Call<RedditAccessToken> getTokenRefreshCall()  {
        Account[] accounts = mAccountManager.getAccountsByType(RedditConstants.ACCOUNT_TYPE);
        mRedditAccount = accounts[0];
        String refreshToken = mAccountManager.getUserData(mRedditAccount, RedditConstants.KEY_REFRESH_TOKEN);

        Api api = new Retrofit.Builder()
                .baseUrl(RedditConstants.REDDIT_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(Api.class);

        String authString = RedditConstants.REDDIT_CLIENT_ID + ":";
        String encodedAuthString = Base64.encodeToString(authString.getBytes(), Base64.NO_WRAP);

        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Basic " + encodedAuthString);
        headers.put("User-Agent", RedditConstants.REDDIT_USER_AGENT);

        Map<String, String> fields = new HashMap<>();
        fields.put("grant_type", "refresh_token");
        fields.put("refresh_token", refreshToken);

        return api.login(headers, fields);
    }

    private void updateAccountManager(retrofit2.Response<RedditAccessToken> response) {
        mAccountManager.invalidateAuthToken(RedditConstants.ACCOUNT_TYPE, RedditConstants.KEY_AUTH_TOKEN);
        mAccountManager.setAuthToken(mRedditAccount, RedditConstants.KEY_AUTH_TOKEN, response.body().getAccess_token());
        mAccountManager.setUserData(mRedditAccount, RedditConstants.KEY_REFRESH_TOKEN, response.body().getRefresh_token());
        Log.d(TAG, "RefreshToken: " + response.body().getRefresh_token());
    }

    private Request getNewRequest(Response originalResponse) {
        String newAuthToken = mAccountManager.peekAuthToken(mRedditAccount, RedditConstants.KEY_AUTH_TOKEN);
        previouslyAttemptedRefresh = true;


        return originalResponse
                .request()
                .newBuilder()
                .addHeader("Authorization", "bearer " + newAuthToken)
                .addHeader("User-Agent", RedditConstants.REDDIT_USER_AGENT)
                .build();
    }
}
