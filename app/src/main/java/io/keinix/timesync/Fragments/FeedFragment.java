package io.keinix.timesync.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.keinix.timesync.R;
import io.keinix.timesync.adapters.FeedAdapter;
import io.keinix.timesync.reddit.model.RedditFeed;
import okhttp3.Callback;
import retrofit2.Call;


public class FeedFragment extends Fragment {

    @BindView(R.id.feedRecyclerView) RecyclerView feedRecyclerView;
    @BindView(R.id.feedFragment) SwipeRefreshLayout mSwipeRefreshLayout;

    FeedItemInterface mFeedItemInterface;
    private FeedAdapter mFeedAdapter;

    public interface FeedItemInterface {
        //TODO: implement this in MainActivity then get a reference using getActivity()
        //TODO: put the methods in the onclickListeners
        void voteUp(int index);
        void voteDown(int index);
        void share(int index);
        void launchCommentFragment(int index);
        void populateRedditFeed(FeedAdapter adapter);
        Context getContext();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_feed, container, false);
        ButterKnife.bind(this, view);
        mFeedItemInterface = (FeedItemInterface) getActivity();
        getActivity().setTitle("RedditFeed");

        mFeedAdapter = new FeedAdapter(mFeedItemInterface, mSwipeRefreshLayout);
        feedRecyclerView.setAdapter(mFeedAdapter);
        feedRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mSwipeRefreshLayout.setOnRefreshListener(() -> mFeedItemInterface.populateRedditFeed(mFeedAdapter));
        return view;
    }






}
