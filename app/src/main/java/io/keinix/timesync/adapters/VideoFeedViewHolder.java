package io.keinix.timesync.adapters;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import im.ene.toro.ToroPlayer;
import im.ene.toro.ToroUtil;
import im.ene.toro.exoplayer.SimpleExoPlayerViewHelper;
import im.ene.toro.media.PlaybackInfo;
import im.ene.toro.widget.Container;
import io.keinix.timesync.Activities.CommentsActivity;
import io.keinix.timesync.Fragments.FeedFragment;
import io.keinix.timesync.MainActivity;
import io.keinix.timesync.reddit.ItemDetailsHelper;
import io.keinix.timesync.reddit.model.Data_;

public class VideoFeedViewHolder extends BaseFeedViewHolder implements ToroPlayer{

    private Uri mVideoUri;

    public VideoFeedViewHolder(View itemView, FeedAdapter adapter, FeedFragment.FeedItemInterface feedItemInterface) {
        super(itemView, adapter, feedItemInterface);

    }

    @Override
    public void bindView(int position) {
        super.bindView(position);
        Data_ post = super.mAdapter.getRedditFeed().getData().getChildren().get(position).getData();
        setViewIcon(post);
        if (post.getCrossLinks() != null) {
            mVideoUri = Uri.parse(post.getCrossLinks().get(0).getMedia().getRedditVideo().getScrubberMediaUrl());
        } else {
            mVideoUri = Uri.parse(post.getMedia().getRedditVideo().getScrubberMediaUrl());
        }
//        mVideoUri = Uri.parse(mAdapter
//                .getRedditFeed()
//                .getData()
//                .getChildren()
//                .get(position)
//                .getData()
//                .getMedia()
//                .getRedditVideo()
//                .getScrubberMediaUrl());

        commentImageButton.setOnClickListener(v -> {
            long timeSincePosted = getTimeSincePosted(post.getCreatedUtc());
            String postDetails = "u/" +post.getAuthor() + " \u2022 "
                    + timeSincePosted + "h";

            Intent intent = new Intent(mFeedItemInterface.getContext(), CommentsActivity.class);
            intent.putExtra(CommentsActivity.KEY_COMMENTS_LAYOUT_TYPE, CommentsActivity.VALUE_VIDEO_COMMENTS_LAYOUT);
            intent.putExtra(CommentsActivity.KEY_VIDEO_URI, mVideoUri.toString());
            intent.putExtra(CommentsActivity.KEY_POST_SUBREDDIT, post.getSubredditNamePrefixed());
            intent.putExtra(CommentsActivity.KEY_POST_SUBREDDIT_NO_PREFIX, post.getSubreddit());
            intent.putExtra(CommentsActivity.KEY_POST_TITLE, post.getTitle());
            intent.putExtra(CommentsActivity.KEY_POST_ID, post.getName());
            intent.putExtra(CommentsActivity.KEY_POST_DETAILS, postDetails);
            intent.putExtra(CommentsActivity.KEY_VOTE_TYPE, ItemDetailsHelper.parseVoteType(post.isLiked()));
            intent.putExtra(CommentsActivity.KEY_POST_ARTICLE, post.getId());
            intent.putExtra(CommentsActivity.KEY_VOTE_COUNT, post.getUps());
            intent.putExtra(CommentsActivity.KEY_INIT_VOTE_TYPE, mRedditVoteHelper.getVoteStatus());
            intent.putExtra(CommentsActivity.KEY_ORIGINAL_POST_POSITION, position);
            ((MainActivity) mFeedItemInterface.getContext()).startActivityForResult(intent, CommentsActivity.REQUEST_CODE);
        });
    }

    private void setViewIcon(Data_ post) {
        if (post.getPostHint() != null) {
            if (post.getPostHint().equals("link") && !post.isRedditMediaDomain()) {
                linkImageView.setVisibility(View.VISIBLE);
            } else {
                linkImageView.setVisibility(View.GONE);
            }
        } else {
            linkImageView.setVisibility(View.GONE);
        }

        if (post.isSelf() != null) {
            if (post.isSelf() && post.getPreview() != null) {
                selfTextIconImageView.setVisibility(View.VISIBLE);
            } else {
                selfTextIconImageView.setVisibility(View.GONE);
            }
        } else {
            selfTextIconImageView.setVisibility(View.GONE);
        }
    }

    @NonNull
    @Override
    public View getPlayerView() {
        return mExoPlayer;
    }

    @NonNull
    @Override
    public PlaybackInfo getCurrentPlaybackInfo() {
        return mExoPlayerViewHelper != null ?
                mExoPlayerViewHelper.getLatestPlaybackInfo() : new PlaybackInfo();
    }

    @Override
    public void initialize(@NonNull Container container, @Nullable PlaybackInfo playbackInfo) {
        if (mExoPlayerViewHelper == null) {
            mExoPlayerViewHelper = new SimpleExoPlayerViewHelper(container, this, mVideoUri);
        }
        mExoPlayerViewHelper.initialize(playbackInfo);
    }

    @Override
    public void play() {
        if (mExoPlayerViewHelper != null) mExoPlayerViewHelper.play();
    }

    @Override
    public void pause() {
        if (mExoPlayerViewHelper != null) mExoPlayerViewHelper.pause();
    }

    @Override
    public boolean isPlaying() {
        return mExoPlayerViewHelper != null && mExoPlayerViewHelper.isPlaying();
    }

    @Override
    public void release() {
        if (mExoPlayerViewHelper != null) {
            mExoPlayerViewHelper.release();
            mExoPlayerViewHelper = null;
        }
    }

    @Override
    public boolean wantsToPlay() {
        return ToroUtil.visibleAreaOffset(this, itemView.getParent()) >= .85;
    }

    @Override
    public int getPlayerOrder() {
        return getAdapterPosition();
    }

    @Override
    public void onSettled(Container container) {

    }
}
