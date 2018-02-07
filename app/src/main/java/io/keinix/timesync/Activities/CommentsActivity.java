package io.keinix.timesync.Activities;

import android.accounts.AccountManager;
import android.content.Intent;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import io.keinix.timesync.Fragments.CommentsFragment;
import io.keinix.timesync.R;
import io.keinix.timesync.reddit.Api;
import io.keinix.timesync.reddit.RedditAuthInterceptor;
import io.keinix.timesync.reddit.RedditConstants;
import io.keinix.timesync.reddit.TokenAuthenticator;
import io.keinix.timesync.reddit.model.comment.Comment;
import io.keinix.timesync.reddit.model.comment.CommentBase;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class CommentsActivity extends AppCompatActivity implements CommentsFragment.CommentsInterface{

    public static final String TAG_COMMENTS_FRAGMENT = "TAG_COMMENTS_FRAGMENT";
    public static final String KEY_COMMENTS_LAYOUT_TYPE = "KEY_COMMENTS_LAYOUT_TYPE";

    public static final String KEY_IMAGE_URL = "KEY_IMAGE_URL";
    public static final String KEY_POST_DETAILS = "KEY_POST_DETAILS";
    public static final String KEY_POST_TITLE = "KEY_POST_TITLE";
    public static final String KEY_POST_SUBREDDIT = "KEY_POST_SUBREDDIT";
    public static final String KEY_POST_ID = "KEY_POST_ID";
    public static final String KEY_POST_ARTICLE = "KEY_POST_ARTICLE";
    public static final String KEY_POST_SUBREDDIT_NO_PREFIX ="KEY_POST_SUBREDDIT_NO_PREFIX";

    public static final String VALUE_IMAGE_COMMENTS_LAYOUT = "VALUE_IMAGE_COMMENTS_LAYOUT";
    public static final String VALUE_GIF_COMMENTS_LAYOUT = "VALUE_GIF_COMMENTS_LAYOUT";
    public static final String VALUE_VIDEO_COMMENTS_LAYOUT = "VALUE_VIDEO_COMMENTS_LAYOUT";
    public static final String VALUE_TEXT_COMMENTS_LAYOUT = "VALUE_TEXT_COMMENTS_LAYOUT";
    private static final String TAG = CommentsActivity.class.getSimpleName();

    private Api mApi;
    private AccountManager mAccountManager;
    private String mPostLayoutType;
    private String mPostTitle;
    private String mPostDetails;
    private String mPostID;
    private String mPostSubreddit;
    private String mPostArticle;
    private String mPostSubredditNoPrefix;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);
        mAccountManager = AccountManager.get(this);
        unPackIntent();
        initApi();

        CommentsFragment savedFragment = (CommentsFragment) getSupportFragmentManager().findFragmentByTag(TAG_COMMENTS_FRAGMENT);

        if (savedFragment == null) {
            CommentsFragment commentsFragment = new CommentsFragment();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.add(R.id.commentsPlaceHolder, commentsFragment, TAG_COMMENTS_FRAGMENT);
            fragmentTransaction.commit();
        }
    }

    public void initApi() {
        OkHttpClient.Builder client = new OkHttpClient.Builder()
                .authenticator(new TokenAuthenticator(mAccountManager))
                .addInterceptor(new RedditAuthInterceptor(mAccountManager, this));

        mApi = new Retrofit.Builder()
                .baseUrl(RedditConstants.REDDIT_BASE_URL_OAUTH2)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client.build())
                .build()
                .create(Api.class);
    }

    private void unPackIntent() {
        Intent intent = getIntent();
        mPostTitle = intent.getStringExtra(KEY_POST_TITLE);
        mPostDetails = intent.getStringExtra(KEY_POST_DETAILS);
        mPostID = intent.getStringExtra(KEY_POST_ID);
        mPostSubreddit = intent.getStringExtra(KEY_POST_SUBREDDIT);
        mPostArticle = intent.getStringExtra(KEY_POST_ARTICLE);
        mPostSubredditNoPrefix = intent.getStringExtra(KEY_POST_SUBREDDIT_NO_PREFIX);
    }

    public List<Comment> parseComments(JsonObject json) {
        Gson gson = new Gson();
        List<Comment> comments = new ArrayList<>();

        JsonElement commentRepliesJson = getRepliesJsonElement(json);
        JsonElement commentJson = getCommentJsonElement(json);

        do {
            if (commentRepliesJson.isJsonPrimitive()) {
                comments.add(gson.fromJson(commentJson, Comment.class));
            } else {
                comments.add(gson.fromJson(commentJson, Comment.class));
                commentJson = commentRepliesJson;
                commentRepliesJson = getRepliesJsonElement(commentJson.getAsJsonObject());
            }

        } while (!commentRepliesJson.isJsonPrimitive());

        return comments;
    }

    public JsonElement getCommentJsonElement(JsonObject json) {
        return json.getAsJsonObject("data")
                .getAsJsonArray("children")
                .get(0).getAsJsonObject()
                .getAsJsonObject("data");
    }

    public JsonElement getRepliesJsonElement(JsonObject json) {
        return json.getAsJsonObject("data")
                .getAsJsonArray("children")
                .get(0).getAsJsonObject()
                .getAsJsonObject("data")
                .get("replies");
    }

    @Override
    public Call<JsonArray> getComments() {
        return mApi.getComments(mPostSubredditNoPrefix, mPostArticle, mPostArticle);
    }
}
