package io.keinix.timesync.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.keinix.timesync.Fragments.FeedFragment.FeedItemInterface;
import io.keinix.timesync.R;
import io.keinix.timesync.reddit.model.Data_;
import io.keinix.timesync.reddit.model.Image;
import io.keinix.timesync.reddit.model.RedditFeed;

public class FeedAdapter extends RecyclerView.Adapter {
    private   FeedItemInterface mFeedItemInterface;
    private RedditFeed mRedditFeed;

    public FeedAdapter(FeedItemInterface feedItemInterface) {
        mFeedItemInterface = feedItemInterface;
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
        return mRedditFeed.getData().getChildren().size();
    }

    public RedditFeed getRedditFeed() {
        return mRedditFeed;
    }

    public void setRedditFeed(RedditFeed redditFeed) {
        mRedditFeed = redditFeed;
    }


    private class FeedViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.imageView) ImageView imageView;
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
            postTitleTextView.setText(post.getTitle());
            upVoteCountTextView.setText(post.getUps());
            commentCountTextView.setText(String.valueOf(post.getNumComments()));
            websiteDisplayTextView.setText(post.getDomain());
        }

        @Override
        public void onClick(View v) {
            //TODO:implemept methods from feed itemInterface
        }
    }
}
