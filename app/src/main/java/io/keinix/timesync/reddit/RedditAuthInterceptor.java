package io.keinix.timesync.reddit;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;

import java.io.IOException;

import io.keinix.timesync.Activities.AddAccountActivity;
import io.keinix.timesync.MainActivity;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;


public class RedditAuthInterceptor implements Interceptor {
    //TODO: implement the account manager crypto in this class

    private AccountManager mAccountManager;
    private String redditToken;
    private MainActivity mMainActivity;

    public RedditAuthInterceptor(AccountManager accountManager, MainActivity mainActivity) {
        mAccountManager = accountManager;
        mMainActivity = mainActivity;
        Account[] accounts = mAccountManager.getAccountsByType(RedditConstants.ACCOUNT_TYPE);

        if (accounts.length > 0) {
            redditToken = mAccountManager.peekAuthToken(accounts[0], RedditConstants.KEY_AUTH_TOKEN);
        } else redditToken = null;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        checkAccount();
        Request originalRequest = chain.request();

        Request.Builder newRequest = originalRequest
                .newBuilder()
                .addHeader("Authorization", "bearer " + redditToken)
                .addHeader("User-Agent", RedditConstants.REDDIT_USER_AGENT);

        return chain.proceed(newRequest.build());
    }

    public void checkAccount() {
        Account[] accounts = mAccountManager.getAccountsByType(RedditConstants.ACCOUNT_TYPE);
        if (accounts.length == 0) {
            mMainActivity.tempLogin();
        }
    }
}
