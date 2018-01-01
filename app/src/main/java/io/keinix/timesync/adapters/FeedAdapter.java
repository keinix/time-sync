package io.keinix.timesync.adapters;

import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.keinix.timesync.Fragments.FeedFragment.FeedItemInterface;
import io.keinix.timesync.R;
import io.keinix.timesync.reddit.model.Data_;
import io.keinix.timesync.reddit.model.RedditFeed;
import io.keinix.timesync.reddit.model.VoteResult;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FeedAdapter extends RecyclerView.Adapter  implements
        Callback<RedditFeed>  {


    public static final String TAG = FeedAdapter.class.getSimpleName();
    private FeedItemInterface mFeedItemInterface;
    private RedditFeed mRedditFeed;


    public FeedAdapter(FeedItemInterface feedItemInterface) {
        mFeedItemInterface = feedItemInterface;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.feed_item, parent, false);
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

    @Override
    public void onResponse(Call<RedditFeed> call, Response<RedditFeed> response) {
        Log.d(TAG, "response "+ response.toString());

        if (response.isSuccessful()) {
            mRedditFeed = response.body();
            notifyDataSetChanged();
            Log.d(TAG, "response was a success! we got the feed!");
            Toast.makeText(mFeedItemInterface.getContext(), "Refresh activated", Toast.LENGTH_SHORT).show();
        } else {
            Log.d(TAG, "responce was not successfull triggered");
            // mAccountManager.invalidateAuthToken(RedditConstants.ACCOUNT_TYPE,
            // mAccountManager.peekAuthToken(accounts[0], RedditConstants.KEY_AUTH_TOKEN));
            // getRefreshToken(methodToRetry);
            //TODO: store an attempt constant so if it keeps failing you can prompt reLogin
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

        @BindView(R.id.imageView) SimpleDraweeView imageView;
        @BindView(R.id.postTitleTextView) TextView postTitleTextView;
        @BindView(R.id.upVoteImageButton) ImageButton upVoteImageButton;
        @BindView(R.id.upVoteCountTextView) TextView upVoteCountTextView;
        @BindView(R.id.commentCountTextView) TextView commentCountTextView;
        @BindView(R.id.shareImageButton) ImageButton shareImageButton;
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
            String id = post.getName();
            String postInfo = String.format(post.getSubredditNamePrefixed() +
                    " \u2022 " +
                    post.getDomain());

            postTitleTextView.setText(post.getTitle());
            upVoteCountTextView.setText(String.valueOf(post.getUps()));
            commentCountTextView.setText(String.valueOf(post.getNumComments()));
            websiteDisplayTextView.setText(postInfo);

            handleImage(post);
            setupVoteOnClick(id);
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

        private void setupVoteOnClick(String id) {
            String voteType = null;
            //TODO: store votes in a hashmap with the id as a key
            //TODO: clear the hashmap in a refresh b/c the JSON response will have the value

            upVoteImageButton.setOnClickListener(v -> mFeedItemInterface
                    .vote(id, voteType)
                    .enqueue(new Callback<VoteResult>() {
                        @Override
                        public void onResponse(Call<VoteResult> call, Response<VoteResult> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(mFeedItemInterface.getContext(), "UPVOTED", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<VoteResult> call, Throwable t) {
                            Log.d(TAG, "UpVote onFailure: " + call.toString());
                        }
                    }));

            downVoteImageButton.setOnClickListener(v -> mFeedItemInterface
                    .vote(id, voteType)
                    .enqueue(new Callback<VoteResult>() {
                        @Override
                        public void onResponse(Call<VoteResult> call, Response<VoteResult> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(mFeedItemInterface.getContext(), "DOWNVOTED", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<VoteResult> call, Throwable t) {
                            Log.d(TAG, "DownVote onFailure: " + call.toString());
                        }
                    }));
        }

        @Override
        public void onClick(View v) {
            //TODO:implemept methods from feed itemInterface
        }
    }
}
