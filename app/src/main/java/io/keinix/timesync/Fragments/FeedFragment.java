package io.keinix.timesync.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
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

    FeedItemInterface mFeedItemInterface;
    private FeedAdapter mFeedAdapter;
    private boolean mLoading;
    private LinearLayoutManager mLinearLayoutManager;
    public static final String TAG = FeedFragment.class.getSimpleName();

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
        mFeedAdapter = new FeedAdapter(mFeedItemInterface, mLinearLayoutManager, feedProgressBar);
        feedRecyclerView.setAdapter(mFeedAdapter);
        feedRecyclerView.setLayoutManager(mLinearLayoutManager);
//        feedRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//                int lastVisibleItem = mLinearLayoutManager.findLastVisibleItemPosition();
//
//                if (!mLoading && mLinearLayoutManager.getItemCount() <= (lastVisibleItem + 5) && mFeedAdapter.initLoadComplete) {
//                    Log.d(TAG, "onScrolled Activated.");
//                    mLoading = true;
//                    mFeedAdapter.appendRedditFeed();
//                }
//            }
//        });
        feedProgressBar.setVisibility(View.VISIBLE);
        mFeedItemInterface.populateRedditFeed(mFeedAdapter);
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
