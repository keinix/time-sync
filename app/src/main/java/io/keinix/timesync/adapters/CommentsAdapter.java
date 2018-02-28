package io.keinix.timesync.adapters;

import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonElement;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.keinix.timesync.Activities.ReplyActivity;
import io.keinix.timesync.Fragments.CommentsFragment;
import io.keinix.timesync.R;
import io.keinix.timesync.reddit.ItemDetailsHelper;
import io.keinix.timesync.reddit.RedditVoteHelper;
import io.keinix.timesync.reddit.model.comment.Comment;
import io.keinix.timesync.utils.CopyUtil;
import io.keinix.timesync.utils.ShareUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommentsAdapter extends RecyclerView.Adapter {

    private static final String TAG = CommentsAdapter.class.getSimpleName();

    private CommentsFragment.CommentsInterface mCommentsInterface;
    private List<Comment> mCommentTree;
    private ProgressBar mCommentsProgressBar;
    private CommentsFragment mFragment;

    public CommentsAdapter(CommentsFragment.CommentsInterface commentsInterface, ProgressBar progressBar, CommentsFragment fragment) {
        mCommentsInterface = commentsInterface;
        mCommentTree = new ArrayList<>();
        mCommentsProgressBar = progressBar;
        mFragment = fragment;
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

    public void insertReply(int position, Comment reply) {
        mCommentTree.add(position, reply);
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


    public class CommentsViewHolder extends RecyclerView.ViewHolder implements PopupMenu.OnMenuItemClickListener {

        @BindView(R.id.commentDetailsTextView) TextView detailsTextView;
        @BindView(R.id.commentTextTextView) TextView textTextView;
        @BindView(R.id.commentReplyImageButton) ImageButton replyImageButton;
        @BindView(R.id.commentUpVoteImageButton) ImageButton upVoteImageButton;
        @BindView(R.id.commentUpCount) TextView upCountTextView;
        @BindView(R.id.commentDownVoteImageButton) ImageButton downVoteImageButton;
        @BindView(R.id.commentCardView) CardView baseConstraintLayout;
        @BindView(R.id.commentMenuImageButton) ImageButton menuImageButton;
        private int mPostion;
        private boolean commentSaved;
        private Comment mComment;

        public CommentsViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        //TODO:add html and hyperlink/subreddit parsing
        public void bindView(int position) {
            mPostion = position;
            mComment = mCommentTree.get(position);
            commentSaved = mComment.isSaved();
            menuImageButton.setOnClickListener(v -> showPopUpMenu(menuImageButton));
            replyImageButton.setOnClickListener(v -> launchReplyActivity());

            mCommentsInterface.setMarkDownText(textTextView, mComment.getBody());
            detailsTextView.setText(ItemDetailsHelper.getUserDetails(mComment.getAuthor(), mComment.getCreatedUtc()));
            upCountTextView.setText(String.valueOf(mComment.getScore()));
            setCommentTreeMargins(baseConstraintLayout, mComment);

            //change to red when usernames match
//            int red = ContextCompat.getColor(mCommentsInterface.getContext(), R.color.red);
//            int accent = ContextCompat.getColor(mCommentsInterface.getContext(), R.color.colorAccent);
//            if (mComment.isSubmitter()) {
//                detailsTextView.setTextColor(red);
//            } else {
//                detailsTextView.setTextColor(accent);
//            }

             new RedditVoteHelper(mCommentsInterface.getContext(),
                    upVoteImageButton, downVoteImageButton, upCountTextView,
                    mCommentsInterface.getApi(), ItemDetailsHelper.parseVoteType(mComment.getLikes()),
                     mComment.getName());
        }

        public void setCommentTreeMargins(CardView item, Comment comment) {
            CardView.LayoutParams layoutParams = new CardView.LayoutParams(
                    CardView.LayoutParams.MATCH_PARENT, CardView.LayoutParams.WRAP_CONTENT);
            int topMargin = 10;
            if (mPostion >= 1) {topMargin = comment.getDepth() == 0 ? 10 : 0;}
            layoutParams.setMargins(Math.max(comment.getDepth() * 20, 6), topMargin, 6, 0);
            item.setLayoutParams(layoutParams);
        }

        public void showPopUpMenu(View v) {
            PopupMenu popUp = new PopupMenu(mCommentsInterface.getContext(), v);
            MenuInflater menuInflater = popUp.getMenuInflater();
            popUp.setOnMenuItemClickListener(this);
            menuInflater.inflate(R.menu.comment_menu, popUp.getMenu());
            MenuItem saveItem = popUp.getMenu().findItem(R.id.save);
             if (commentSaved) {
                 saveItem.setTitle("Unsave");
             } else {
                 saveItem.setTitle("Save");
             }
            popUp.show();
        }

        public void launchReplyActivity() {
            Intent intent = new Intent(mCommentsInterface.getContext(), ReplyActivity.class);
            intent.putExtra(ReplyActivity.KEY_AUTHOR, mComment.getAuthor());
            intent.putExtra(ReplyActivity.KEY_BODY, mComment.getBody());
            intent.putExtra(ReplyActivity.KEY_CREATED_UTC, mComment.getCreatedUtc());
            intent.putExtra(ReplyActivity.KEY_POSITION, getAdapterPosition());
            intent.putExtra(ReplyActivity.KEY_DEPTH, mComment.getDepth());
            intent.putExtra(ReplyActivity.KEY_PARENT_ID, mComment.getName());
            mFragment.startActivityForResult(intent, ReplyActivity.REQUEST_CODE);
        }


        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.save:
                    if (commentSaved) {
                        commentSaved = false;
                        mCommentsInterface.unsave(mComment.getName());
                        Toast.makeText(mCommentsInterface.getContext(), "Unsaved", Toast.LENGTH_SHORT).show();
                    } else {
                        commentSaved = true;
                        mCommentsInterface.save(mComment.getName());
                        Toast.makeText(mCommentsInterface.getContext(), "Saved", Toast.LENGTH_SHORT).show();
                    }
                    return true;
                case R.id.share:
                    String shareText = "u/" +mComment.getAuthor() +
                            ": " + mComment.getBody() + "\n\nwww.reddit.com" + mComment.getPermalink();
                    ShareUtil.shareText(mCommentsInterface.getContext(), shareText);
                    return true;
                case R.id.copy:
                    Toast.makeText(mCommentsInterface.getContext(), "Text copied", Toast.LENGTH_SHORT).show();
                    CopyUtil.copy(mCommentsInterface.getContext(), mComment.getBody());
                    return true;
                case R.id.report:
                    Toast.makeText(mCommentsInterface.getContext(), "Reported", Toast.LENGTH_SHORT).show();
                    return true;
                default:
                    return false;
            }

        }


    }
}
