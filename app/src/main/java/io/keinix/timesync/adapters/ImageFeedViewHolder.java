package io.keinix.timesync.adapters;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;

import io.keinix.timesync.Activities.CommentsActivity;
import io.keinix.timesync.Fragments.FeedFragment;
import io.keinix.timesync.MainActivity;
import io.keinix.timesync.R;
import io.keinix.timesync.reddit.ItemDetailsHelper;
import io.keinix.timesync.reddit.model.Data_;
import io.keinix.timesync.utils.OnSwipeTouchListener;


public class ImageFeedViewHolder extends BaseFeedViewHolder {

    SimpleDraweeView mPopUpDraweeView;
    ImageButton mPopUpUpVoteImageButton;
    ImageButton mPopUpDownVoteImageButton;
    ImageButton mPopUpCommentImageButton;
    ImageButton mPopUpShareImageButton;
    TextView mPopUpCommentCountTextView;
    TextView mPopUpVoteCountTextView;

    private boolean isGif;
    private Uri mGifUri;

    public ImageFeedViewHolder(View itemView, FeedAdapter adapter, FeedFragment.FeedItemInterface feedItemInterface) {
        super(itemView, adapter, feedItemInterface);
    }

    @Override
    public void bindView(int position) {
        super.bindView(position);
        Data_ post =  mAdapter.getRedditFeed().getData().getChildren().get(position).getData();
        setPostImage(post, postImageView);
        setViewIcon(post);
        postImageView.setOnClickListener(view -> {
            if (post.getPostHint().equals("link") && !post.isRedditMediaDomain() &&
                    !post.getDomain().equals("i.imgur.com")) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(post.getUrl()));
                mFeedItemInterface.getContext().startActivity(intent);
            } else {
                Log.d(TAG, "onClick Called");
                showPopUp(post);
            }
        });

        commentImageButton.setOnClickListener(v -> {
            Intent intent = new Intent(mFeedItemInterface.getContext(), CommentsActivity.class);
            if (isGif) {
                intent.putExtra(CommentsActivity.KEY_COMMENTS_LAYOUT_TYPE, CommentsActivity.VALUE_GIF_COMMENTS_LAYOUT);
            } else {
                intent.putExtra(CommentsActivity.KEY_COMMENTS_LAYOUT_TYPE, CommentsActivity.VALUE_IMAGE_COMMENTS_LAYOUT);
            }

            packIntent(post, intent, position);
            ((MainActivity) mFeedItemInterface.getContext()).startActivityForResult(intent, CommentsActivity.REQUEST_CODE);
        });
    }

    private void packIntent(Data_ post, Intent intent, int position) {
        long timeSincePosted = getTimeSincePosted(post.getCreatedUtc());
        String domain = post.getDomain();
        if (domain.startsWith("self")) {
            domain = "self";
        }
        String postDetails = "u/" +post.getAuthor() + " \u2022 "
                + timeSincePosted + "h" + " \u2022 " +
                domain;
        if (isGif) {
            intent.putExtra(CommentsActivity.KEY_IMAGE_URL, mGifUri.toString());
        } else {
            intent.putExtra(CommentsActivity.KEY_IMAGE_URL, post.getPreview().getImages().get(0).getSource().getUrl());
        }

        intent.putExtra(CommentsActivity.KEY_INIT_VOTE_TYPE, mRedditVoteHelper.getVoteStatus());
        intent.putExtra(CommentsActivity.KEY_POST_SUBREDDIT, post.getSubredditNamePrefixed());
        intent.putExtra(CommentsActivity.KEY_VOTE_TYPE, ItemDetailsHelper.parseVoteType(post.isLiked()));
        intent.putExtra(CommentsActivity.KEY_POST_SUBREDDIT_NO_PREFIX, post.getSubreddit());
        intent.putExtra(CommentsActivity.KEY_POST_TITLE, post.getTitle());
        intent.putExtra(CommentsActivity.KEY_POST_ID, post.getName());
        intent.putExtra(CommentsActivity.KEY_POST_DETAILS, postDetails);
        intent.putExtra(CommentsActivity.KEY_POST_ARTICLE, post.getId());
        intent.putExtra(CommentsActivity.KEY_VOTE_COUNT, post.getUps());
        intent.putExtra(CommentsActivity.KEY_ORIGINAL_POST_POSITION, position);
    }


    private void showPopUp(Data_ post) {
        View popUpView = setUpPopUpView(post);
        PopupWindow popupWindow = new PopupWindow(popUpView,
                WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        popUpView.setOnTouchListener(new OnSwipeTouchListener(mFeedItemInterface.getContext()) {
            @Override
            public void onSwipeTop() { popupWindow.dismiss(); }

            @Override
            public void onSwipeBottom() { popupWindow.dismiss(); }

            @Override
            public void onSwipeRight() { popupWindow.dismiss(); }

            @Override
            public void onSwipeLeft() { popupWindow.dismiss(); }
        });

        popupWindow.showAsDropDown(popUpView, 0, 0);
    }

    private View setUpPopUpView(Data_ post) {
        View popUpView = LayoutInflater.from(mFeedItemInterface.getContext()).inflate(R.layout.pop_up_feed_image, null);
         mPopUpDraweeView = popUpView.findViewById(R.id.popUpDraweeView);
         mPopUpUpVoteImageButton = popUpView.findViewById(R.id.popUpUpVoteImageButton);
         mPopUpDownVoteImageButton = popUpView.findViewById(R.id.popUpDownVoteImageButton);
         mPopUpCommentImageButton = popUpView.findViewById(R.id.popUpCommentImageButton);
         mPopUpShareImageButton = popUpView.findViewById(R.id.popUpShareImageButton);
         mPopUpCommentCountTextView = popUpView.findViewById(R.id.popUpCommentCountTextView);
         mPopUpVoteCountTextView = popUpView.findViewById(R.id.popUpVoteCountTextView);

        setPoPUpVoteColor(post.getName());
        setVoteOnClick(mIndex, post.getName(), post, mPopUpUpVoteImageButton, mPopUpDownVoteImageButton, mPopUpVoteCountTextView);
        setPostImage(post, mPopUpDraweeView);

        return popUpView;
    }

    private void setPoPUpVoteColor(String id) {
        if (mAdapter.mLocalVoteTracker.get(id) != null) {
            if (mAdapter.mLocalVoteTracker.get(id).equals(VALUE_UPVOTED)) {
                mPopUpUpVoteImageButton.getDrawable().setColorFilter(mUpVoteColor, PorterDuff.Mode.MULTIPLY);
                mPopUpVoteCountTextView.setTextColor(mUpVoteColor);
                mPopUpDownVoteImageButton.setColorFilter(mColorWhite, PorterDuff.Mode.MULTIPLY);
            } else {
                mPopUpDownVoteImageButton.getDrawable().setColorFilter(mDownVoteColor, PorterDuff.Mode.MULTIPLY);
                mPopUpVoteCountTextView.setTextColor(mDownVoteColor);
                mPopUpDownVoteImageButton.setColorFilter(mColorWhite, PorterDuff.Mode.MULTIPLY);
            }
        } else {
            mPopUpUpVoteImageButton.setColorFilter(mColorWhite, PorterDuff.Mode.MULTIPLY);
            mPopUpDownVoteImageButton.setColorFilter(mColorWhite, PorterDuff.Mode.MULTIPLY);
            mPopUpVoteCountTextView.setTextColor(mDefaultCountTextColor);
        }
    }

    private void setPostImage(Data_ post, SimpleDraweeView imageView) {
        mGifUri = null;
        isGif = false;
        if (post.getPreview()!= null) {

            if (post.getPreview().getImages().get(0).getVariants().getGif() != null) {
                mGifUri = Uri.parse(post.getPreview()
                        .getImages()
                        .get(0)
                        .getVariants()
                        .getGif()
                        .getSource()
                        .getUrl());
                Log.d(TAG, "GIF URL: "+ mGifUri);
            }

            if (mGifUri != null) {
                isGif = true;
                DraweeController controller = Fresco.newDraweeControllerBuilder()
                        .setUri(mGifUri)
                        .setAutoPlayAnimations(true)
                        .build();
                imageView.setController(controller);
            } else {
                Uri uri = Uri.parse(post.getPreview().getImages().get(0).getSource().getUrl());
                imageView.setImageURI(uri);
            }
        }
    }

    private void setViewIcon(Data_ post) {
        if (post.getPostHint() != null) {
            if ((post.getPostHint().equals("link") || post.getPostHint().equals("rich:video")) && !post.isRedditMediaDomain()) {
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
}
