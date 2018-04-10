package io.keinix.timesync.adapters;

import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.keinix.timesync.Fragments.FeedFragment.FeedItemInterface;
import io.keinix.timesync.R;
import io.keinix.timesync.reddit.model.Child;
import io.keinix.timesync.reddit.model.Data_;
import io.keinix.timesync.reddit.model.RedditFeed;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FeedAdapter extends RecyclerView.Adapter  implements Callback<RedditFeed>  {

    private static final String TAG = FeedAdapter.class.getSimpleName();
    private static final int VALUE_UPVOTED = 1;
    private static final int VALUE_DOWNVOTED = -1;
    private static final int VIEW_ITEM_TYPE_IMAGE = 100;
    private static final int VIEW_ITEM_TYPE_TEXT = 200;
    private static final int  VIEW_ITEM_TYPE_VIDEO =  300;

    public FeedItemInterface mFeedItemInterface;
    private RedditFeed mRedditFeed;
    private String mAfter;
    public Map<String, Integer> mLocalVoteTracker;
    public boolean initLoadComplete;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private ProgressBar mProgressBar;
    private boolean mLoading = false;
    private boolean isNestedScroll;
    private NestedScrollView mNestedScrollView;
    private boolean isFromSubReddit;
    private FloatingActionButton mFab;



    public FeedAdapter(FeedItemInterface feedItemInterface, LinearLayoutManager linearLayoutManager, ProgressBar progressBar) {
        isNestedScroll = false;
        mFeedItemInterface = feedItemInterface;
        mLinearLayoutManager = linearLayoutManager;
        mProgressBar = progressBar;
        mLocalVoteTracker = Collections.synchronizedMap(new HashMap<>());
        mAfter = "";
    }

    public FeedAdapter(FeedItemInterface feedItemInterface, LinearLayoutManager linearLayoutManager,
                       ProgressBar progressBar, FloatingActionButton fab) {
        isNestedScroll = false;
        mFeedItemInterface = feedItemInterface;
        mLinearLayoutManager = linearLayoutManager;
        mProgressBar = progressBar;
        mLocalVoteTracker = Collections.synchronizedMap(new HashMap<>());
        mAfter = "";
        mFab = fab;
    }

    public FeedAdapter(FeedItemInterface feedItemInterface, LinearLayoutManager linearLayoutManager,
                       ProgressBar progressBar, NestedScrollView nestedScrollView) {
        isNestedScroll = true;
        mFeedItemInterface = feedItemInterface;
        mLinearLayoutManager = linearLayoutManager;
        mProgressBar = progressBar;
        mNestedScrollView = nestedScrollView;
        mLocalVoteTracker = Collections.synchronizedMap(new HashMap<>());
        mAfter = "";
    }


    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mRecyclerView = recyclerView;
        if (isNestedScroll) {
            setOnScroll(mNestedScrollView);
        } else {
            setOnScroll();
        }
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View imageItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.feed_item, parent, false);
        View textItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.text_feed_item, parent, false);
        View videoItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.video_feed_item, parent, false);
        switch (viewType) {
            case VIEW_ITEM_TYPE_TEXT:
                return new TextFeedViewHolder(textItem, this, mFeedItemInterface);
            case VIEW_ITEM_TYPE_VIDEO:
                return new VideoFeedViewHolder(videoItem, this, mFeedItemInterface);
            default:
                return new ImageFeedViewHolder(imageItem, this, mFeedItemInterface);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case VIEW_ITEM_TYPE_TEXT:
                ((TextFeedViewHolder) holder).bindView(position);
                break;
            case VIEW_ITEM_TYPE_VIDEO:
                ((VideoFeedViewHolder) holder).bindView(position);
                break;
            case VIEW_ITEM_TYPE_IMAGE:
                ((ImageFeedViewHolder) holder).bindView(position);
        }
    }

    @Override
    public int getItemCount() {
        if (mRedditFeed != null) {
            return mRedditFeed.getData().getChildren().size();
        } else {
            return 0;
        }
    }

    //TODO:bug where a link has no picture (post hint is normally null in this case)
    @Override
    public int getItemViewType(int position) {
        Data_ post = mRedditFeed.getData().getChildren().get(position).getData();
        if ((post.isSelf()) && post.getPreview() == null) {
            return VIEW_ITEM_TYPE_TEXT;
        } else if (post.getDomain().equals("v.redd.it")) {
            return VIEW_ITEM_TYPE_VIDEO;
        }
        return VIEW_ITEM_TYPE_IMAGE;
    }

    @Override
    public void onResponse(Call<RedditFeed> call, Response<RedditFeed> response) {
        Log.d(TAG, "response "+ response.toString());

        if (response.isSuccessful()) {
            mProgressBar.setVisibility(View.INVISIBLE);
            mRedditFeed = response.body();
            mAfter = response.body().getData().getAfter();
            populateLocalVoteTracker(mRedditFeed.getData().getChildren());
            notifyDataSetChanged();
            initLoadComplete = true;

            Log.d(TAG, "response was a success! we got the feed!");
            Toast.makeText(mFeedItemInterface.getContext(), "Refresh activated", Toast.LENGTH_SHORT).show();
        } else {
            Log.d(TAG, "responce was not successfull triggered");
        }
    }

    private void populateLocalVoteTracker(List<Child> children) {
        for (Child child : children) {
            if (child.getData().isLiked() != null) {
                if (child.getData().isLiked()) {
                    mLocalVoteTracker.put(child.getData().getName(), VALUE_UPVOTED);
                } else {
                    mLocalVoteTracker.put(child.getData().getName(), VALUE_DOWNVOTED);
                }
            }
        }
    }

    public void setOnScroll() {
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (mFab != null) {
                    if (dy > 0) {
                        mFab.hide();
                    } else if (dy < 0 ) {
                        mFab.show();
                    }
                }
                int lastVisibleItem = mLinearLayoutManager.findLastVisibleItemPosition();

                if (!mLoading && mLinearLayoutManager.getItemCount() <= (lastVisibleItem + 5) && initLoadComplete) {
                    Log.d(TAG, "onScrolled Activated.");
                    mLoading = true;
                    appendRedditFeed();
                }
            }
        });
    }
    public void setOnScroll(NestedScrollView nestedScrollView) {
        nestedScrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener)
                (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if(v.getChildAt(v.getChildCount() - 1) != null) {
                if ((scrollY >= (v.getChildAt(v.getChildCount() - 1).getMeasuredHeight() - v.getMeasuredHeight())) &&
                        scrollY > oldScrollY) {
                    appendRedditFeed();
                }
            }
        });
    }

    public void appendRedditFeed() {
        mFeedItemInterface.appendFeed(mAfter).enqueue(new Callback<RedditFeed>() {
            @Override
            public void onResponse(Call<RedditFeed> call, Response<RedditFeed> response) {

                if (response.isSuccessful()) {
                    int previousFeedLength = getItemCount();
                    mAfter = response.body().getData().getAfter();

                    mRedditFeed.getData()
                            .getChildren()
                            .addAll(response.body().getData().getChildren());

                    populateLocalVoteTracker(response.body().getData().getChildren());
                    notifyItemRangeInserted(previousFeedLength, response.body().getData().getChildren().size());
                    mLoading = false;
                }
            }

            @Override
            public void onFailure(Call<RedditFeed> call, Throwable t) {
                mLoading = false;
            }
        });
    }

    @Override
    public void onFailure(Call<RedditFeed> call, Throwable t) {
        Log.d(TAG, "onFailure called from populateRedditFeed");
        Log.d(TAG, "Call " + call.toString());
        Log.d(TAG, "Call request header: " + call.request().headers());
        Log.d(TAG, "Call request toString: " + call.request().toString());
        Log.d(TAG, t.toString());
    }

    public boolean isFromSubReddit() {
        return isFromSubReddit;
    }

    public void setFromSubReddit(boolean fromSubReddit) {
        isFromSubReddit = fromSubReddit;
    }

    public RedditFeed getRedditFeed() {
        return mRedditFeed;
    }

    public boolean isNestedScroll() {
        return isNestedScroll;
    }

    public void setNestedScroll(boolean nestedScroll) {
        isNestedScroll = nestedScroll;
    }
}
