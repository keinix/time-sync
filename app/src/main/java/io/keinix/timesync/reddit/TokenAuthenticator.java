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

    public TokenAuthenticator(AccountManager accountManager) {
        mAccountManager = accountManager;
    }

    @Nullable
    @Override
    public Request authenticate(Route route, Response originalResponse) throws IOException {
        Log.d(TAG, "TokenAuthenticatorCalled");
        if (!hasRedditAccount()) { return null; }

        retrofit2.Response<RedditAccessToken> refreshResponse = getTokenRefreshCall().execute();
        if (refreshResponse.isSuccessful()) {
            Log.d(TAG, "Response successful:  RedditAccessToken: " + refreshResponse.body().toString());
            updateAccountManager(refreshResponse);
            return getNewRequest(originalResponse);
        } else {
            Log.d(TAG, "Response  NOT successful");
        }
        return null;
    }


    private retrofit2.Call<RedditAccessToken> getTokenRefreshCall()  {
        Account[] accounts = mAccountManager.getAccountsByType(RedditConstants.ACCOUNT_TYPE);
        if (accounts.length > 0) {
            mRedditAccount = accounts[0];
        }

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


        Map<String, String> fields = new HashMap<>();
        fields.put("grant_type", "refresh_token");
        fields.put("refresh_token", refreshToken);
        fields.put("User-Agent", RedditConstants.REDDIT_USER_AGENT);

        return api.login(headers, fields);
    }

    private void updateAccountManager(retrofit2.Response<RedditAccessToken> response) {
        mAccountManager.invalidateAuthToken(RedditConstants.ACCOUNT_TYPE, RedditConstants.KEY_AUTH_TOKEN);
        mAccountManager.setAuthToken(mRedditAccount, RedditConstants.KEY_AUTH_TOKEN, response.body().getAccess_token());
    }

    private Request getNewRequest(Response originalResponse) {
        String newAuthToken = mAccountManager.peekAuthToken(mRedditAccount, RedditConstants.KEY_AUTH_TOKEN);


        Request newRequest =originalResponse
                .request()
                .newBuilder()
                .header("Authorization", "bearer " + newAuthToken)
                .header("User-Agent", RedditConstants.REDDIT_USER_AGENT)
                .build();

        Log.d(TAG, "New Request: " + newRequest.toString());
        Log.d(TAG, "New Request: " + newRequest.url());

        Log.d(TAG, "OriginalRequest: " + originalResponse.request().toString());
        Log.d(TAG, "OriginalRequest: " + originalResponse.request().url());
        return newRequest;
    }

    public boolean hasRedditAccount() {
        Account[] accounts = mAccountManager.getAccountsByType(RedditConstants.ACCOUNT_TYPE);
        if (accounts.length > 0) {
            return true;
        } else {return false; }
    }
}
