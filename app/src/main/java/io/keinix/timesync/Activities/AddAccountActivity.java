package io.keinix.timesync.Activities;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.keinix.timesync.R;
import io.keinix.timesync.reddit.Api;
import io.keinix.timesync.reddit.RedditConstants;
import io.keinix.timesync.reddit.model.RedditAccessToken;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AddAccountActivity extends AppCompatActivity {

    Retrofit mRetrofit;

    @BindView(R.id.redditLoginButton) Button mRedditLoginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_account);
        ButterKnife.bind(this);

        mRedditLoginButton.setOnClickListener(v -> {
            redditLogin();
        });
    }

    private void redditLogin() {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(RedditConstants.REDDIT_URL));
        startActivity(intent);
    }

    private void redditConsentCallback() {

        // TODO: add check for redirect Uri
        if(getIntent()!= null && Intent.ACTION_VIEW.equals(getIntent().getAction())) {
            Uri uri = getIntent().getData();
            if(uri.getQueryParameter("error") != null) {
                String error = uri.getQueryParameter("error");
                Log.e("findme", "An error has occurred : " + error);
            } else {
                String state = uri.getQueryParameter("state");
                if(state.equals(RedditConstants.REDDIT_STATE)) {
                    String code = uri.getQueryParameter("code");
                    Toast.makeText(this, "we did it", Toast.LENGTH_SHORT).show();
                    Log.d("findme", "REDDIT COD: " + code);

                    getAccessToken(code);
                }
            }
        }

    }

    private void getAccessToken(String code) {

        if (mRetrofit == null) {
            mRetrofit = new Retrofit.Builder()
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        Api api = mRetrofit.create(Api.class);

        Map<String, String> headers = new HashMap<>();
        headers.put("user", RedditConstants.REDDIT_CLIENT_ID);
        headers.put("password", "");

        Map<String, String> queryParameters = new HashMap<>();
        queryParameters.put("grant_type" , "refresh_token");
        queryParameters.put("code", code);
        queryParameters.put("redirect_uri", RedditConstants.REDDIT_REDIRECT_URL);


        Call<RedditAccessToken> call = api.login(headers, queryParameters);
        call.enqueue(new Callback<RedditAccessToken>() {
            @Override
            public void onResponse(Call<RedditAccessToken> call, Response<RedditAccessToken> response) {
                Log.d()
            }

            @Override
            public void onFailure(Call<RedditAccessToken> call, Throwable t) {

            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        redditConsentCallback();
    }

}
