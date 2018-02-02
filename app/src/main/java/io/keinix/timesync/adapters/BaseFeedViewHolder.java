package io.keinix.timesync.adapters;

import android.graphics.PorterDuff;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;

import butterknife.BindView;
import butterknife.ButterKnife;
import im.ene.toro.exoplayer.SimpleExoPlayerViewHelper;
import io.keinix.timesync.Fragments.FeedFragment;
import io.keinix.timesync.R;
import io.keinix.timesync.reddit.model.Data_;
import io.keinix.timesync.reddit.model.VoteResult;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BaseFeedViewHolder extends RecyclerView.ViewHolder {

        public static final String TAG = BaseFeedViewHolder.class.getSimpleName();
        public static final String VOTE_TYPE_UPVOTE = "1";
        public static final String VOTE_TYPE_DOWNVOTE = "-1";
        public static final String VOTE_TYPE_UNVOTE = "0";
        public static final int VALUE_UPVOTED = 1;
        public static final int VALUE_DOWNVOTED = -1;
        public static final String VALUE_VIEW_GIF = "VALUE_VIEW_GIF";

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

        @Nullable
        SimpleExoPlayerViewHelper mExoPlayerViewHelper;
        protected FeedFragment.FeedItemInterface mFeedItemInterface;
        protected FeedAdapter mAdapter;

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
            String domain = post.getDomain();
            if (domain.startsWith("self")) {
                domain = "self";
            }
            long timeSincePosted = getTimeSincePosted(post.getCreatedUtc());
            String postInfo = String.format(post.getSubredditNamePrefixed() +
                    " \u2022 " + timeSincePosted + "h" + " \u2022 " +
                    domain);

            postTitleTextView.setText(post.getTitle());
            upVoteCountTextView.setText(String.valueOf(post.getUps()));
            commentCountTextView.setText(String.valueOf(post.getNumComments()));
            websiteDisplayTextView.setText(postInfo);

            setVoteColor(id);
            setVoteOnClick(position, id, post);

        }

    private void setVoteOnClick(int position, String id, Data_ post) {
            upVoteImageButton.setOnClickListener(v -> {
                Log.d(TAG, "ID: " + id +  ": " + mAdapter.mLocalVoteTracker.get(id));
                if (mAdapter.mLocalVoteTracker.get(id) != null) {
                    if (mAdapter.mLocalVoteTracker.get(id).equals(VALUE_UPVOTED)) {
                        unVote(id, position, post);
                    } else {
                        upVote(id, position, post);
                    }
                } else {
                    upVote(id, position, post);
                }});

            downVoteImageButton.setOnClickListener(v -> {
                Log.d(TAG, "ID: " + id +  ": " + mAdapter.mLocalVoteTracker.get(id));
                if (mAdapter.mLocalVoteTracker.get(id) != null) {
                    if (mAdapter.mLocalVoteTracker.get(id).equals(VALUE_DOWNVOTED)) {
                        unVote(id, position, post);
                    } else {
                        downVote(id, position, post);
                    }
                } else {
                    downVote(id, position, post);
                }});
        }

    public void setVoteOnClick(int position, String id, Data_ post,
                                ImageView upVoteImageButton, ImageView downVoteImageButton, TextView upVoteCountTextView) {
        upVoteImageButton.setOnClickListener(v -> {
            Log.d(TAG, "ID: " + id +  ": " + mAdapter.mLocalVoteTracker.get(id));
            if (mAdapter.mLocalVoteTracker.get(id) != null) {
                if (mAdapter.mLocalVoteTracker.get(id).equals(VALUE_UPVOTED)) {
                    unVote(id, position, post);
                    upVoteImageButton.setColorFilter(mColorWhite, PorterDuff.Mode.MULTIPLY);
                    downVoteImageButton.setColorFilter(mColorWhite, PorterDuff.Mode.MULTIPLY);
                    upVoteCountTextView.setTextColor(mDefaultCountTextColor);
                } else {
                    upVote(id, position, post);
                    upVoteImageButton.getDrawable().setColorFilter(mUpVoteColor, PorterDuff.Mode.MULTIPLY);
                    upVoteCountTextView.setTextColor(mUpVoteColor);
                    downVoteImageButton.setColorFilter(mColorWhite, PorterDuff.Mode.MULTIPLY);
                }
            } else {
                upVote(id, position, post);
                upVoteImageButton.getDrawable().setColorFilter(mUpVoteColor, PorterDuff.Mode.MULTIPLY);
                upVoteCountTextView.setTextColor(mUpVoteColor);
                downVoteImageButton.setColorFilter(mColorWhite, PorterDuff.Mode.MULTIPLY);
            }});

        downVoteImageButton.setOnClickListener(v -> {
            Log.d(TAG, "ID: " + id +  ": " + mAdapter.mLocalVoteTracker.get(id));
            if (mAdapter.mLocalVoteTracker.get(id) != null) {
                if (mAdapter.mLocalVoteTracker.get(id).equals(VALUE_DOWNVOTED)) {
                    unVote(id, position, post);
                    upVoteImageButton.setColorFilter(mColorWhite, PorterDuff.Mode.MULTIPLY);
                    downVoteImageButton.setColorFilter(mColorWhite, PorterDuff.Mode.MULTIPLY);
                    upVoteCountTextView.setTextColor(mDefaultCountTextColor);
                } else {
                    downVote(id, position, post);
                    downVoteImageButton.getDrawable().setColorFilter(mDownVoteColor, PorterDuff.Mode.MULTIPLY);
                    upVoteCountTextView.setTextColor(mDownVoteColor);
                    upVoteImageButton.setColorFilter(mColorWhite, PorterDuff.Mode.MULTIPLY);
                }
            } else {
                downVote(id, position, post);
                downVoteImageButton.getDrawable().setColorFilter(mDownVoteColor, PorterDuff.Mode.MULTIPLY);
                upVoteCountTextView.setTextColor(mDownVoteColor);
                upVoteImageButton.setColorFilter(mColorWhite, PorterDuff.Mode.MULTIPLY);
            }});
    }

        //TODO: upVote click effect popUP and feed item
        private void setVoteColor(String id) {
            if (mAdapter.mLocalVoteTracker.get(id) != null) {
                if (mAdapter.mLocalVoteTracker.get(id).equals(VALUE_UPVOTED)) {
                    upVoteImageButton.getDrawable().setColorFilter(mUpVoteColor, PorterDuff.Mode.MULTIPLY);
                    upVoteCountTextView.setTextColor(mUpVoteColor);
                    downVoteImageButton.setColorFilter(mColorWhite, PorterDuff.Mode.MULTIPLY);
                } else {
                    downVoteImageButton.getDrawable().setColorFilter(mDownVoteColor, PorterDuff.Mode.MULTIPLY);
                    upVoteCountTextView.setTextColor(mDownVoteColor);
                    upVoteImageButton.setColorFilter(mColorWhite, PorterDuff.Mode.MULTIPLY);
                }
            } else {
                upVoteImageButton.setColorFilter(mColorWhite, PorterDuff.Mode.MULTIPLY);
                downVoteImageButton.setColorFilter(mColorWhite, PorterDuff.Mode.MULTIPLY);
                upVoteCountTextView.setTextColor(mDefaultCountTextColor);
            }
        }

        //TODO: clean up these API call into a single method
        private void downVote(String id, int position, Data_ post) {

            mFeedItemInterface
                    .vote(id, VOTE_TYPE_DOWNVOTE)
                    .enqueue(new Callback<VoteResult>() {
                        @Override
                        public void onResponse(Call<VoteResult> call, Response<VoteResult> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(mFeedItemInterface.getContext(), "DOWNVOTED", Toast.LENGTH_SHORT).show();
                                if (mAdapter.mLocalVoteTracker.get(id) != null) {
                                    if (mAdapter.mLocalVoteTracker.get(id) == VALUE_UPVOTED) {
                                        post.setUps(post.getUps() - 2);
                                    } else {
                                        post.setUps(post.getUps() - 1);
                                    }
                                } else {
                                    post.setUps(post.getUps() - 1);
                                }
                                mAdapter.mLocalVoteTracker.put(id, VALUE_DOWNVOTED);
                                mAdapter.notifyItemChanged(position);
                            }
                        }

                        @Override
                        public void onFailure(Call<VoteResult> call, Throwable t) {
                            Log.d(TAG, "DownVote onFailure: " + call.toString());
                        }
                    });
        }

        public void upVote(String id, int position, Data_ post) {
            mFeedItemInterface
                    .vote(id, VOTE_TYPE_UPVOTE)
                    .enqueue(new Callback<VoteResult>() {
                        @Override
                        public void onResponse(Call<VoteResult> call, Response<VoteResult> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(mFeedItemInterface.getContext(), "UPVOTED", Toast.LENGTH_SHORT).show();
                                if (mAdapter.mLocalVoteTracker.get(id) != null) {
                                    if (mAdapter.mLocalVoteTracker.get(id) == VALUE_DOWNVOTED) {
                                        post.setUps(post.getUps() + 2);
                                    } else {
                                        post.setUps(post.getUps() + 1);
                                    }
                                } else {
                                    post.setUps(post.getUps() + 1);
                                }
                                mAdapter.mLocalVoteTracker.put(id, VALUE_UPVOTED);
                                mAdapter.notifyItemChanged(position);
                            }
                        }

                        @Override
                        public void onFailure(Call<VoteResult> call, Throwable t) {
                            Log.d(TAG, "UpVote onFailure: " + call.toString());
                        }
                    });
        }

        public void unVote(String id, int position, Data_ post) {
            mFeedItemInterface
                    .vote(id, VOTE_TYPE_UNVOTE)
                    .enqueue(new Callback<VoteResult>() {
                        @Override
                        public void onResponse(Call<VoteResult> call, Response<VoteResult> response) {
                            Toast.makeText(mFeedItemInterface.getContext(), "UN-VOTED", Toast.LENGTH_SHORT).show();
                            if (mAdapter.mLocalVoteTracker.get(id) == VALUE_UPVOTED) {
                                post.setUps(post.getUps() - 1);
                            } else {
                                post.setUps(post.getUps() + 1);
                            }
                            mAdapter.mLocalVoteTracker.remove(id);
                            mAdapter.notifyItemChanged(position);
                        }

                        @Override
                        public void onFailure(Call<VoteResult> call, Throwable t) {

                        }
                    });
        }

        protected long getTimeSincePosted(long createdUtc) {
            long systemTime = System.currentTimeMillis() / 1000;
            return ((systemTime - createdUtc) / 60) / 60;
        }
}

