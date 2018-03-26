package io.keinix.timesync.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import im.ene.toro.widget.Container;
import io.keinix.timesync.R;
import io.keinix.timesync.adapters.MessagesAdapter;
import io.keinix.timesync.reddit.model.Message;


public class MessagesFragment extends Fragment {

    public interface MessagesInterface {
        void tempLogin();
        void getMessages(MessagesAdapter adapter, boolean isNotification);
        Context getContext();
    }

    public static final String KEY_MESSAGE_TYPE = "VALUE_MESSAGE_TYPE";
    public static final String VALUE_MESSAGE_TYPE_MESSAGE = "MESSAGE_TYPE_MESSAGE";
    public static final String VALUE_MESSAGE_TYPE_NOTIFICATION = "MESSAGE_TYPE_NOTIFICATION";
    public boolean isNotification;

    @BindView(R.id.feedRecyclerView) Container mRecyclerView;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_feed, container, false);
        ButterKnife.bind(this, view);
        final MessagesInterface messagesInterface = (MessagesInterface) getActivity();
        determineFragmentType();
        mRecyclerView.setAdapter(new MessagesAdapter(messagesInterface, isNotification));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        return view;
    }

    private void determineFragmentType() {
        if (getArguments().getString(KEY_MESSAGE_TYPE).equals(VALUE_MESSAGE_TYPE_NOTIFICATION)) {
            isNotification = true;
        } else {
            isNotification = false;
        }
    }
}
