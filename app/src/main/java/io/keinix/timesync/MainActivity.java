package io.keinix.timesync;

import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;

import butterknife.ButterKnife;
import io.keinix.timesync.Activities.AddAccountActivity;
import io.keinix.timesync.Activities.CommentsActivity;
import io.keinix.timesync.Fragments.CommentsFragment;
import io.keinix.timesync.Fragments.FeedFragment;
import io.keinix.timesync.Fragments.MessagesFragment;
import io.keinix.timesync.Fragments.SubredditNavigationFragment;
import io.keinix.timesync.Fragments.ViewPagerFragment;
import io.keinix.timesync.adapters.FeedAdapter;
import io.keinix.timesync.reddit.Api;
import io.keinix.timesync.reddit.RedditAuthInterceptor;
import io.keinix.timesync.reddit.RedditConstants;
import io.keinix.timesync.reddit.TokenAuthenticator;
import io.keinix.timesync.reddit.model.RedditFeed;
import io.keinix.timesync.reddit.model.VoteResult;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements FeedFragment.FeedItemInterface,
        MessagesFragment.MessagesInterface {

    public static final String TAG = MainActivity.class.getSimpleName();
    public static final String TAG_FEED_FRAGMENT = "TAG_FEED_FRAGMENT";
    public static final String TAG_MESSAGES_FRAGMENT = "TAG_MESSAGES_FRAGMENT";
    public static final String TAB_ACCOUNT_FRAGMENT = "TAB_ACCOUNT_FRAGMENT";
    public static final String TAG_VIEW_PAGER_FRAGMENT = "TAG_VIEW_PAGER_FRAGMENT";
    public static final String TAG_SUB_REDDIT_FRAGMENT = "TAG_SUB_REDDIT_FRAGMENT";
    public static final String TAG_COMMENTS_FRAGMENT = "TAG_COMMENTS_NORMAL_FRAGMENT";
    public static final String EXTRA_REDDIT_TOKEN = "EXTRA_REDDIT_TOKEN";
    public static final int NULL_RESULT = 4;

    public AccountManager mAccountManager;
    public Api mApi;
    private boolean backPressedOnce;
    private int mCommentsResultVoteValue = NULL_RESULT;
    private int mInitVoteType;
    private int mOriginalPostPosition;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mAccountManager = AccountManager.get(this);
        if (!Fresco.hasBeenInitialized()) Fresco.initialize(this);
        initApi();
        replaceFragmentPlaceHolders();
//        initNavigationDrawer();
    }

//    private void initNavigationDrawer() {
//        DrawerLayout drawerLayout = findViewById(R.id.subredditDrawerLayout);
//        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, null, "hello", "goodbye");
//    }

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

        SubredditNavigationFragment saveNavFragment = (SubredditNavigationFragment) getSupportFragmentManager()
                .findFragmentByTag(TAG_SUB_REDDIT_FRAGMENT);

        if (saveNavFragment == null) {
            SubredditNavigationFragment subredditNavigationFragment = new SubredditNavigationFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(R.id.drawerFragmentPlaceHolder, subredditNavigationFragment, TAG_SUB_REDDIT_FRAGMENT);
            fragmentTransaction.addToBackStack(null);
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

    // -----------Feed Fragment Interface Methods-----------------
    @Override
    public Call<VoteResult> vote(String id, String voteType) {
        return mApi.vote(voteType, id);
    }



    @Override
    public void share(int index) {

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

    @Override
    public void launchCommentFragment(int index) {
        CommentsFragment commentsFragment = new CommentsFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(CommentsFragment.KEY_INDEX, index);
        commentsFragment.setArguments(bundle);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.placeHolder, commentsFragment, TAG_COMMENTS_FRAGMENT);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void populateRedditFeed(FeedAdapter adapter) {
        Log.d(TAG, "REFRESH TRIGGERED");
            mApi.getFeed().enqueue(adapter);
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

    // -----------Message Fragment Interface Methods-----------------

    @Override
    public void tempLogin() {
        Intent intent = new Intent(this, AddAccountActivity.class);
        startActivity(intent);
    }
}
