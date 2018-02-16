package io.keinix.timesync.Activities;

import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import io.keinix.timesync.Fragments.CommentsFragment;
import io.keinix.timesync.Fragments.CommentsFragmentVideo;
import io.keinix.timesync.MainActivity;
import io.keinix.timesync.R;
import io.keinix.timesync.reddit.Api;
import io.keinix.timesync.reddit.RedditAuthInterceptor;
import io.keinix.timesync.reddit.RedditConstants;
import io.keinix.timesync.reddit.TokenAuthenticator;
import io.keinix.timesync.reddit.model.comment.Comment;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.noties.markwon.Markwon;
import ru.noties.markwon.SpannableConfiguration;
import ru.noties.markwon.renderer.SpannableRenderer;
import ru.noties.markwon.spans.SpannableTheme;


public class CommentsActivity extends AppCompatActivity implements CommentsFragment.CommentsInterface{

    public static final String TAG_COMMENTS_NORMAL_FRAGMENT = "TAG_COMMENTS_NORMAL_FRAGMENT";
    public static final String TAG_COMMENTS_VIDEO_FRAGMENT = "TAG_COMMENTS_VIDEO_FRAGMENT";
    public static final String KEY_COMMENTS_LAYOUT_TYPE = "KEY_COMMENTS_LAYOUT_TYPE";
    public static final int REQUEST_CODE =  101;

    public static final String KEY_IMAGE_URL = "KEY_IMAGE_URL";
    public static final String KEY_POST_DETAILS = "KEY_POST_DETAILS";
    public static final String KEY_POST_TITLE = "KEY_POST_TITLE";
    public static final String KEY_POST_SUBREDDIT = "KEY_POST_SUBREDDIT";
    public static final String KEY_POST_ID = "KEY_POST_ID";
    public static final String KEY_POST_ARTICLE = "KEY_POST_ARTICLE";
    public static final String KEY_POST_SUBREDDIT_NO_PREFIX ="KEY_POST_SUBREDDIT_NO_PREFIX";
    public static final String KEY_SELF_TEXT = "KEY_SELF_TEXT";
    public static final String KEY_VIDEO_URI = "KEY_VIDEO_URI";
    public static final String KEY_VOTE_TYPE = "KEY_VOTE_TYPE";
    public static final String KEY_VOTE_COUNT = "KEY_VOTE_COUNT";
    public static final String KEY_INIT_VOTE_TYPE = "KEY_INIT_VOTE_TYPE";
    public static final String KEY_ORIGINAL_POST_POSITION = "KEY_ORIGINAL_POST_POSITION";

    public static final String VALUE_IMAGE_COMMENTS_LAYOUT = "VALUE_IMAGE_COMMENTS_LAYOUT";
    public static final String VALUE_GIF_COMMENTS_LAYOUT = "VALUE_GIF_COMMENTS_LAYOUT";
    public static final String VALUE_VIDEO_COMMENTS_LAYOUT = "VALUE_VIDEO_COMMENTS_LAYOUT";
    public static final String VALUE_TEXT_COMMENTS_LAYOUT = "VALUE_TEXT_COMMENTS_LAYOUT";
    private static final String TAG = CommentsActivity.class.getSimpleName();

    private CommentsFragment mCommentsFragment;
    private Api mApi;
    private AccountManager mAccountManager;
    private String mPostLayoutType;
    private String mPostTitle;
    private String mPostDetails;
    private String mPostID;
    private String mPostSubreddit;
    private String mPostArticle;
    private String mPostSubredditNoPrefix;
    private int mInitVoteType;
    private int mOrigionalPostPosition;
    private Parser mParser;
    private SpannableConfiguration mMarkDownConfig;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);
        mAccountManager = AccountManager.get(this);
        unPackIntent();
        initApi();
        launchFragment();

    }

    private void launchFragment() {
        if (mPostLayoutType.equals(VALUE_VIDEO_COMMENTS_LAYOUT)) {
            CommentsFragmentVideo savedFragment = (CommentsFragmentVideo) getSupportFragmentManager().findFragmentByTag(TAG_COMMENTS_VIDEO_FRAGMENT);
            if (savedFragment == null) {
                mCommentsFragment = new CommentsFragmentVideo();
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.add(R.id.commentsPlaceHolder, mCommentsFragment, TAG_COMMENTS_VIDEO_FRAGMENT);
                fragmentTransaction.commit();
            }
        } else {
            CommentsFragment savedFragment = (CommentsFragment) getSupportFragmentManager().findFragmentByTag(TAG_COMMENTS_NORMAL_FRAGMENT);
            if (savedFragment == null) {
                mCommentsFragment = new CommentsFragment();
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.add(R.id.commentsPlaceHolder, mCommentsFragment, TAG_COMMENTS_NORMAL_FRAGMENT);
                fragmentTransaction.commit();
            }
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
        mPostLayoutType = intent.getStringExtra(KEY_COMMENTS_LAYOUT_TYPE);
        mInitVoteType = intent.getIntExtra(KEY_INIT_VOTE_TYPE, MainActivity.NULL_RESULT);
        mOrigionalPostPosition = intent.getIntExtra(KEY_ORIGINAL_POST_POSITION, MainActivity.NULL_RESULT);
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

    @Override
    public Api getApi() {
        return mApi;
    }


    public List<Comment> parseComments(JsonObject json) {
        Gson gson = new Gson();
        List<Comment> comments = new ArrayList<>();
        Deque<JsonObject> commentStack = new ArrayDeque<>();
        commentStack.add(json.getAsJsonObject("data"));

        do {
            JsonObject currentComment = commentStack.pop();
            JsonElement currentReplies = currentComment.get("replies");

            //check if comment object
            if (currentReplies != null) {
                if (currentReplies.isJsonPrimitive()) {
                    //remove deleted comments with no replies
                    if (!currentComment.getAsJsonPrimitive("author")
                            .getAsString().equals("u/[deleted]")) {
                        comments.add(gson.fromJson(currentComment, Comment.class));
                    }
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

    @Override
    public void setMarkDownText(TextView textView, String text) {
         String parsedPext = text.replace("&gt;", ">");
       if (mParser == null) mParser = Markwon.createParser();
       if (mMarkDownConfig == null) {
           int blockQuoteColor = ContextCompat.getColor(this, R.color.colorAccent);
           SpannableTheme spannableTheme = SpannableTheme
                   .builderWithDefaults(this)
                   .blockQuoteColor(blockQuoteColor)
                   .build();
           mMarkDownConfig = SpannableConfiguration.create(this).builder(this).theme(spannableTheme).build();
       }
        SpannableRenderer spannableRenderer = new SpannableRenderer();
        Node node = mParser.parse(parsedPext);
        CharSequence markDownText = spannableRenderer.render(mMarkDownConfig, node);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        Markwon.unscheduleDrawables(textView);
        Markwon.unscheduleTableRows(textView);
        textView.setText(markDownText);
    }

    @Override
    public void save(String id) {
        mApi.save(id).enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(CommentsActivity.this, "Saved", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                Toast.makeText(CommentsActivity.this, "Saved", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void unsave(String id) {
        mApi.unsave(id).enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(CommentsActivity.this, "Saved", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                Toast.makeText(CommentsActivity.this, "Saved", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra(CommentsActivity.KEY_VOTE_TYPE, mCommentsFragment.getRedditVoteHelper().getVoteStatus());
        intent.putExtra(KEY_INIT_VOTE_TYPE, mInitVoteType);
        intent.putExtra(KEY_ORIGINAL_POST_POSITION, mOrigionalPostPosition);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

}
