package io.keinix.timesync.adapters;

import android.content.ClipData;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.JsonElement;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.keinix.timesync.Fragments.CommentsFragment;
import io.keinix.timesync.R;
import io.keinix.timesync.reddit.ItemDetailsHelper;
import io.keinix.timesync.reddit.RedditVoteHelper;
import io.keinix.timesync.reddit.model.comment.Comment;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.noties.markwon.Markwon;

public class CommentsAdapter extends RecyclerView.Adapter {

    private static final String TAG = CommentsAdapter.class.getSimpleName();

    private CommentsFragment.CommentsInterface mCommentsInterface;
    private List<Comment> mCommentTree;
    private ProgressBar mCommentsProgressBar;

    public CommentsAdapter(CommentsFragment.CommentsInterface commentsInterface, ProgressBar progressBar) {
        mCommentsInterface = commentsInterface;
        mCommentTree = new ArrayList<>();
        mCommentsProgressBar = progressBar;
        populateComments();
        Log.d(TAG, "CommentAdapter created");
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View commentItemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_item, parent, false);
        return new CommentsViewHolder(commentItemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((CommentsViewHolder) holder).bindView(position);
    }

    @Override
    public int getItemCount() {
        return mCommentTree.size();
    }

    private void populateComments() {

       mCommentsInterface.getComments().enqueue(new Callback<JsonElement>() {
           @Override
           public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                Log.d(TAG, "Call: " + call);
                Log.d(TAG, "response: " + response);

                if (response.isSuccessful()) {
                    Log.d(TAG, "Responce: " + response.body().toString());
                    JsonElement baseCommentElement = response.body();
                    mCommentTree = mCommentsInterface.createCommentTree(baseCommentElement);
                    notifyDataSetChanged();
                    mCommentsProgressBar.setVisibility(View.GONE);
                    Log.d(TAG, "Comment Tree Length: " + mCommentTree.size());
                }
           }

           @Override
           public void onFailure(Call<JsonElement> call, Throwable t) {
                Log.d(TAG, "OnFailure called for getComments");
                Log.d(TAG, "Call: " + call.request());
                Log.d(TAG, t.toString());
           }
       });
    }

    public class CommentsViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.commentDetailsTextView) TextView detailsTextView;
        @BindView(R.id.commentTextTextView) TextView textTextView;
        @BindView(R.id.commentReplyImageButton) ImageButton replyImageButton;
        @BindView(R.id.commentUpVoteImageButton) ImageButton upVoteImageButton;
        @BindView(R.id.commentUpCount) TextView upCountTextView;
        @BindView(R.id.commentDownVoteImageButton) ImageButton downVoteImageButton;
        @BindView(R.id.commentCardView) CardView baseConstraintLayout;
        private int mPostion;

        public CommentsViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        //TODO:add html and hyperlink/subreddit parsing
        public void bindView(int position) {
            mPostion = position;
            Comment comment = mCommentTree.get(position);

            detailsTextView.setText(ItemDetailsHelper.getUserDetails(comment.getAuthor(), comment.getCreatedUtc()));
            textTextView.setText(comment.getBody());
            upCountTextView.setText(String.valueOf(comment.getScore()));
            setCommentTreeMargins(baseConstraintLayout, comment);

             new RedditVoteHelper(mCommentsInterface.getContext(),
                    upVoteImageButton, downVoteImageButton, upCountTextView,
                    mCommentsInterface.getApi(), ItemDetailsHelper.parseVoteType(comment.getLikes()),
                     comment.getName());
        }

        public void setCommentTreeMargins(CardView item, Comment comment) {
            CardView.LayoutParams layoutParams = new CardView.LayoutParams(
                    CardView.LayoutParams.MATCH_PARENT, CardView.LayoutParams.WRAP_CONTENT);
            int topMargin = 10;
            if (mPostion >= 1) {topMargin = comment.getDepth() == 0 ? 10 : 0;}
            layoutParams.setMargins(Math.max(comment.getDepth() * 20, 6), topMargin, 6, 0);
            item.setLayoutParams(layoutParams);
        }
    }
}
