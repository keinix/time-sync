package io.keinix.timesync.adapters;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import io.keinix.timesync.Activities.CommentsActivity;
import io.keinix.timesync.Activities.SubredditActivity;
import io.keinix.timesync.Fragments.FeedFragment;
import io.keinix.timesync.MainActivity;
import io.keinix.timesync.R;
import io.keinix.timesync.reddit.ItemDetailsHelper;
import io.keinix.timesync.reddit.model.Data_;

public class TextFeedViewHolder extends BaseFeedViewHolder {

    @Nullable @BindView(R.id.selfTextTextView) TextView selfTextView;

    public TextFeedViewHolder(View itemView, FeedAdapter adapter, FeedFragment.FeedItemInterface feedItemInterface) {
        super(itemView, adapter, feedItemInterface);
    }

    @Override
    public void bindView(int position) {
        super.bindView(position);
        Data_ post = mAdapter.getRedditFeed().getData().getChildren().get(position).getData();
        selfTextView.setText(post.getSelfText());
        long timeSincePosted = getTimeSincePosted(post.getCreatedUtc());
        String postDetails = "u/" +post.getAuthor() + " \u2022 "
                + timeSincePosted + "h";
        selfTextView.setOnClickListener(v -> launchCommentsActivity(post, postDetails, position));
        commentImageButton.setOnClickListener(v -> launchCommentsActivity(post, postDetails, position));
        commentCountTextView.setOnClickListener(v -> launchCommentsActivity(post, postDetails, position));
    }

    private void launchCommentsActivity(Data_ post, String postDetails, int position) {
        Intent intent = new Intent(mFeedItemInterface.getContext(), CommentsActivity.class);
        intent.putExtra(CommentsActivity.KEY_COMMENTS_LAYOUT_TYPE, CommentsActivity.VALUE_TEXT_COMMENTS_LAYOUT);
        intent.putExtra(CommentsActivity.KEY_POST_SUBREDDIT, post.getSubredditNamePrefixed());
        intent.putExtra(CommentsActivity.KEY_POST_SUBREDDIT_NO_PREFIX, post.getSubreddit());
        intent.putExtra(CommentsActivity.KEY_POST_TITLE, post.getTitle());
        intent.putExtra(CommentsActivity.KEY_POST_ID, post.getName());
        intent.putExtra(CommentsActivity.KEY_POST_DETAILS, postDetails);
        intent.putExtra(CommentsActivity.KEY_POST_ARTICLE, post.getId());
        intent.putExtra(CommentsActivity.KEY_VOTE_TYPE, ItemDetailsHelper.parseVoteType(post.isLiked()));
        intent.putExtra(CommentsActivity.KEY_SELF_TEXT, post.getSelfText());
        intent.putExtra(CommentsActivity.KEY_VOTE_COUNT, post.getUps());
        intent.putExtra(CommentsActivity.KEY_ORIGINAL_POST_POSITION, position);
        intent.putExtra(CommentsActivity.KEY_INIT_VOTE_TYPE, mBaseRedditVoteHelper.getVoteStatus());

        Log.d(TAG, "isFromSubReddit: " + mAdapter.isFromSubReddit());
        if (mAdapter.isFromSubReddit()) {
            ((SubredditActivity) mFeedItemInterface.getContext()).startActivityForResult(intent, CommentsActivity.REQUEST_CODE);
        } else {
            ((MainActivity) mFeedItemInterface.getContext()).startActivityForResult(intent, CommentsActivity.REQUEST_CODE);
        }
    }
}
