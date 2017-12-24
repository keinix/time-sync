package io.keinix.timesync.reddit;

import android.accounts.AccountManager;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;


public class RedditTokenInterceptor implements Interceptor {
    //TODO: implement the account manager crypto in this class

    AccountManager mAccountManager;

    public RedditTokenInterceptor(AccountManager accountManager) {
        mAccountManager = accountManager;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        return null;
    }
}
