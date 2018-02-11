package io.keinix.timesync.reddit;

import android.accounts.AccountManager;
import android.content.Context;
import android.graphics.PorterDuff;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

import io.keinix.timesync.R;
import io.keinix.timesync.reddit.model.Data_;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class RedditVoteHelper {

    public static final String TAG = RedditVoteHelper.class.getSimpleName();
    public static final int VALUE_UPVOTED = 1;
    public static final int VALUE_DOWNVOTED = -1;
    public static final int VALUE_NOT_VOTED = 0;

    private Map<String, Integer> localVoteTracker;
    private ImageButton mUpVoteImageButton;
    private ImageButton mDownVoteColorImageButton;
    private TextView mVoteCountTextView;
    private String mId;
    private Context mContext;

    private int mUpVoteColor;
    private int mDownVoteColor;
    private int mDefaultCountTextColor;
    private int mColorWhite;

    public RedditVoteHelper(Context context, ImageButton upButton, ImageButton downButton, TextView voteCount, String id) {
        localVoteTracker= new HashMap<>();
        mDownVoteColorImageButton = downButton;
        mUpVoteImageButton = upButton;
        mVoteCountTextView = voteCount;
        mId = id;
    }

    public void setVoteColor() {
        int voteStatus = localVoteTracker.get(mId);

        mUpVoteImageButton.setOnClickListener(v -> {
        switch (voteStatus) {
            case VALUE_UPVOTED:
                mUpVoteImageButton.setColorFilter(mColorWhite, PorterDuff.Mode.MULTIPLY);
                mDownVoteColorImageButton.setColorFilter(mColorWhite, PorterDuff.Mode.MULTIPLY);
                mVoteCountTextView.setTextColor(mDefaultCountTextColor);
                //un-vote + set count
                break;
            case VALUE_DOWNVOTED:
                break;
            default:
                break;
        }});
    }

    public void bindColors() {
        mUpVoteColor = ContextCompat.getColor(mContext, R.color.upVoteColor);
        mDownVoteColor = ContextCompat.getColor(mContext, R.color.downVoteColor);
        mDefaultCountTextColor = ContextCompat.getColor(mContext, R.color.colorCountText);
        mColorWhite = ContextCompat.getColor(mContext, R.color.white);
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

}
