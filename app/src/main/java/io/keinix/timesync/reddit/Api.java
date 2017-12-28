package io.keinix.timesync.reddit;

import java.util.Map;

import io.keinix.timesync.reddit.model.RedditAccessToken;
import io.keinix.timesync.reddit.model.RedditFeed;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface Api {

    @FormUrlEncoded
    @POST("access_token/")
    Call<RedditAccessToken> login (
        @HeaderMap Map<String, String> headers,
        @FieldMap Map<String, String> fields
    );

    // use RedditAuthInterceptor
    @GET(".")
    Call<RedditFeed> getFeed ();
}
