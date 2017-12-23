package io.keinix.timesync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import io.keinix.timesync.Activities.AddAccountActivity;
import io.keinix.timesync.Fragments.CommentsFragment;
import io.keinix.timesync.Fragments.FeedFragment;
import io.keinix.timesync.Fragments.MessagesFragment;
import io.keinix.timesync.Fragments.ViewPagerFragment;
import io.keinix.timesync.adapters.FeedAdapter;
import io.keinix.timesync.reddit.Api;
import io.keinix.timesync.reddit.RedditConstants;
import io.keinix.timesync.reddit.model.RedditAccessToken;
import io.keinix.timesync.reddit.model.RedditFeed;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements FeedFragment.FeedItemInterface,
        MessagesFragment.MessagesInterface {

    public static final String TAG_FEED_FRAGMENT = "TAG_FEED_FRAGMENT";
    public static final String TAG_MESSAGES_FRAGMENT = "TAG_MESSAGES_FRAGMENT";
    public static final String TAB_ACCOUNT_FRAGMENT = "TAB_ACCOUNT_FRAGMENT";
    public static final String TAG_VIEW_PAGER_FRAGMENT = "TAG_VIEW_PAGER_FRAGMENT";
    public static final String TAG_COMMENTS_FRAGMENT = "TAG_COMMENTS_FRAGMENT";
    public static final String EXTRA_REDDIT_TOKEN = "EXTRA_REDDIT_TOKEN";
    public static final int REQUEST_CODE_ACCOUNT_LOGIN = 1000;

    public AccountManager mAccountManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mAccountManager = AccountManager.get(this);

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

    // -----------Feed Fragment Interface Methods-----------------
    @Override
    public void voteUp(int index) {

    }

    @Override
    public void voteDown(int index) {

    }

    @Override
    public void share(int index) {

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
        Log.d("FINDME", "populateRedditFeed called");
        Account accounts[] = mAccountManager.getAccountsByType(RedditConstants.ACCOUNT_TYPE);
        String redditToken = mAccountManager.peekAuthToken(accounts[0], RedditConstants.KEY_ACCESS_TOKEN);

        if (redditToken != null) {
            adapter.getSwipeRefreshLayout().setRefreshing(true);
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(RedditConstants.REDDIT_BASE_URL_OAUTH2)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            Api api = retrofit.create(Api.class);

            Map<String, String> headers = new HashMap<>();
            headers.put("Authorization", redditToken);
            headers.put("User-Agent", RedditConstants.REDDIT_USER_AGENT);

            Call<RedditFeed> call = api.getFeed(headers);
            call.enqueue(new Callback<RedditFeed>() {
                @Override
                public void onResponse(Call<RedditFeed> call, Response<RedditFeed> response) {
                    adapter.setRedditFeed(response.body());
                    adapter.notifyDataSetChanged();
                    adapter.getSwipeRefreshLayout().setRefreshing(false);
                }
                @Override
                public void onFailure(Call<RedditFeed> call, Throwable t) {
                    Log.d("FINDME", "onFailure called from populateRedditFeed");
                }
            });
        } else {
            adapter.getSwipeRefreshLayout().setRefreshing(false);
            Toast.makeText(this, "Please Login with Reddit", Toast.LENGTH_SHORT).show();
        }
    }

    // -----------Message Fragment Interface Methods-----------------

    @Override
    public void tempLogin() {
        Intent intent = new Intent(this, AddAccountActivity.class);
        startActivityForResult(intent, REQUEST_CODE_ACCOUNT_LOGIN);
    }
}
