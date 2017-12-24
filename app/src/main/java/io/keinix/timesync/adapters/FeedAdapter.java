package io.keinix.timesync.adapters;

import android.net.Uri;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.keinix.timesync.Fragments.FeedFragment.FeedItemInterface;
import io.keinix.timesync.R;
import io.keinix.timesync.reddit.model.Data_;
import io.keinix.timesync.reddit.model.RedditFeed;

public class FeedAdapter extends RecyclerView.Adapter {
    private   FeedItemInterface mFeedItemInterface;
    private RedditFeed mRedditFeed;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    public FeedAdapter(FeedItemInterface feedItemInterface, SwipeRefreshLayout swipeRefreshLayout) {
        mFeedItemInterface = feedItemInterface;
        mSwipeRefreshLayout = swipeRefreshLayout;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.feed_item, parent, false);
        mFeedItemInterface.populateRedditFeed(this );
        return new FeedViewHolder(view);
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ((FeedViewHolder) holder).bindView(position);
    }

    @Override
    public int getItemCount() {
        if (mRedditFeed != null) {
            return mRedditFeed.getData().getChildren().size();
        } else {
            return 0;
        }

    }

    public RedditFeed getRedditFeed() {
        return mRedditFeed;
    }

    public void setRedditFeed(RedditFeed redditFeed) {
        mRedditFeed = redditFeed;
    }

    public SwipeRefreshLayout getSwipeRefreshLayout() {
        return mSwipeRefreshLayout;
    }

    public void setSwipeRefreshLayout(SwipeRefreshLayout swipeRefreshLayout) {
        mSwipeRefreshLayout = swipeRefreshLayout;
    }

    public class FeedViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.imageView) SimpleDraweeView imageView;
        @BindView(R.id.postTitleTextView) TextView postTitleTextView;
        @BindView(R.id.upVoteImageButton) ImageButton upVoteImageButton;
        @BindView(R.id.upVoteCountTextView) TextView upVoteCountTextView;
        @BindView(R.id.commentCountTextView) TextView commentCountTextView;
        @BindView(R.id.shareImageButton) ImageButton shareImageButotn;
        @BindView(R.id.websiteDisplayTextView) TextView websiteDisplayTextView;
        @BindView(R.id.downVoteImageButton) ImageButton downVoteImageButton;
        @BindView(R.id.commentImageButton) ImageButton commentImageButton;

        private int mIndex;

        public FeedViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bindView(int position) {
            mIndex = position;
            Data_ post = mRedditFeed.getData().getChildren().get(position).getData();
            if (post.getPreview() != null) {
                Uri uri = Uri.parse(post.getPreview().getImages().get(0).getSource().getUrl());
                imageView.setImageURI(uri);
            }
            postTitleTextView.setText(post.getTitle());
            upVoteCountTextView.setText(String.valueOf(post.getUps()));
            commentCountTextView.setText(String.valueOf(post.getNumComments()));
            websiteDisplayTextView.setText(post.getDomain());
        }

        @Override
        public void onClick(View v) {
            //TODO:implemept methods from feed itemInterface
        }
    }
}
