package io.keinix.timesync.adapters;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import io.keinix.timesync.Fragments.CommentsFragment;
import io.keinix.timesync.R;
import io.keinix.timesync.reddit.model.comment.Comment;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommentsAdapter extends RecyclerView.Adapter {

    private static final String TAG = CommentsAdapter.class.getSimpleName();

    private CommentsFragment.CommentsInterface mCommentsInterface;
    private List<Comment> mCommentTree;

    public CommentsAdapter(CommentsFragment.CommentsInterface commentsInterface) {
        mCommentsInterface = commentsInterface;
        mCommentTree = new ArrayList<>();
        populateComments();
        Log.d(TAG, "CommentAdapter created");
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

           mCommentsInterface.getComments().enqueue(new Callback<JsonArray>() {
               @Override
               public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                    Log.d(TAG, "Call: " + call);
                    Log.d(TAG, "response: " + response);

                    if (response.isSuccessful()) {
                        Log.d(TAG, "Responce: " + response.body().toString());
                        JsonArray baseCommentArray = response.body();
                        createCommentTree(baseCommentArray);
                        Log.d(TAG, "Comment Tree Length: " + mCommentTree.size());
                    }
               }

               @Override
               public void onFailure(Call<JsonArray> call, Throwable t) {
                    Log.d(TAG, "OnFailure called for getComments");
                    Log.d(TAG, "Call: " + call.request());
                    Log.d(TAG, t.toString());
               }
           });
    }

    public void createCommentTree(JsonArray jsonArray) {
        JsonArray commentArray = jsonArray
                .get(1)
                .getAsJsonObject()
                .getAsJsonObject("data")
                .getAsJsonArray("children");

        for (JsonElement comment : commentArray) {
           mCommentTree.addAll(mCommentsInterface.parseComments(comment.getAsJsonObject()));
        }
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
