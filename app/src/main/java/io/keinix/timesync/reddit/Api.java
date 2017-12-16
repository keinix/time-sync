package io.keinix.timesync.reddit;

import java.util.Map;

import io.keinix.timesync.reddit.model.RedditAccessToken;
import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;

public interface Api {

    @FormUrlEncoded
    @POST("access_token/")
    Call<RedditAccessToken> login (
            @HeaderMap Map<String, String> headers,
            @FieldMap Map<String, String> fields
    );
}
