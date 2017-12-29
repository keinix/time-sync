package io.keinix.timesync.adapters;

import android.net.Uri;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.keinix.timesync.Fragments.FeedFragment.FeedItemInterface;
import io.keinix.timesync.MainActivity;
import io.keinix.timesync.R;
import io.keinix.timesync.reddit.model.Data_;
import io.keinix.timesync.reddit.model.RedditFeed;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FeedAdapter extends RecyclerView.Adapter  implements
        Callback<RedditFeed>, SwipeRefreshLayout.OnRefreshListener {


    public static final String TAG = FeedAdapter.class.getSimpleName();
    private FeedItemInterface mFeedItemInterface;
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

    public SwipeRefreshLayout getSwipeRefreshLayout() {
        return mSwipeRefreshLayout;
    }

    public void setSwipeRefreshLayout(SwipeRefreshLayout swipeRefreshLayout) {
        mSwipeRefreshLayout = swipeRefreshLayout;
    }

    @Override
    public void onResponse(Call<RedditFeed> call, Response<RedditFeed> response) {
        Log.d(TAG, "response "+ response.toString());

        if (response.isSuccessful()) {
            mRedditFeed = response.body();
            notifyDataSetChanged();
            Log.d(TAG, "response was a success! we got the feed!");
            Toast.makeText(mFeedItemInterface.getContext(), "Refresh activated", Toast.LENGTH_SHORT).show();
            getSwipeRefreshLayout().setRefreshing(false);
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
        getSwipeRefreshLayout().setRefreshing(false);
        Log.d(TAG, "onFailure called from populateRedditFeed");
        Log.d(TAG, "Call " + call.toString());
        Log.d(TAG, "Call request header: " + call.request().headers());
        Log.d(TAG, "Call request toString: " + call.request().toString());
        Log.d(TAG, t.toString());
    }

    @Override
    public void onRefresh() {

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
            Uri gifUri = null;
            String postInfo = String.format(post.getSubredditNamePrefixed() +
                    "\\u2022" +
                    post.getDomain());

            if (post.getPreview().getImages().get(0).getVariants().getGif() != null) {
                 gifUri = Uri.parse(post.getPreview()
                        .getImages()
                        .get(0)
                        .getVariants()
                        .getGif()
                        .getSource()
                        .getUrl());
            }

            if (post.getPreview().getImages() != null) {
                if (gifUri != null) {
                    imageView.setImageURI(gifUri);
                } else {
                    Uri uri = Uri.parse(post.getPreview().getImages().get(0).getSource().getUrl());
                    imageView.setImageURI(uri);
                }
            } else {
                imageView.setVisibility(View.GONE);
            }

            postTitleTextView.setText(post.getTitle());
            upVoteCountTextView.setText(String.valueOf(post.getUps()));
            commentCountTextView.setText(String.valueOf(post.getNumComments()));
            websiteDisplayTextView.setText(postInfo);
        }

        @Override
        public void onClick(View v) {
            //TODO:implemept methods from feed itemInterface
        }
    }
}
