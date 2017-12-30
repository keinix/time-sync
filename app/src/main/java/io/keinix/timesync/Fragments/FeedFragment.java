package io.keinix.timesync.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.keinix.timesync.R;
import io.keinix.timesync.adapters.FeedAdapter;
import io.keinix.timesync.reddit.model.RedditFeed;
import io.keinix.timesync.reddit.model.VoteResult;
import okhttp3.Callback;
import retrofit2.Call;


public class FeedFragment extends Fragment {

    @BindView(R.id.feedRecyclerView) RecyclerView feedRecyclerView;

    FeedItemInterface mFeedItemInterface;
    private FeedAdapter mFeedAdapter;

    public interface FeedItemInterface {
        //TODO: implement this in MainActivity then get a reference using getActivity()
        //TODO: put the methods in the onclickListeners
        Call<VoteResult> voteUp(String id);
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
        setHasOptionsMenu(true);


        mFeedAdapter = new FeedAdapter(mFeedItemInterface);
        feedRecyclerView.setAdapter(mFeedAdapter);
        feedRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        return view;
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
                mFeedItemInterface.populateRedditFeed(mFeedAdapter);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
