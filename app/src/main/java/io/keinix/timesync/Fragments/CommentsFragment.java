package io.keinix.timesync.Fragments;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.gson.JsonElement;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import im.ene.toro.exoplayer.SimpleExoPlayerViewHelper;
import io.keinix.timesync.Activities.CommentsActivity;
import io.keinix.timesync.Activities.ReplyActivity;
import io.keinix.timesync.R;
import io.keinix.timesync.adapters.CommentsAdapter;
import io.keinix.timesync.reddit.Api;
import io.keinix.timesync.reddit.RedditVoteHelper;
import io.keinix.timesync.reddit.model.comment.Comment;
import io.keinix.timesync.views.WrapContentDraweeView;
import retrofit2.Call;

import static android.support.v4.provider.FontsContractCompat.FontRequestCallback.RESULT_OK;


public class CommentsFragment extends Fragment {

    public interface CommentsInterface {
        Call<JsonElement> getComments();
        List<Comment> createCommentTree(JsonElement baseCommentElement);
        CommentsActivity getContext();
        Api getApi();
        void setMarkDownText(TextView textView, String text);
        void save(String id);
        void unsave(String id);
        Comment generateReplyComment(Intent data);
    }

    @Nullable @BindView(R.id.postExoPlayer) SimpleExoPlayerView mExoPlayer;
    @Nullable @BindView(R.id.postDraweeView) WrapContentDraweeView mPostDraweeView;
    @Nullable @BindView(R.id.commentsTextPostTitle) TextView mCommentsTextPostTitle;
    @Nullable @BindView(R.id.commentText) TextView mCommentText;
    @Nullable @BindView(R.id.commentsPostTitle) TextView mCommentsPostTitle;
    @Nullable @BindView(R.id.postGifDraweeView) SimpleDraweeView mPostGifDraweeView;

    @BindView(R.id.commentsNestScrollView) NestedScrollView mCommentsNextScrollView;
    @BindView(R.id.commentPostVoteCount) TextView mVoteCountTextView;
    @BindView(R.id.commentPostUpVote) ImageButton mUpVoteImageButton;
    @BindView(R.id.commentPostDownVote) ImageButton mDownVoteImageButton;
    @BindView(R.id.commentsPostInfo) TextView mCommentsPostDetails;
    @BindView(R.id.commentsSubRedditName) TextView mCommentsSubreddit;
    @BindView(R.id.commentsRecyclerView) RecyclerView mCommentsRecyclerView;
    @BindView(R.id.commentsProgressBar) ProgressBar mcommentsProgressBar;
    public static final String KEY_INDEX = "KEY_INDEX";
    public static final String TAG = CommentsFragment.class.getSimpleName();

