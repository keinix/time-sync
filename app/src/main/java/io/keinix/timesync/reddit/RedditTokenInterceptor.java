package io.keinix.timesync.reddit;

import android.accounts.Account;
import android.accounts.AccountManager;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;


public class RedditTokenInterceptor implements Interceptor {
    //TODO: implement the account manager crypto in this class

    AccountManager mAccountManager;
    private String redditToken;

    public RedditTokenInterceptor(AccountManager accountManager) {
        mAccountManager = accountManager;
        Account[] accounts = mAccountManager.getAccountsByType(RedditConstants.ACCOUNT_TYPE);
        redditToken = mAccountManager.peekAuthToken(accounts[0], RedditConstants.KEY_AUTH_TOKEN);
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();

        Request.Builder newRequest = originalRequest
                .newBuilder()
                .addHeader("Authorization", "bearer " + redditToken)
                .addHeader("User-Agent", RedditConstants.REDDIT_USER_AGENT);

        return chain.proceed(newRequest.build());


    }
}
