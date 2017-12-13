package io.keinix.timesync.reddit;

import java.util.Map;

import io.keinix.timesync.reddit.model.RedditAccessToken;
import retrofit2.Call;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;

public interface Api {

    @POST("access_token/")
    Call<RedditAccessToken> login (
            @HeaderMap Map<String, String> headers
    );
}
