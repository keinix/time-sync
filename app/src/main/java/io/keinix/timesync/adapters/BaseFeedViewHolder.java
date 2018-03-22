package io.keinix.timesync.adapters;

import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;

import butterknife.BindView;
import butterknife.ButterKnife;
import im.ene.toro.exoplayer.SimpleExoPlayerViewHelper;
import io.keinix.timesync.Fragments.FeedFragment;
import io.keinix.timesync.R;
import io.keinix.timesync.reddit.ItemDetailsHelper;
import io.keinix.timesync.reddit.RedditVoteHelper;
import io.keinix.timesync.reddit.model.Data_;
import io.keinix.timesync.utils.ShareUtil;

public class BaseFeedViewHolder extends RecyclerView.ViewHolder {

    public static final String TAG = BaseFeedViewHolder.class.getSimpleName();
    public static final String VOTE_TYPE_UPVOTE = "1";
    public static final String VOTE_TYPE_DOWNVOTE = "-1";
    public static final String VOTE_TYPE_UNVOTE = "0";
    public static final int VALUE_UPVOTED = 1;
    public static final int VALUE_DOWNVOTED = -1;
    public static final String VALUE_VIEW_GIF = "VALUE_VIEW_GIF";

    @Nullable SimpleExoPlayerViewHelper mExoPlayerViewHelper;
    @Nullable @BindView(R.id.selfTextTextView) TextView selfTextView;
    @Nullable @BindView(R.id.imageView) SimpleDraweeView postImageView;
    @Nullable @BindView(R.id.selfTextIconImageView) ImageView selfTextIconImageView;
    @Nullable @BindView(R.id.exoPlayer) SimpleExoPlayerView mExoPlayer;
    @BindView(R.id.postTitleTextView) TextView postTitleTextView;
    @BindView(R.id.upVoteImageButton) ImageButton upVoteImageButton;
    @BindView(R.id.upVoteCountTextView) TextView upVoteCountTextView;
    @BindView(R.id.commentCountTextView) TextView commentCountTextView;
    @BindView(R.id.shareImageButton) ImageButton shareImageButton;
    @BindView(R.id.websiteDisplayTextView) TextView websiteDisplayTextView;
    @BindView(R.id.downVoteImageButton) ImageButton downVoteImageButton;
    @BindView(R.id.commentImageButton) ImageButton commentImageButton;
    @BindView(R.id.linkImageView) ImageView linkImageView;

    protected FeedFragment.FeedItemInterface mFeedItemInterface;
    protected FeedAdapter mAdapter;
    protected RedditVoteHelper mBaseRedditVoteHelper;

    protected int mIndex;
    protected int mUpVoteColor;
    protected int mDownVoteColor;
    protected int mDefaultCountTextColor;
    protected int mColorWhite;

    public BaseFeedViewHolder(View itemView, FeedAdapter adapter, FeedFragment.FeedItemInterface feedItemInterface) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        mFeedItemInterface = feedItemInterface;
        mAdapter = adapter;

        mUpVoteColor = ContextCompat.getColor(mFeedItemInterface.getContext(), R.color.upVoteColor);
        mDownVoteColor = ContextCompat.getColor(mFeedItemInterface.getContext(), R.color.downVoteColor);
        mDefaultCountTextColor = ContextCompat.getColor(mFeedItemInterface.getContext(), R.color.colorCountText);
        mColorWhite = ContextCompat.getColor(mFeedItemInterface.getContext(), R.color.white);
    }

    public void bindView(int position) {
        upVoteImageButton.getDrawable().mutate();
        downVoteImageButton.getDrawable().mutate();
        mIndex = position;
        Data_ post = mAdapter.getRedditFeed().getData().getChildren().get(position).getData();
        String id = post.getName();
        postTitleTextView.setText(post.getTitle());
        upVoteCountTextView.setText(String.valueOf(post.getUps()));
        commentCountTextView.setText(String.valueOf(post.getNumComments()));
        websiteDisplayTextView.setText(ItemDetailsHelper.getPostDetails(post));
        String shareText = "www.reddit.com" + post.getPermalink();
        shareImageButton.setOnClickListener(v -> ShareUtil.shareText(mFeedItemInterface.getContext(), shareText));

        mBaseRedditVoteHelper = new RedditVoteHelper(mFeedItemInterface.getContext(),
                upVoteImageButton, downVoteImageButton, upVoteCountTextView, mFeedItemInterface.getApi(),
                ItemDetailsHelper.parseVoteType(post.isLiked()), id);
    }

    protected long getTimeSincePosted(long createdUtc) {
        long systemTime = System.currentTimeMillis() / 1000;
        return ((systemTime - createdUtc) / 60) / 60;
    }
}

