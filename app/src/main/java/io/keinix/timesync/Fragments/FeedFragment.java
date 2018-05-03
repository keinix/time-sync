package io.keinix.timesync.Fragments;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.google.gson.JsonObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import im.ene.toro.widget.Container;
import io.keinix.timesync.Activities.PostActivity;
import io.keinix.timesync.MainActivity;
import io.keinix.timesync.R;
import io.keinix.timesync.adapters.FeedAdapter;
import io.keinix.timesync.reddit.Api;
import io.keinix.timesync.reddit.RedditConstants;
import io.keinix.timesync.reddit.RedditVoteHelper;
import io.keinix.timesync.reddit.model.Data_;
import io.keinix.timesync.reddit.model.RedditFeed;
import io.keinix.timesync.reddit.model.SubReddit;
import io.keinix.timesync.reddit.model.VoteResult;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class FeedFragment extends Fragment {

    @BindView(R.id.feedRecyclerView) Container feedRecyclerView;
     public @BindView(R.id.feedProgressBar) ProgressBar feedProgressBar;
     @BindView(R.id.swipeRefresh) SwipeRefreshLayout swipeRefreshLayout;
     @BindView(R.id.postFab) FloatingActionButton fab;

     public static final String KEY_FEED_TYPE = "KEY_FEED_TYPE";
     public static final String VALUE_FEED_TYPE_MAIN = "VALUE_FEED_TYPE_MAIN";
     public static final String VALUE_FEED_TYPE_UPVOTED = "VALUE_FEED_TYPE_UPVOTED";
     public static final String VALUE_FEED_TYPE_SAVED = "VALUE_FEED_TYPE_SAVED";
     public static final String VALUE_FEED_TYPE_POSTS = "VALUE_FEED_TYPE_POSTS";
     public static final String KEY_USER_NAME = "KEY_USER_NAME";
     public static final String KEY_NO_USER_NAME = "KEY_NO_USER_NAME";

    FeedItemInterface mFeedItemInterface;
    private FeedAdapter mFeedAdapter;
    private LinearLayoutManager mLinearLayoutManager;
    public static final String TAG = FeedFragment.class.getSimpleName();
    private String mFeedType;

    // implemented in MainActivity and SubredditActivity
    public interface FeedItemInterface {
        //TODO: implement this in MainActivity then get a reference using getActivity()
        //TODO: put the methods in the onclickListeners
        Call<VoteResult> vote(String id, String voteType);

        void populateRedditFeed(FeedAdapter adapter, String feedType);

        Call<RedditFeed> appendFeed(String after, String feedType);

        Context getContext();

        Api getApi();

        int getCommentsResult();

        int getPostInitVoteType();

        int getOriginalPostPosition();

        void launchPostActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_feed, container, false);
        ButterKnife.bind(this, view);
        mFeedItemInterface = (FeedItemInterface) getActivity();
        if (getArguments() != null) {
            mFeedType = getArguments().getString(KEY_FEED_TYPE);
        } else {
            mFeedType = VALUE_FEED_TYPE_MAIN;
        }
        checkUserName();
        getActivity().setTitle("RedditFeed");
        setHasOptionsMenu(true);


        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mFeedAdapter = new FeedAdapter(mFeedItemInterface, mLinearLayoutManager, feedProgressBar, fab);
        mFeedAdapter.setFeedType(mFeedType);
        feedRecyclerView.setAdapter(mFeedAdapter);
        feedRecyclerView.setLayoutManager(mLinearLayoutManager);
        feedProgressBar.setVisibility(View.VISIBLE);
        mFeedItemInterface.populateRedditFeed(mFeedAdapter, mFeedType);
        setUpFab();
        swipeRefreshLayout.setOnRefreshListener(() -> {
            swipeRefreshLayout.setRefreshing(false);
            feedProgressBar.setVisibility(View.VISIBLE);
            mFeedItemInterface.populateRedditFeed(mFeedAdapter, mFeedType);
        });
        return view;
    }

    public void checkUserName() {
        AccountManager am = AccountManager.get(mFeedItemInterface.getContext());
        Account[] accounts = am.getAccountsByType(RedditConstants.ACCOUNT_TYPE);
        if (accounts.length > 0) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mFeedItemInterface.getContext());
            String username = prefs.getString(KEY_USER_NAME, KEY_NO_USER_NAME);
            Log.d(TAG, "username from Prefs: " + username);
            if (username.equals(KEY_NO_USER_NAME)) {
                getUserName(prefs.edit());
            }
        }
    }

    public void getUserName(SharedPreferences.Editor editor) {
        mFeedItemInterface.getApi().getUsername().enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                String userNamePrefixed = response.body().getAsJsonObject("subreddit")
                        .getAsJsonPrimitive("display_name_prefixed").getAsString();
                editor.putString(KEY_USER_NAME, userNamePrefixed.substring(2));
                editor.apply();
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.feed_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.refreshFeedMenu:
                feedProgressBar.setVisibility(View.VISIBLE);
                mFeedItemInterface.populateRedditFeed(mFeedAdapter, mFeedType);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        Log.d(TAG, "FEED FRAGMENT ON RESUME");
        processVoteFromCommentSection();
        super.onResume();
    }

    public void setUpFab() {
        fab.setOnClickListener(v -> {
            mFeedItemInterface.launchPostActivity();
        });
    }

    private void processVoteFromCommentSection() {
        if (mFeedItemInterface.getCommentsResult() != MainActivity.NULL_RESULT) {
            int voteTypeFromComments = mFeedItemInterface.getCommentsResult();
            int initPostVoteType = mFeedItemInterface.getPostInitVoteType();

            if (voteTypeFromComments != initPostVoteType) {
                int postPosition = mFeedItemInterface.getOriginalPostPosition();
                Data_ post = mFeedAdapter.getRedditFeed().getData().getChildren().get(postPosition).getData();
                Boolean isLiked;

                switch (voteTypeFromComments){
                    case RedditVoteHelper.VALUE_UPVOTED:
                        isLiked = true;
                        break;
                    case RedditVoteHelper.VALUE_DOWNVOTED:
                        isLiked = false;
                        break;
                    default:
                        isLiked = null;
                        break;
                }
                post.setLiked(isLiked);
                mFeedAdapter.notifyItemChanged(postPosition);
            }
        }
    }
}
