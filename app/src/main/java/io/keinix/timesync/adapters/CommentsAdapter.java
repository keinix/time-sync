package io.keinix.timesync.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import butterknife.BindView;
import io.keinix.timesync.Fragments.CommentsFragment;
import io.keinix.timesync.R;
import io.keinix.timesync.reddit.model.comment.CommentBase;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommentsAdapter extends RecyclerView.Adapter {

    private CommentsFragment.CommentsInterface mCommentsInterface;

    public CommentsAdapter(CommentsFragment.CommentsInterface commentsInterface) {
        mCommentsInterface = commentsInterface;
        populateComments();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    private void populateComments() {

           mCommentsInterface.getComments().enqueue(new Callback<CommentBase>() {
               @Override
               public void onResponse(Call<CommentBase> call, Response<CommentBase> response) {

               }

               @Override
               public void onFailure(Call<CommentBase> call, Throwable t) {

               }
           });
    }

    public class CommentsViewHolder {

        @BindView(R.id.commentDetailsTextView) TextView detailsTextView;
        @BindView(R.id.commentTextTextView) TextView TextTextView;
        @BindView(R.id.commentReplyImageButton) ImageButton ReplyImageButton;
        @BindView(R.id.commentUpVoteImageButton) ImageButton upVoteImageButton;
        @BindView(R.id.commentUpCount) TextView upCountTextView;
        @BindView(R.id.commentDownVoteImageButton) ImageButton downVoteImageButton;



        public void bindView(int position) {

        }
    }
}
