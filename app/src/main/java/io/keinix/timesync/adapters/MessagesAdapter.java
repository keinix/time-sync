package io.keinix.timesync.adapters;

import android.content.ClipData;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.keinix.timesync.Fragments.MessagesFragment;
import io.keinix.timesync.R;
import io.keinix.timesync.reddit.ItemDetailsHelper;
import io.keinix.timesync.reddit.model.Message;

public class MessagesAdapter extends Adapter {
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


        public MessagesViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bindView(int position) {
            if (mIsNotification) {
                bindNotification(position);
            } else {
                bindMessage(position);
            }
        }

        public void bindNotification(int position) {
            Message message = mMessages.get(position);
            String capSubject = message.getSubject().substring(0,1).toUpperCase() +
                    message.getSubject().substring(1);
            String bottomText =  capSubject + " \u2022 " +
                    ItemDetailsHelper.getTimeWithUnit(message.getCreatedUtc());
            topTextView.setText(getNoticicationText(message));
            middleTextView.setText(message.getBody());
            bottomTextView.setText(bottomText);
        }

        public void bindMessage(int position) {
            Message message = mMessages.get(position);
            String bottomText = message.getAuthor() + " \u2022 " +
                    ItemDetailsHelper.getTimeWithUnit(message.getCreatedUtc());
            topTextView.setText(message.getSubject());
            middleTextView.setText(message.getBody());
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

        }

    }
}
