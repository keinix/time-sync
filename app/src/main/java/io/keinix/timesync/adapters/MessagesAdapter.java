package io.keinix.timesync.adapters;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.keinix.timesync.Activities.CommentsActivity;
import io.keinix.timesync.Activities.ReplyActivity;
import io.keinix.timesync.Fragments.MessagesFragment;
import io.keinix.timesync.R;
import io.keinix.timesync.reddit.Api;
import io.keinix.timesync.reddit.ApiHelper;
import io.keinix.timesync.reddit.ItemDetailsHelper;
import io.keinix.timesync.reddit.model.Data_;
import io.keinix.timesync.reddit.model.Message;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static io.keinix.timesync.reddit.ItemDetailsHelper.getTimeSincePosted;

public class MessagesAdapter extends Adapter {

    private static final int VIEW_ITEM_TYPE_IMAGE = 100;
    private static final int VIEW_ITEM_TYPE_TEXT = 200;
    private static final int  VIEW_ITEM_TYPE_VIDEO =  300;

    public static final String TAG = MessagesAdapter.class.getSimpleName();
    private MessagesFragment.MessagesInterface mMessagesInterface;
    private List<Message> mMessages;
    private boolean mIsNotification;
    private ProgressBar mProgressBar;

    public MessagesAdapter(MessagesFragment.MessagesInterface messagesInterface, boolean isNotification, ProgressBar progressBar) {
        mMessages = new ArrayList<>();
        mIsNotification = isNotification;
        mMessagesInterface = messagesInterface;
        mProgressBar = progressBar;
        mMessagesInterface.getMessages(this, mIsNotification);
        mProgressBar.setVisibility(View.GONE);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mMessagesInterface.getContext()).inflate(R.layout.message_item, parent, false);
        return new MessagesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((MessagesViewHolder) holder).bindView(position);
    }

    @Override
    public int getItemCount() {
        return mMessages.size();
    }


    public void setMessages(List<Message> messages) {
        mMessages = messages;
    }

    public class MessagesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private String commentReplyNotification = "$user$ replied to your comment in $subreddit$";
        private String postReplyNotification = "$user$ replied to you post in $subreddit$";
        private String userNameMentionNotification = "$user$ mentioned you in $subreddit$";

        @BindView(R.id.messageTopTextView) TextView topTextView;
        @BindView(R.id.messageMiddleTextView) TextView middleTextView;
        @BindView(R.id.messageBottomTextView) TextView bottomTextView;

        private Message mMessage;


        public MessagesViewHolder(View itemView)  {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        public void bindView(int position) {
            if (mIsNotification) {
                bindNotification(position);
            } else {
                bindMessage(position);
            }
        }

        public void bindNotification(int position) {
            mMessage = mMessages.get(position);
            String capSubject = mMessage.getSubject().substring(0,1).toUpperCase() +
                    mMessage.getSubject().substring(1);
            String bottomText =  capSubject + " \u2022 " +
                    ItemDetailsHelper.getTimeWithUnit(mMessage.getCreatedUtc());
            topTextView.setText(getNoticicationText(mMessage));
            middleTextView.setText(mMessage.getBody());
            bottomTextView.setText(bottomText);
        }

        public void bindMessage(int position) {
            mMessage = mMessages.get(position);
            String bottomText = mMessage.getAuthor() + " \u2022 " +
                    ItemDetailsHelper.getTimeWithUnit(mMessage.getCreatedUtc());
            topTextView.setText(mMessage.getSubject());
            middleTextView.setText(mMessage.getBody());
            bottomTextView.setText(bottomText);
        }

        private String getNoticicationText(Message message) {
            String subjectText;
            switch (message.getSubject()) {
                case "comment reply":
                    subjectText = commentReplyNotification;
                    break;
                case "post reply":
                    subjectText = postReplyNotification;
                    break;
                default: subjectText = userNameMentionNotification;
            }
            subjectText = subjectText.replace("$user$",  "u/" + message.getAuthor());
            subjectText = subjectText.replace("$subreddit$", message.getSubbreditNamePrefixed());
            return subjectText;
        }

        @Override
        public void onClick(View view) {
            Log.d(TAG, "onClick");
            if (mIsNotification) {
                notificationOnClick();
            } else {
                messageOnClick();
            }
        }

        private void messageOnClick() {
            Log.d(TAG, "messageOnClick");
            Intent intent = new Intent(mMessagesInterface.getContext(), ReplyActivity.class);
            intent.putExtra(ReplyActivity.KEY_MESSAGE_TYPE, true);
            intent.putExtra(ReplyActivity.KEY_PARENT_ID, mMessage.getName());
            intent.putExtra(ReplyActivity.KEY_BODY, mMessage.getBody());
            intent.putExtra(ReplyActivity.KEY_AUTHOR, mMessage.getAuthor());
            intent.putExtra(ReplyActivity.KEY_CREATED_UTC, mMessage.getCreatedUtc());
            mMessagesInterface.getContext().startActivity(intent);
        }

        private void notificationOnClick() {
            String contextUrl = mMessage.getContextUrl().split("\\?")[0];
            getParentPost(contextUrl);
        }

    }

    public void getParentPost(String contextUrl) {
        Log.d(TAG, "contextUrl: " + contextUrl);
        Api api = ApiHelper.initApi(mMessagesInterface.getContext());
        api.getPostfromMessage(contextUrl).enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                Log.d(TAG, "response: " + response);
                Log.d(TAG, "response body: " + response.body());
                Gson gson = new Gson();
                JsonObject json = response.body().getAsJsonArray().get(0)
                        .getAsJsonObject().getAsJsonObject("data")
                        .getAsJsonArray("children").get(0)
                        .getAsJsonObject().getAsJsonObject("data");
                Log.d(TAG, "pre-Return post: " + gson.fromJson(json, Data_.class));
                Data_ post = gson.fromJson(json, Data_.class);
                launchCommentsActivity(post);
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {

            }
        });

    }

    public void launchCommentsActivity(Data_ post) {
        int viewType = checkViewType(post);
        Intent intent = new Intent(mMessagesInterface.getContext(), CommentsActivity.class);
        switch (viewType) {
            case VIEW_ITEM_TYPE_IMAGE:
                attachImageExtras(post, intent);
                break;
            case VIEW_ITEM_TYPE_TEXT:
                attachTextExtras(post, intent);
                break;
            case VIEW_ITEM_TYPE_VIDEO:
                attachVideoExtras(post, intent);
        }
        mMessagesInterface.getContext().startActivity(intent);
    }

    public int checkViewType(Data_ post) {
        if ((post.isSelf()) && post.getPreview() == null) {
            return VIEW_ITEM_TYPE_TEXT;
        } else if (post.getDomain().equals("v.redd.it")) {
            return VIEW_ITEM_TYPE_VIDEO;
        }
        return VIEW_ITEM_TYPE_IMAGE;
    }

    private void attachTextExtras(Data_ post, Intent intent) {
        long timeSincePosted = getTimeSincePosted(post.getCreatedUtc());
        String postDetails = "u/" +post.getAuthor() + " \u2022 "
                + timeSincePosted + "h";
        intent.putExtra(CommentsActivity.KEY_COMMENTS_LAYOUT_TYPE, CommentsActivity.VALUE_TEXT_COMMENTS_LAYOUT);
        intent.putExtra(CommentsActivity.KEY_POST_SUBREDDIT, post.getSubredditNamePrefixed());
        intent.putExtra(CommentsActivity.KEY_POST_SUBREDDIT_NO_PREFIX, post.getSubreddit());
        intent.putExtra(CommentsActivity.KEY_POST_TITLE, post.getTitle());
        intent.putExtra(CommentsActivity.KEY_POST_ID, post.getName());
        intent.putExtra(CommentsActivity.KEY_POST_DETAILS, postDetails);
        intent.putExtra(CommentsActivity.KEY_POST_ARTICLE, post.getId());
        intent.putExtra(CommentsActivity.KEY_VOTE_TYPE, ItemDetailsHelper.parseVoteType(post.isLiked()));
        intent.putExtra(CommentsActivity.KEY_SELF_TEXT, post.getSelfText());
        intent.putExtra(CommentsActivity.KEY_VOTE_COUNT, post.getUps());
        intent.putExtra(CommentsActivity.KEY_ORIGINAL_POST_POSITION, 0);
        intent.putExtra(CommentsActivity.KEY_INIT_VOTE_TYPE, ItemDetailsHelper.parseVoteType(post.isLiked()));
    }

    public void attachImageExtras(Data_ post, Intent intent) {

        Uri imageUri;
        boolean isGif = false;

        if (post.getPreview().getImages().get(0).getVariants().getGif() != null) {
            imageUri = Uri.parse(post.getPreview()
                    .getImages()
                    .get(0)
                    .getVariants()
                    .getGif()
                    .getSource()
                    .getUrl());
            isGif = true;
            Log.d(TAG, "GIF URL: "+ imageUri);
        } else {
            imageUri = Uri.parse(post.getPreview().getImages().get(0).getSource().getUrl().replace("amp;", ""));
        }

        if (isGif) {
            intent.putExtra(CommentsActivity.KEY_IMAGE_URL, imageUri.toString());
        } else {
            Log.d(TAG, "NOT GIF TRIGGERED");
            intent.putExtra(CommentsActivity.KEY_IMAGE_URL, post.getPreview().getImages().get(0).getSource().getUrl().replace("amp;", ""));
        }
        if (isGif) {
            intent.putExtra(CommentsActivity.KEY_COMMENTS_LAYOUT_TYPE, CommentsActivity.VALUE_GIF_COMMENTS_LAYOUT);
        } else {
            intent.putExtra(CommentsActivity.KEY_COMMENTS_LAYOUT_TYPE, CommentsActivity.VALUE_IMAGE_COMMENTS_LAYOUT);
        }

        intent.putExtra(CommentsActivity.KEY_INIT_VOTE_TYPE, ItemDetailsHelper.getUserDetails(post.getAuthor(), post.getCreatedUtc()));
        intent.putExtra(CommentsActivity.KEY_POST_SUBREDDIT, post.getSubredditNamePrefixed());
        intent.putExtra(CommentsActivity.KEY_VOTE_TYPE, ItemDetailsHelper.parseVoteType(post.isLiked()));
        intent.putExtra(CommentsActivity.KEY_POST_SUBREDDIT_NO_PREFIX, post.getSubreddit());
        intent.putExtra(CommentsActivity.KEY_POST_TITLE, post.getTitle());
        intent.putExtra(CommentsActivity.KEY_POST_ID, post.getName());
        intent.putExtra(CommentsActivity.KEY_POST_DETAILS, ItemDetailsHelper.getUserDetails(post.getAuthor(), post.getCreatedUtc()));
        intent.putExtra(CommentsActivity.KEY_POST_ARTICLE, post.getId());
        intent.putExtra(CommentsActivity.KEY_VOTE_COUNT, post.getUps());
        intent.putExtra(CommentsActivity.KEY_ORIGINAL_POST_POSITION, 0);
    }

    private void attachVideoExtras(Data_ post, Intent intent) {
        long timeSincePosted = getTimeSincePosted(post.getCreatedUtc());
        String postDetails = "u/" +post.getAuthor() + " \u2022 "
                + timeSincePosted + "h";

        Uri videoUri;

        if (post.getCrossLinks() != null) {
            videoUri = Uri.parse(post.getCrossLinks().get(0).getMedia().getRedditVideo().getScrubberMediaUrl());
        } else {
            videoUri = Uri.parse(post.getMedia().getRedditVideo().getScrubberMediaUrl());
        }

        intent.putExtra(CommentsActivity.KEY_COMMENTS_LAYOUT_TYPE, CommentsActivity.VALUE_VIDEO_COMMENTS_LAYOUT);
        intent.putExtra(CommentsActivity.KEY_VIDEO_URI, videoUri.toString());
        intent.putExtra(CommentsActivity.KEY_POST_SUBREDDIT, post.getSubredditNamePrefixed());
        intent.putExtra(CommentsActivity.KEY_POST_SUBREDDIT_NO_PREFIX, post.getSubreddit());
        intent.putExtra(CommentsActivity.KEY_POST_TITLE, post.getTitle());
        intent.putExtra(CommentsActivity.KEY_POST_ID, post.getId());
        intent.putExtra(CommentsActivity.KEY_POST_DETAILS, postDetails);
        intent.putExtra(CommentsActivity.KEY_VOTE_TYPE, ItemDetailsHelper.parseVoteType(post.isLiked()));
        intent.putExtra(CommentsActivity.KEY_POST_ARTICLE, post.getName());
        intent.putExtra(CommentsActivity.KEY_VOTE_COUNT, post.getUps());
        intent.putExtra(CommentsActivity.KEY_INIT_VOTE_TYPE, ItemDetailsHelper.parseVoteType(post.isLiked()));
        intent.putExtra(CommentsActivity.KEY_ORIGINAL_POST_POSITION, 0);
    }
}
