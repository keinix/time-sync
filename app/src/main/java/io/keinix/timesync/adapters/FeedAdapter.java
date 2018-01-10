package io.keinix.timesync.adapters;

import android.graphics.PorterDuff;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Optional;
import io.keinix.timesync.Fragments.FeedFragment.FeedItemInterface;
import io.keinix.timesync.R;
import io.keinix.timesync.reddit.model.Child;
import io.keinix.timesync.reddit.model.Data_;
import io.keinix.timesync.reddit.model.RedditFeed;
import io.keinix.timesync.reddit.model.VoteResult;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static java.lang.System.currentTimeMillis;

public class FeedAdapter extends RecyclerView.Adapter  implements
        Callback<RedditFeed>  {

    private static final String TAG = FeedAdapter.class.getSimpleName();
    private static final int VALUE_UPVOTED = 1;
    private static final int VALUE_DOWNVOTED = -1;
    private static final int VIEW_ITEM_TYPE_IMAGE = 100;
    private static final int VIEW_ITEM_TYPE_TEXT = 200;
    private static final String VOTE_TYPE_UPVOTE = "1";
    private static final String VOTE_TYPE_DOWNVOTE = "-1";
    private static final String VOTE_TYPE_UNVOTE = "0";


    private FeedItemInterface mFeedItemInterface;
    private RedditFeed mRedditFeed;
    public Map<String, Integer> mLocalVoteTracker;


    public FeedAdapter(FeedItemInterface feedItemInterface) {
        mFeedItemInterface = feedItemInterface;
        mLocalVoteTracker = new HashMap<>();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View imageItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.feed_item, parent, false);
        View textItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.text_feed_item, parent, false);
        if (viewType == VIEW_ITEM_TYPE_TEXT) { return new FeedViewHolder(textItem); }
        return new FeedViewHolder(imageItem);
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

    @Override
    public int getItemViewType(int position) {
        Data_ post = mRedditFeed.getData().getChildren().get(position).getData();
        if (post.getSelfText().length() > 2 && post.getPreview() == null) {
            return VIEW_ITEM_TYPE_TEXT;
        }
        return VIEW_ITEM_TYPE_IMAGE;
    }



    @Override
    public void onResponse(Call<RedditFeed> call, Response<RedditFeed> response) {
        Log.d(TAG, "response "+ response.toString());

        if (response.isSuccessful()) {
            mRedditFeed = response.body();

            for (Child child : mRedditFeed.getData().getChildren()) {
                if (child.getData().isLiked() != null) {
                    if (child.getData().isLiked()) {
                        mLocalVoteTracker.put(child.getData().getName(), VALUE_UPVOTED);
                    } else {
                        mLocalVoteTracker.put(child.getData().getName(), VALUE_DOWNVOTED);
                    }
                }
            }

            notifyDataSetChanged();
            Log.d(TAG, "response was a success! we got the feed!");
            Toast.makeText(mFeedItemInterface.getContext(), "Refresh activated", Toast.LENGTH_SHORT).show();
        } else {
            Log.d(TAG, "responce was not successfull triggered");
        }
    }

    @Override
    public void onFailure(Call<RedditFeed> call, Throwable t) {
        Log.d(TAG, "onFailure called from populateRedditFeed");
        Log.d(TAG, "Call " + call.toString());
        Log.d(TAG, "Call request header: " + call.request().headers());
        Log.d(TAG, "Call request toString: " + call.request().toString());
        Log.d(TAG, t.toString());
    }


    public class FeedViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @Nullable @BindView(R.id.imageView) SimpleDraweeView imageView;
        @Nullable @BindView(R.id.selfTextTextView) TextView selfTextView;
        @BindView(R.id.postTitleTextView) TextView postTitleTextView;
        @BindView(R.id.upVoteImageButton) ImageButton upVoteImageButton;
        @BindView(R.id.upVoteCountTextView) TextView upVoteCountTextView;
        @BindView(R.id.commentCountTextView) TextView commentCountTextView;
        @BindView(R.id.shareImageButton) ImageButton shareImageButton;
        @BindView(R.id.websiteDisplayTextView) TextView websiteDisplayTextView;
        @BindView(R.id.downVoteImageButton) ImageButton downVoteImageButton;
        @BindView(R.id.commentImageButton) ImageButton commentImageButton;
        @BindView(R.id.linkImageView) ImageView linkImageView;

        private int mIndex;
        private int mUpVoteColor;
        private int mDownVoteColor;
        private int mDefaultCountTextColor;
        private int mColorWhite;

        public FeedViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mUpVoteColor = ContextCompat.getColor(mFeedItemInterface.getContext(), R.color.upVoteColor);
            mDownVoteColor = ContextCompat.getColor(mFeedItemInterface.getContext(), R.color.downVoteColor);
            mDefaultCountTextColor = ContextCompat.getColor(mFeedItemInterface.getContext(), R.color.colorCountText);
            mColorWhite = ContextCompat.getColor(mFeedItemInterface.getContext(), R.color.white);
        }

        public void bindView(int position) {
            upVoteImageButton.getDrawable().mutate();
            downVoteImageButton.getDrawable().mutate();
            mIndex = position;
            Data_ post = mRedditFeed.getData().getChildren().get(position).getData();
            String id = post.getName();
            long timeSincePosted = getTimeSincePosted(post.getCreatedUtc());
            String postInfo = String.format(post.getSubredditNamePrefixed() +
                    " \u2022 " + timeSincePosted + "h" + " \u2022 " +
                    post.getDomain());

            if (post.getPostHint() != null) {
                if (post.getPostHint().equals("link") && !post.isRedditMediaDomain()) {
                    linkImageView.setVisibility(View.VISIBLE);
                } else {
                    linkImageView.setVisibility(View.GONE);
                }
            } else {
                linkImageView.setVisibility(View.GONE);
            }

            postTitleTextView.setText(post.getTitle());
            upVoteCountTextView.setText(String.valueOf(post.getUps()));
            commentCountTextView.setText(String.valueOf(post.getNumComments()));
            websiteDisplayTextView.setText(postInfo);

            if (post.getSelfText().length() > 2 && post.getPreview() == null) {
                selfTextView.setText(post.getSelfText());
            } else {
                handleImage(post);
            }
            setVoteColor(id);
            setVoteOnClick(position, id);
        }

        private void setVoteOnClick(int position, String id) {
            upVoteImageButton.setOnClickListener(v -> {
                Log.d(TAG, "ID: " + id +  ": " + mLocalVoteTracker.get(id));
                if (mLocalVoteTracker.get(id) != null) {
                    if (mLocalVoteTracker.get(id).equals(VALUE_UPVOTED)) {
                        unVote(id, position);
                    } else {
                        upVote(id, position);
                    }
                } else {
                    upVote(id, position);
                }});

            downVoteImageButton.setOnClickListener(v -> {
                Log.d(TAG, "ID: " + id +  ": " + mLocalVoteTracker.get(id));
                if (mLocalVoteTracker.get(id) != null) {
                    if (mLocalVoteTracker.get(id).equals(VALUE_DOWNVOTED)) {
                        unVote(id, position);
                    } else {
                        downVote(id, position);
                    }
                } else {
                    downVote(id, position);
                }});
        }

        private void setVoteColor(String id) {
            if (mLocalVoteTracker.get(id) != null) {
                if (mLocalVoteTracker.get(id).equals(VALUE_UPVOTED)) {
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

        private void handleImage(Data_ post) {
            Uri gifUri = null;
            if (post.getPreview()!= null) {

                if (post.getPreview().getImages().get(0).getVariants().getGif() != null) {
                    gifUri = Uri.parse(post.getPreview()
                            .getImages()
                            .get(0)
                            .getVariants()
                            .getGif()
                            .getSource()
                            .getUrl());

                } else if (post.getMedia() != null && post.isRedditMediaDomain()) {
                    gifUri = Uri.parse(post.getMedia().getRedditVideo().getScrubberMediaUrl());
                }


                if (gifUri != null) {
                    DraweeController controller = Fresco.newDraweeControllerBuilder()
                            .setUri(gifUri)
                            .setAutoPlayAnimations(true)
                            .build();
                    imageView.setController(controller);
                } else {
                    Uri uri = Uri.parse(post.getPreview().getImages().get(0).getSource().getUrl());
                    imageView.setImageURI(uri);
                }
            }
        }

        //TODO: clean up these API call into a single method
        private void downVote(String id, int position) {

            mFeedItemInterface
                    .vote(id, VOTE_TYPE_DOWNVOTE)
                    .enqueue(new Callback<VoteResult>() {
                        @Override
                        public void onResponse(Call<VoteResult> call, Response<VoteResult> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(mFeedItemInterface.getContext(), "DOWNVOTED", Toast.LENGTH_SHORT).show();
                                mLocalVoteTracker.put(id, VALUE_DOWNVOTED);
                                notifyItemChanged(position);
                            }
                        }

                        @Override
                        public void onFailure(Call<VoteResult> call, Throwable t) {
                            Log.d(TAG, "DownVote onFailure: " + call.toString());
                        }
                    });
        }

        private void upVote(String id, int position) {
            mFeedItemInterface
                    .vote(id, VOTE_TYPE_UPVOTE)
                    .enqueue(new Callback<VoteResult>() {
                        @Override
                        public void onResponse(Call<VoteResult> call, Response<VoteResult> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(mFeedItemInterface.getContext(), "UPVOTED", Toast.LENGTH_SHORT).show();
                                mLocalVoteTracker.put(id, VALUE_UPVOTED);
                                notifyItemChanged(position);
                            }
                        }

                        @Override
                        public void onFailure(Call<VoteResult> call, Throwable t) {
                            Log.d(TAG, "UpVote onFailure: " + call.toString());
                        }
                    });
        }

        public void unVote(String id, int position) {
            mFeedItemInterface
                    .vote(id, VOTE_TYPE_UNVOTE)
                    .enqueue(new Callback<VoteResult>() {
                        @Override
                        public void onResponse(Call<VoteResult> call, Response<VoteResult> response) {
                            Toast.makeText(mFeedItemInterface.getContext(), "UN-VOTED", Toast.LENGTH_SHORT).show();
                            mLocalVoteTracker.remove(id);
                            notifyItemChanged(position);
                        }

                        @Override
                        public void onFailure(Call<VoteResult> call, Throwable t) {

                        }
                    });
        }

        private long getTimeSincePosted(long createdUtc) {
            long systemTime = System.currentTimeMillis() / 1000;
            return ((systemTime - createdUtc) / 60) / 60;
        }
        @Override
        public void onClick(View v) {
            //TODO:implemept methods from feed itemInterface
        }

    }
}
