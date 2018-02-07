package io.keinix.timesync.Fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.keinix.timesync.Activities.CommentsActivity;
import io.keinix.timesync.R;
import io.keinix.timesync.adapters.CommentsAdapter;
import io.keinix.timesync.reddit.model.comment.Comment;
import io.keinix.timesync.reddit.model.comment.CommentBase;
import retrofit2.Call;



public class CommentsFragment extends Fragment {

    public interface CommentsInterface {
        Call<JsonArray> getComments();
        List<Comment> parseComments(JsonObject json);
    }

    @Nullable @BindView(R.id.postDraweeView) SimpleDraweeView mPostDraweeView;
    @BindView(R.id.commentsPostInfo) TextView mCommentsPostDetails;
    @BindView(R.id.commentsPostTitle) TextView mCommentsPostTitle;
    @BindView(R.id.commentsSubRedditName) TextView mCommentsSubreddit;
    @BindView(R.id.commentsRecyclerView) RecyclerView mCommentsRecyclerView;

    public static final String KEY_INDEX = "KEY_INDEX";

    private CommentsInterface mCommentsInterface;
    private String mPostLayoutType;
    private String mPostTitle;
    private String mPostDetails;
    private String mPostID;
    private String mPostSubreddit;
    private String mPostArticle;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_comments_image, container, false);
        ButterKnife.bind(this, view);
        unPackIntent();
        mCommentsInterface = (CommentsInterface) getActivity();
        mPostLayoutType = getActivity().getIntent().getStringExtra(CommentsActivity.KEY_COMMENTS_LAYOUT_TYPE);
        setRecyclerView();

        switch (mPostLayoutType) {
            case CommentsActivity.VALUE_IMAGE_COMMENTS_LAYOUT:
                bindCommentsView();
                setBasicImage();
                break;
            case CommentsActivity.VALUE_GIF_COMMENTS_LAYOUT:
                bindCommentsView();
                setGifImage();
                break;
            case CommentsActivity.VALUE_TEXT_COMMENTS_LAYOUT:
                break;
            case CommentsActivity.VALUE_VIDEO_COMMENTS_LAYOUT:
                break;
        }
        return view;
    }

    private void unPackIntent() {
        Intent intent = getActivity().getIntent();
        mPostTitle = getActivity().getIntent().getStringExtra(CommentsActivity.KEY_POST_TITLE);
        mPostDetails = intent.getStringExtra(CommentsActivity.KEY_POST_DETAILS);
        mPostID = intent.getStringExtra(CommentsActivity.KEY_POST_ID);
        mPostSubreddit = intent.getStringExtra(CommentsActivity.KEY_POST_SUBREDDIT);
        mPostArticle = intent.getStringExtra(CommentsActivity.KEY_POST_ARTICLE);
    }

    private void bindCommentsView() {
        mCommentsPostDetails.setText(mPostDetails);
        mCommentsPostTitle.setText(mPostTitle);
        mCommentsSubreddit.setText(mPostSubreddit);
    }

    private void setRecyclerView() {
        mCommentsRecyclerView.setAdapter(new CommentsAdapter(mCommentsInterface));
        mCommentsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    private void setGifImage() {
        Uri gifUri = Uri.parse(getActivity().getIntent().getStringExtra(CommentsActivity.KEY_IMAGE_URL));
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setUri(gifUri)
                .setAutoPlayAnimations(true)
                .build();
        mPostDraweeView.setController(controller);
    }

    private void setBasicImage() {
        Uri imageUri = Uri.parse(getActivity().getIntent().getStringExtra(CommentsActivity.KEY_IMAGE_URL));
        mPostDraweeView.setImageURI(imageUri);
    }



}
