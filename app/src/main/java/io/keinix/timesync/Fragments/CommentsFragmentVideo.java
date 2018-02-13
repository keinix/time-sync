package io.keinix.timesync.Fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import im.ene.toro.ToroPlayer;
import im.ene.toro.ToroUtil;
import im.ene.toro.exoplayer.SimpleExoPlayerViewHelper;
import im.ene.toro.media.PlaybackInfo;
import im.ene.toro.widget.Container;
import io.keinix.timesync.Activities.CommentsActivity;


public class CommentsFragmentVideo extends CommentsFragment implements ToroPlayer {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
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
        return ToroUtil.visibleAreaOffset(this, mView.getParent()) >= .85;
    }

    @Override
    public int getPlayerOrder() {
        return 0;
    }

    @Override
    public void onSettled(Container container) {

    }
}
