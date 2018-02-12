package io.keinix.timesync.reddit;

import android.content.Context;
import android.graphics.PorterDuff;
import android.support.v4.content.ContextCompat;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

import io.keinix.timesync.R;
import io.keinix.timesync.reddit.model.VoteResult;
import io.keinix.timesync.reddit.model.comment.Comment;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class RedditVoteHelper {

    public static final String TAG = RedditVoteHelper.class.getSimpleName();
    public static final int VALUE_UPVOTED = 1;
    public static final int VALUE_DOWNVOTED = -1;
    public static final int VALUE_NOT_VOTED = 0;
    public static final String UP_VOTE = "1";
    public static final String DOWN_VOTE = "-1";
    public static final String UN_VOTE = "0";


    private ImageButton mUpVoteImageButton;
    private ImageButton mDownVoteImageButton;
    private TextView mVoteCountTextView;
    private Context mContext;
    private String mId;
    private Api mApi;
    private int mVoteStatus;

    private int mUpVoteColor;
    private int mDownVoteColor;
    private int mDefaultCountTextColor;
    private int mColorWhite;

    private boolean setResults;

    public RedditVoteHelper(Context context, ImageButton upButton,
                            ImageButton downButton, TextView voteCount, Api api, int voteStatus, String id) {
        mDownVoteImageButton = downButton;
        mUpVoteImageButton = upButton;
        mVoteCountTextView = voteCount;
        mVoteStatus = voteStatus;
        mContext = context;
        mApi = api;
        mId = id;

        mUpVoteImageButton.getDrawable().mutate();
        mDownVoteImageButton.getDrawable().mutate();
        bindColors();
        setInitialVoteColors();
        setOnClick();
    }

    //default is triggered when not vote has been cast
    public void setOnClick() {
        mUpVoteImageButton.setOnClickListener(v -> {
        switch (mVoteStatus) {
            case VALUE_UPVOTED:
                mUpVoteImageButton.setColorFilter(mColorWhite, PorterDuff.Mode.MULTIPLY);
                mDownVoteImageButton.setColorFilter(mColorWhite, PorterDuff.Mode.MULTIPLY);
                mVoteCountTextView.setText(String.valueOf(Integer.parseInt(mVoteCountTextView.getText().toString()) - 1));
                mVoteCountTextView.setTextColor(mDefaultCountTextColor);
                vote(UN_VOTE);
                break;
            case VALUE_DOWNVOTED:
                mUpVoteImageButton.getDrawable().setColorFilter(mUpVoteColor, PorterDuff.Mode.MULTIPLY);
                mDownVoteImageButton.setColorFilter(mColorWhite, PorterDuff.Mode.MULTIPLY);
                mVoteCountTextView.setText(String.valueOf(Integer.parseInt(mVoteCountTextView.getText().toString()) + 2));
                mVoteCountTextView.setTextColor(mUpVoteColor);
                vote(UP_VOTE);
                break;
            default:
                mUpVoteImageButton.getDrawable().setColorFilter(mUpVoteColor, PorterDuff.Mode.MULTIPLY);
                mDownVoteImageButton.setColorFilter(mColorWhite, PorterDuff.Mode.MULTIPLY);
                mVoteCountTextView.setText(String.valueOf(Integer.parseInt(mVoteCountTextView.getText().toString()) + 1));
                mVoteCountTextView.setTextColor(mUpVoteColor);
                vote(UP_VOTE);
                break;
        }});
        mDownVoteImageButton.setOnClickListener(v -> {
        switch (mVoteStatus) {
            case VALUE_UPVOTED:
                mDownVoteImageButton.getDrawable().setColorFilter(mDownVoteColor, PorterDuff.Mode.MULTIPLY);
                mUpVoteImageButton.setColorFilter(mColorWhite, PorterDuff.Mode.MULTIPLY);
                mVoteCountTextView.setText(String.valueOf(Integer.parseInt(mVoteCountTextView.getText().toString()) - 2));
                mVoteCountTextView.setTextColor(mDownVoteColor);
                vote(DOWN_VOTE);
                break;
            case VALUE_DOWNVOTED:
                mUpVoteImageButton.setColorFilter(mColorWhite, PorterDuff.Mode.MULTIPLY);
                mDownVoteImageButton.setColorFilter(mColorWhite, PorterDuff.Mode.MULTIPLY);
                mVoteCountTextView.setText(String.valueOf(Integer.parseInt(mVoteCountTextView.getText().toString()) + 1));
                mVoteCountTextView.setTextColor(mDefaultCountTextColor);
                vote(DOWN_VOTE);
                break;
            default:
                mDownVoteImageButton.getDrawable().setColorFilter(mDownVoteColor, PorterDuff.Mode.MULTIPLY);
                mUpVoteImageButton.setColorFilter(mColorWhite, PorterDuff.Mode.MULTIPLY);
                mVoteCountTextView.setText(String.valueOf(Integer.parseInt(mVoteCountTextView.getText().toString()) - 1));
                mVoteCountTextView.setTextColor(mDownVoteColor);
                vote(DOWN_VOTE);
                break;
        }});
    }

    public void setInitialVoteColors() {

        switch (mVoteStatus) {
            case VALUE_UPVOTED:
                mUpVoteImageButton.getDrawable().setColorFilter(mUpVoteColor, PorterDuff.Mode.MULTIPLY);
                mDownVoteImageButton.setColorFilter(mColorWhite, PorterDuff.Mode.MULTIPLY);
                mVoteCountTextView.setTextColor(mUpVoteColor);
                break;
            case VALUE_DOWNVOTED:
                mDownVoteImageButton.getDrawable().setColorFilter(mDownVoteColor, PorterDuff.Mode.MULTIPLY);
                mUpVoteImageButton.setColorFilter(mColorWhite, PorterDuff.Mode.MULTIPLY);
                mVoteCountTextView.setTextColor(mDownVoteColor);
                break;
            default:
                mUpVoteImageButton.setColorFilter(mColorWhite, PorterDuff.Mode.MULTIPLY);
                mDownVoteImageButton.setColorFilter(mColorWhite, PorterDuff.Mode.MULTIPLY);
                mVoteCountTextView.setTextColor(mDefaultCountTextColor);
                break;
        }
    }

    public void vote(String voteType) {
        mVoteStatus = Integer.parseInt(voteType);
        mApi.vote(voteType, mId).enqueue(new Callback<VoteResult>() {
            @Override
            public void onResponse(Call<VoteResult> call, Response<VoteResult> response) {
                // Toast.makeText(mContext, "Voted!" + voteType, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<VoteResult> call, Throwable t) {
                Toast.makeText(mContext, "Vote Not Counted: network problem", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void bindColors() {
        mUpVoteColor = ContextCompat.getColor(mContext, R.color.upVoteColor);
        mDownVoteColor = ContextCompat.getColor(mContext, R.color.downVoteColor);
        mDefaultCountTextColor = ContextCompat.getColor(mContext, R.color.colorCountText);
        mColorWhite = ContextCompat.getColor(mContext, R.color.white);
    }

    public int getVoteStatus() {
        return mVoteStatus;
    }

    public void setVoteStatus(int voteStatus) {
        mVoteStatus = voteStatus;
    }

    public void setResults() {
        setResults = true;
    }

    public void launchResultCall() {

    }
}
