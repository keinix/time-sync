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
        mVideoUri = Uri.parse(mAdapter
                .getRedditFeed()
                .getData()
                .getChildren()
                .get(position)
                .getData()
                .getMedia()
                .getRedditVideo()
                .getScrubberMediaUrl());

        commentImageButton.setOnClickListener(v -> {
            Intent intent = new Intent(mFeedItemInterface.getContext(), CommentsActivity.class);
            intent.putExtra(CommentsActivity.KEY_COMMENTS_VIEW_TYPE, CommentsActivity.VALUE_VIDEO_COMMENTS_VIEW);
            mFeedItemInterface.getContext().startActivity(intent);
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
