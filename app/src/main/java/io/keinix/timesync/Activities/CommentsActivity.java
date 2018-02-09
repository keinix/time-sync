package io.keinix.timesync.Activities;

import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import io.keinix.timesync.Fragments.CommentsFragment;
import io.keinix.timesync.R;
import io.keinix.timesync.reddit.Api;
import io.keinix.timesync.reddit.RedditAuthInterceptor;
import io.keinix.timesync.reddit.RedditConstants;
import io.keinix.timesync.reddit.TokenAuthenticator;
import io.keinix.timesync.reddit.model.comment.Comment;
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
    public static final String KEY_SELF_TEXT = "KEY_SELF_TEXT";

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

    @Override
    public List<Comment> createCommentTree(JsonElement baseCommentElement) {
        int commentArrayIndex = baseCommentElement.getAsJsonArray().size() == 1 ? 0 : 1;
        List<Comment> tempCommentTree = new ArrayList<>();
        Log.d(TAG, "Json Length: " + baseCommentElement.getAsJsonArray().size());

        //TODO: not being parsed correctly when size == 1
        JsonArray commentArray = baseCommentElement
                .getAsJsonArray()
                .get(commentArrayIndex)
                .getAsJsonObject()
                .getAsJsonObject("data")
                .getAsJsonArray("children");

        for (JsonElement comment : commentArray) {
            tempCommentTree.addAll(parseComments(comment.getAsJsonObject()));
        }
        return tempCommentTree;
    }

    @Override
    public CommentsActivity getContext() {
        return this;
    }


    public List<Comment> parseComments(JsonObject json) {
        Gson gson = new Gson();
        List<Comment> comments = new ArrayList<>();
        Deque<JsonObject> commentStack = new ArrayDeque<>();
        commentStack.add(json.getAsJsonObject("data"));

        do {
            JsonObject currentComment = commentStack.pop();
            JsonElement currentReplies = currentComment.get("replies");

            if (currentReplies != null) {
                if (currentReplies.isJsonPrimitive()) {
                    comments.add(gson.fromJson(currentComment, Comment.class));
                } else {
                    comments.add(gson.fromJson(currentComment, Comment.class));
                    commentStack.addAll(getReplyChildren(currentReplies.getAsJsonObject()));
                }
            }
        } while (!commentStack.isEmpty());
        return comments;
    }

    public List<JsonObject> getReplyChildren(JsonObject json) {
        JsonArray repliesArray = json.getAsJsonObject("data").getAsJsonArray("children");
        List<JsonObject> replyChildren = new ArrayList<>();

        for (JsonElement reply : repliesArray) {
            replyChildren.add(reply.getAsJsonObject().getAsJsonObject("data"));
        }
        return replyChildren;
    }

    @Override
    public Call<JsonElement> getComments() {
        return mApi.getComments(mPostSubredditNoPrefix, mPostArticle, mPostArticle);
    }
}
