package io.keinix.timesync.reddit;

import android.accounts.AccountManager;
import android.content.Context;

import io.keinix.timesync.utils.AccountAuthenticator;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public abstract class ApiHelper {

    public static Api initApi(Context context) {

        AccountManager am = AccountManager.get(context);
        OkHttpClient.Builder client = new OkHttpClient.Builder()
                .authenticator(new TokenAuthenticator(am))
                .addInterceptor(new RedditAuthInterceptor(am, context));

        return new Retrofit.Builder()
                .baseUrl(RedditConstants.REDDIT_BASE_URL_OAUTH2)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client.build())
                .build()
                .create(Api.class);
    }
}
