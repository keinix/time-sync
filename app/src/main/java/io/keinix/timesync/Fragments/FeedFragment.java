package io.keinix.timesync.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import butterknife.BindView;
import butterknife.ButterKnife;
import im.ene.toro.widget.Container;
import io.keinix.timesync.R;
import io.keinix.timesync.adapters.FeedAdapter;
import io.keinix.timesync.reddit.Api;
import io.keinix.timesync.reddit.model.RedditFeed;
import io.keinix.timesync.reddit.model.VoteResult;
import okhttp3.Callback;
import retrofit2.Call;


public class FeedFragment extends Fragment {

    @BindView(R.id.feedRecyclerView) Container feedRecyclerView;

    FeedItemInterface mFeedItemInterface;
    private FeedAdapter mFeedAdapter;
    private boolean mLoading;
    public static final String TAG = FeedFragment.class.getSimpleName();

    public interface FeedItemInterface {
        //TODO: implement this in MainActivity then get a reference using getActivity()
        //TODO: put the methods in the onclickListeners
        Call<VoteResult> vote(String id, String voteType);
        void share(int index);
        void launchCommentFragment(int index);
        void populateRedditFeed(FeedAdapter adapter);
        Call<RedditFeed> appendFeed(String after);
        Context getContext();
        Api getApi();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_feed, container, false);
        ButterKnife.bind(this, view);
        mFeedItemInterface = (FeedItemInterface) getActivity();
        getActivity().setTitle("RedditFeed");
        setHasOptionsMenu(true);


        mFeedAdapter = new FeedAdapter(mFeedItemInterface, this);
        feedRecyclerView.setAdapter(mFeedAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        feedRecyclerView.setLayoutManager(linearLayoutManager);
        feedRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();

                if (!mLoading && linearLayoutManager.getItemCount() <= (lastVisibleItem + 5) && mFeedAdapter.initLoadComplete) {
                    Log.d(TAG, "onScrolled Activated.");
                    mLoading = true;
                    mFeedAdapter.appendRedditFeed();
                }
            }
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
                mFeedItemInterface.populateRedditFeed(mFeedAdapter);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
