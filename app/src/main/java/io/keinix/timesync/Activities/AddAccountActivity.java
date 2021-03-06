package io.keinix.timesync.Activities;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.keinix.timesync.MainActivity;
import io.keinix.timesync.R;
import io.keinix.timesync.reddit.Api;
import io.keinix.timesync.reddit.RedditAuthInterceptor;
import io.keinix.timesync.reddit.RedditConstants;
import io.keinix.timesync.reddit.TokenAuthenticator;
import io.keinix.timesync.reddit.model.RedditAccessToken;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AddAccountActivity extends AccountAuthenticatorActivity {

    public static final String TAG = AddAccountActivity.class.getSimpleName();
    private Retrofit mRetrofit;
    private AccountManager mAccountManager;

    public static final String KEY_NONCE = "KEY_NONCE";

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
        String nonce = UUID.randomUUID().toString();
        storeNonce(nonce);
        String url = RedditConstants.REDDIT_URL.replace(RedditConstants.REDDIT_STATE, nonce);
        Log.d(TAG, "Nonce: " + nonce);
        Log.d(TAG, "Url: " + url);

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }

    private void storeNonce(String nonce) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_NONCE, nonce);
        editor.apply();
    }

    private void redditConsentCallback() {

        if(getIntent()!= null && Intent.ACTION_VIEW.equals(getIntent().getAction())) {
            Uri uri = getIntent().getData();
            Log.d(TAG, "URI: " + uri.getAuthority());
            if(uri.getQueryParameter("error") != null) {
                if (uri.getQueryParameter("error").equals("access_denied")) {
                    Toast.makeText(this, ":( maybe next time then...", Toast.LENGTH_LONG).show();
                }
                String error = uri.getQueryParameter("error");
                Log.e(TAG, "An error has occurred : " + error);
            } else {
                String state = uri.getQueryParameter("state");
                String afterNonce = getNonce();
                Log.d(TAG, "AfterState: " + state);
                Log.d(TAG, "AfterNonce: " + afterNonce);
                if (state.equals(afterNonce)) {
                    String code = uri.getQueryParameter("code");
                    getAccessToken(code);
                } else {
                    Toast.makeText(this, "nonce does not match", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void getAccessToken(String code) {

        if (mRetrofit == null) {
            mRetrofit = new Retrofit.Builder()
                    .baseUrl(RedditConstants.REDDIT_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        Api api = mRetrofit.create(Api.class);

        String authString = RedditConstants.REDDIT_CLIENT_ID + ":";
        String encodedAuthString = Base64.encodeToString(authString.getBytes(), Base64.NO_WRAP);

        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Basic " + encodedAuthString);

        Map<String, String> fields = new HashMap<>();
        fields.put("grant_type", "authorization_code");
        fields.put("code", code);
        fields.put("redirect_uri", RedditConstants.REDDIT_REDIRECT_URL);
        fields.put("User-Agent", RedditConstants.REDDIT_USER_AGENT);


        Call<RedditAccessToken> call = api.login(headers, fields);
        call.enqueue(new Callback<RedditAccessToken>() {
            @Override
            public void onResponse(Call<RedditAccessToken> call, Response<RedditAccessToken> response) {
                Log.d(TAG, "body: " + response.body().toString());
                Log.d(TAG, "response: " + response.toString());

                if (response.body().getError() == null) {
                    mAccountManager = AccountManager.get(AddAccountActivity.this);
                    Bundle userdata = new Bundle();

                    Account account = new Account(RedditConstants.ACCOUNT_NAME, RedditConstants.ACCOUNT_TYPE);
                    mAccountManager.addAccountExplicitly(account, "123", userdata);
                    mAccountManager.setAuthToken(account, RedditConstants.KEY_AUTH_TOKEN, response.body().getAccess_token());
                    mAccountManager.setUserData(account, RedditConstants.KEY_REFRESH_TOKEN, response.body().getRefresh_token());
                    launchFeedActivity();
                } else {
                    Toast.makeText(AddAccountActivity.this, "There was an error", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<RedditAccessToken> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t.getMessage());
            }
        });
    }

    private void launchFeedActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //TODO: when adding another API add a check here to determine the respnce type in the intent or redirect Uri
        redditConsentCallback();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "OnDestroy Called");
        super.onDestroy();
    }

    private String getNonce() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        return prefs.getString(KEY_NONCE, "random");
    }
}
