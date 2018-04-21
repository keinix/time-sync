package io.keinix.timesync.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import im.ene.toro.widget.Container;
import io.keinix.timesync.Activities.PostActivity;
import io.keinix.timesync.MainActivity;
import io.keinix.timesync.R;
import io.keinix.timesync.adapters.FeedAdapter;
import io.keinix.timesync.reddit.Api;
import io.keinix.timesync.reddit.RedditVoteHelper;
import io.keinix.timesync.reddit.model.Data_;
import io.keinix.timesync.reddit.model.RedditFeed;
import io.keinix.timesync.reddit.model.SubReddit;
import io.keinix.timesync.reddit.model.VoteResult;
import retrofit2.Call;


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

    FeedItemInterface mFeedItemInterface;
    private FeedAdapter mFeedAdapter;
    private boolean mLoading;
    private LinearLayoutManager mLinearLayoutManager;
    public static final String TAG = FeedFragment.class.getSimpleName();

    // implemented in MainActivity and SubredditActivity
    public interface FeedItemInterface {
        //TODO: implement this in MainActivity then get a reference using getActivity()
        //TODO: put the methods in the onclickListeners
        Call<VoteResult> vote(String id, String voteType);

        void populateRedditFeed(FeedAdapter adapter);

        Call<RedditFeed> appendFeed(String after);

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
        getActivity().setTitle("RedditFeed");
        setHasOptionsMenu(true);


        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mFeedAdapter = new FeedAdapter(mFeedItemInterface, mLinearLayoutManager, feedProgressBar, fab);
        feedRecyclerView.setAdapter(mFeedAdapter);
        feedRecyclerView.setLayoutManager(mLinearLayoutManager);
        feedProgressBar.setVisibility(View.VISIBLE);
        mFeedItemInterface.populateRedditFeed(mFeedAdapter);
        setUpFab();
        swipeRefreshLayout.setOnRefreshListener(() -> {
            swipeRefreshLayout.setRefreshing(false);
            feedProgressBar.setVisibility(View.VISIBLE);
            mFeedItemInterface.populateRedditFeed(mFeedAdapter);
        });
        return view;
    }




    public void setLoaded() {
        mLoading = false;
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
                mFeedItemInterface.populateRedditFeed(mFeedAdapter);
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
