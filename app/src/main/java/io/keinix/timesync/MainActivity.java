package io.keinix.timesync;

import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import io.keinix.timesync.Activities.AddAccountActivity;
import io.keinix.timesync.Activities.CommentsActivity;
import io.keinix.timesync.Activities.PostActivity;
import io.keinix.timesync.Fragments.FeedFragment;
import io.keinix.timesync.Fragments.MessagesFragment;
import io.keinix.timesync.Fragments.SubredditNavigationFragment;
import io.keinix.timesync.Fragments.ViewPagerFragment;
import io.keinix.timesync.adapters.FeedAdapter;
import io.keinix.timesync.adapters.MessagesAdapter;
import io.keinix.timesync.reddit.Api;
import io.keinix.timesync.reddit.RedditAuthInterceptor;
import io.keinix.timesync.reddit.RedditConstants;
import io.keinix.timesync.reddit.TokenAuthenticator;
import io.keinix.timesync.reddit.model.Message;
import io.keinix.timesync.reddit.model.RedditFeed;
import io.keinix.timesync.reddit.model.SubReddit;
import io.keinix.timesync.reddit.model.VoteResult;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements FeedFragment.FeedItemInterface,
        MessagesFragment.MessagesInterface {

    public static final String TAG = MainActivity.class.getSimpleName();
    public static final String TAG_VIEW_PAGER_FRAGMENT = "TAG_VIEW_PAGER_FRAGMENT";
    public static final String TAG_NAVIGATION_FRAGMENT = "TAG_NAVIGATION_FRAGMENT";

    // used to call Api.getPersonal()
    public static final String REDDIT_FEED_TYPE_UPVATED = "upvoted";
    public static final String REDDIT_FEED_TYPE_SAVED = "saved";
    public static final String REDDIT_FEED_TYPE_SUBMITTED = "submitted";

    public static final int NULL_RESULT = 4;

    public AccountManager mAccountManager;
    public Api mApi;
    private boolean backPressedOnce;
    private int mCommentsResultVoteValue = NULL_RESULT;
    private int mInitVoteType;
    private int mOriginalPostPosition;
    private SubredditNavigationFragment mNavFragment;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mAccountManager = AccountManager.get(this);
        if (!Fresco.hasBeenInitialized()) Fresco.initialize(this);
        initApi();
        replaceFragmentPlaceHolders();
        setUpNavigationDrawer();
    }


    private void replaceFragmentPlaceHolders() {
        ViewPagerFragment savedFragment = (ViewPagerFragment) getSupportFragmentManager()
                .findFragmentByTag(TAG_VIEW_PAGER_FRAGMENT);
        if (savedFragment == null) {
            ViewPagerFragment viewPagerFragment = new ViewPagerFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(R.id.placeHolder, viewPagerFragment, TAG_VIEW_PAGER_FRAGMENT);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();

        }
    }

    public void setUpNavigationDrawer() {
        SubredditNavigationFragment savedFragment = (SubredditNavigationFragment)
                getSupportFragmentManager().findFragmentByTag(TAG_NAVIGATION_FRAGMENT);

        if (savedFragment == null) {
            mNavFragment = new SubredditNavigationFragment();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.add(R.id.navigationPlaceHolder, mNavFragment, TAG_NAVIGATION_FRAGMENT)
                    .addToBackStack(null)
                    .commit();
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

    @Override
    public void onBackPressed() {
        if (backPressedOnce) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Press back again to exit.", Toast.LENGTH_SHORT).show();
            backPressedOnce = true;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "OnResult called in ACTIVITY");
        Log.d(TAG, "Request Code: " + requestCode + "Result Code: " + resultCode);
        super.onActivityResult(requestCode, resultCode, data);
        if ( resultCode == Activity.RESULT_OK && requestCode == CommentsActivity.REQUEST_CODE) {
            mCommentsResultVoteValue = data.getIntExtra(CommentsActivity.KEY_VOTE_TYPE, NULL_RESULT);
            mInitVoteType = data.getIntExtra(CommentsActivity.KEY_INIT_VOTE_TYPE, NULL_RESULT);
            mOriginalPostPosition = data.getIntExtra(CommentsActivity.KEY_ORIGINAL_POST_POSITION, NULL_RESULT);
        }

    }




    // -----------Feed Fragment Interface Methods-----------------
    @Override
    public Call<VoteResult> vote(String id, String voteType) {
        return mApi.vote(voteType, id);
    }

    @Override
    public void populateRedditFeed(FeedAdapter adapter, String feedType) {
        String username = "";
        if (!feedType.equals(FeedFragment.VALUE_FEED_TYPE_MAIN)) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            username = prefs.getString(FeedFragment.KEY_USER_NAME, FeedFragment.KEY_NO_USER_NAME);
        }

        switch (feedType) {
            case FeedFragment.VALUE_FEED_TYPE_MAIN:
                mApi.getFeed().enqueue(adapter);
                break;
            case FeedFragment.VALUE_FEED_TYPE_POSTS:
                mApi.getPersonal(username, REDDIT_FEED_TYPE_SUBMITTED).enqueue(adapter);
                break;
            case FeedFragment.VALUE_FEED_TYPE_SAVED:
                mApi.getPersonal(username, REDDIT_FEED_TYPE_SAVED).enqueue(adapter);
                break;
            case FeedFragment.VALUE_FEED_TYPE_UPVOTED:
                mApi.getPersonal(username, REDDIT_FEED_TYPE_UPVATED).enqueue(adapter);
                break;
        }
    }


    @Override
    public Call<RedditFeed> appendFeed(String after) {
         return mApi.appendFeed(after);
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public Api getApi() {
        return mApi;
    }

    @Override
    public int getCommentsResult() {
        return mCommentsResultVoteValue;
    }

    @Override
    public int getPostInitVoteType() {
        return mInitVoteType;
    }

    @Override
    public int getOriginalPostPosition() {
        return mOriginalPostPosition;
    }

    @Override
    public void launchPostActivity() {
        ArrayList<String> subNames = new ArrayList<>();
        for (SubReddit subReddit : mNavFragment.getSubreddits()) {
            subNames.add(subReddit.getDisplayNamePrefixed());
        }
        Intent intent = new Intent(this, PostActivity.class);
        intent.putStringArrayListExtra(PostActivity.KEY_SUB_LIST, subNames);
        startActivity(intent);
    }

    // -----------Message Fragment Interface Methods-----------------

    @Override
    public void tempLogin() {
        Intent intent = new Intent(this, AddAccountActivity.class);
        startActivity(intent);
    }

    @Override
    public void getMessages(MessagesAdapter adapter, boolean isNotification) {

        mApi.getMessages("100").enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if (response.isSuccessful()) {
                    adapter.setMessages(populateMessageList(response.body(), isNotification));
                    adapter.notifyDataSetChanged();
                } else {
                    Log.d(TAG, "Response was not successful: " + response);
                }
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {

            }
        });
    }

    public List<Message> populateMessageList(JsonElement jsonElement, boolean isNotification) {
        List<Message> tempMessages = new ArrayList<>();
        Gson gson = new Gson();
        JsonArray baseMessageArray = jsonElement.getAsJsonObject()
                .getAsJsonObject("data")
                .getAsJsonArray("children");

        for (JsonElement messageElement : baseMessageArray) {
            JsonObject message = messageElement.getAsJsonObject().getAsJsonObject("data");
            boolean wasComment = message.getAsJsonPrimitive("was_comment").getAsBoolean();
            if (isNotification) {
                if (wasComment) {
                    tempMessages.add(gson.fromJson(message, Message.class));
                }
            } else {
                if (!wasComment) {
                    tempMessages.add(gson.fromJson(message, Message.class));
                }
            }
        }
        return tempMessages;
    }
}
