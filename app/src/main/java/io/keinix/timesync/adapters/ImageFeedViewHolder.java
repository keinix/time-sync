package io.keinix.timesync.adapters;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;

import io.keinix.timesync.Fragments.FeedFragment;
import io.keinix.timesync.R;
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
            if (post.getPostHint().equals("link") &&
                    !post.isRedditMediaDomain() &&
                    post.getDomain().equals("i.imgur.com")) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(post.getUrl()));
                mFeedItemInterface.getContext().startActivity(intent);
            } else {
                Log.d(TAG, "onClick Called");
                showPopUp(post);
            }
        });
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

        setVoteOnClick(mIndex, post.getName(), post, mPopUpUpVoteImageButton, mPopUpDownVoteImageButton, mPopUpVoteCountTextView);
        setPoPUpVoteColor(post.getName());
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
            mPopUpDownVoteImageButton.setColorFilter(mColorWhite, PorterDuff.Mode.MULTIPLY);
            mPopUpDownVoteImageButton.setColorFilter(mColorWhite, PorterDuff.Mode.MULTIPLY);
            mPopUpVoteCountTextView.setTextColor(mDefaultCountTextColor);
        }
    }


    private void setPostImage(Data_ post, SimpleDraweeView imageView) {
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
                Log.d(TAG, "GIF URL: "+ gifUri);
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
