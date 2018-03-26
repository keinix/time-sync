package io.keinix.timesync.adapters;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.keinix.timesync.Fragments.MessagesFragment;
import io.keinix.timesync.R;
import io.keinix.timesync.reddit.model.Message;

public class MessagesAdapter extends Adapter {
    private MessagesFragment.MessagesInterface mMessagesInterface;
    private List<Message> mMessages;
    private boolean mIsNotification;

    public MessagesAdapter(MessagesFragment.MessagesInterface messagesInterface, boolean isNotification) {
        mMessages = new ArrayList<>();
        mIsNotification = isNotification;
        mMessagesInterface = messagesInterface;
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

    public class MessagesViewHolder extends RecyclerView.ViewHolder{

        private String commentReplyNotification = "$user$ replied to your comment in $subreddit$";
        private String postReplyNotification = "$user$ replied to you post in $subreddit$";
        private String userNameMentionNotification = "$user$ mentioned you in $subreddit$";

        @BindView(R.id.messagesTextView) TextView topTextView;
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
            topTextView.setText(message.);
        }

        public void bindMessage(int position) {
            Message message = mMessages.get(position);
        }
    }
}