    protected CommentsInterface mCommentsInterface;
    protected SimpleExoPlayerViewHelper mExoPlayerViewHelper;
    protected RedditVoteHelper mRedditVoteHelper;
    protected String mPostLayoutType;
    protected int mVoteStatus;
    protected int mVoteCount;
    protected String mPostTitle;
    protected String mPostDetails;
    protected String mPostID;
    protected String mPostSubreddit;
    protected String mSelfText;
    protected Uri mVideoUri;
    protected View mView;
    protected CommentsAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mPostLayoutType = getActivity().getIntent().getStringExtra(CommentsActivity.KEY_COMMENTS_LAYOUT_TYPE);
        mView = setCommentsView(inflater, container);
        ButterKnife.bind(this, mView);
        unPackIntent();
        mCommentsInterface = (CommentsInterface) getActivity();
        setRecyclerView();
        populateLayout();
        mVoteCountTextView.setText(String.valueOf(mVoteCount));
        mRedditVoteHelper = new RedditVoteHelper(mCommentsInterface.getContext(), mUpVoteImageButton, mDownVoteImageButton,
                mVoteCountTextView, mCommentsInterface.getApi(), mVoteStatus, mPostID);
        return mView;
    }

    private void populateLayout() {
        switch (mPostLayoutType) {
            case CommentsActivity.VALUE_IMAGE_COMMENTS_LAYOUT:
                bindCommentsView();
                setBasicImage();
                break;
            case CommentsActivity.VALUE_GIF_COMMENTS_LAYOUT:
                Log.d(TAG, "Is gif triggered in switch");
                bindCommentsView();
                setGifImage();
                break;
            case CommentsActivity.VALUE_TEXT_COMMENTS_LAYOUT:
                bindTextCommentsView();
                break;
            case CommentsActivity.VALUE_VIDEO_COMMENTS_LAYOUT:
                bindCommentsView();
                setVideo();
                break;
        }
    }

    protected View setCommentsView(LayoutInflater inflater, ViewGroup container) {
        switch (mPostLayoutType) {
            case CommentsActivity.VALUE_TEXT_COMMENTS_LAYOUT:
                return inflater.inflate(R.layout.fragment_comments_text, container, false);
            case CommentsActivity.VALUE_GIF_COMMENTS_LAYOUT:
                return inflater.inflate(R.layout.fragment_comments_gif, container, false);
            case CommentsActivity.VALUE_IMAGE_COMMENTS_LAYOUT:
                return inflater.inflate(R.layout.fragment_comments_image, container, false);
            case CommentsActivity.VALUE_VIDEO_COMMENTS_LAYOUT:
                return inflater.inflate(R.layout.fragment_comments_video, container, false);
            default:
                return inflater.inflate(R.layout.fragment_comments_image, container, false);
        }
    }

    protected void unPackIntent() {
        Intent intent = getActivity().getIntent();
        mPostTitle = getActivity().getIntent().getStringExtra(CommentsActivity.KEY_POST_TITLE);
        mPostDetails = intent.getStringExtra(CommentsActivity.KEY_POST_DETAILS);
        mPostID = intent.getStringExtra(CommentsActivity.KEY_POST_ID);
        mPostSubreddit = intent.getStringExtra(CommentsActivity.KEY_POST_SUBREDDIT);
        mVoteStatus = intent.getIntExtra(CommentsActivity.KEY_INIT_VOTE_TYPE, 0);
        mVoteCount = intent.getIntExtra(CommentsActivity.KEY_VOTE_COUNT, 0);

        if (intent.getStringExtra(CommentsActivity.KEY_VIDEO_URI) != null) {
            mVideoUri = Uri.parse(intent.getStringExtra(CommentsActivity.KEY_VIDEO_URI));
        }
        if (intent.getStringExtra(CommentsActivity.KEY_SELF_TEXT) != null) {
            mSelfText = intent.getStringExtra(CommentsActivity.KEY_SELF_TEXT);
        }
    }

    protected void bindCommentsView() {
        mCommentsPostDetails.setText(mPostDetails);
        mCommentsPostTitle.setText(mPostTitle);
        mCommentsSubreddit.setText(mPostSubreddit);
    }

    private void bindTextCommentsView() {
        mCommentsTextPostTitle.setText(mPostTitle);
        mCommentsInterface.setMarkDownText(mCommentText, mSelfText);
        mCommentsSubreddit.setText(mPostSubreddit);
        mCommentsPostDetails.setText(mPostDetails);
    }

    protected void setRecyclerView() {
        mAdapter = new CommentsAdapter(mCommentsInterface, mcommentsProgressBar, this);
        mCommentsRecyclerView.setAdapter(mAdapter);
        mCommentsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }


    protected void setVideo() {

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == -1 && requestCode == ReplyActivity.REQUEST_CODE) {
            postReply(data);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void postReply(Intent data) {
        int position = data.getIntExtra(ReplyActivity.KEY_POSITION, mAdapter.getItemCount());
        //int replyPosition = position++;
        Comment reply = mCommentsInterface.generateReplyComment(data);
        mAdapter.insertReply(position, reply);
        mAdapter.notifyItemInserted(position);
    }

    private void setGifImage() {
        Uri gifUri = Uri.parse(getActivity().getIntent().getStringExtra(CommentsActivity.KEY_IMAGE_URL));
        Log.d(TAG, "gif URI in setGifImage: " + gifUri);
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setUri(gifUri)
                .setAutoPlayAnimations(true)
                .build();
        mPostGifDraweeView.setController(controller);
    }

    private void setBasicImage() {
        Uri imageUri = Uri.parse(getActivity().getIntent().getStringExtra(CommentsActivity.KEY_IMAGE_URL));
        mPostDraweeView.setImageURI(imageUri);
    }

    public RedditVoteHelper getRedditVoteHelper() {
        return mRedditVoteHelper;
    }


}
