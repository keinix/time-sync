package io.keinix.timesync.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.keinix.timesync.R;


public class MessagesFragment extends Fragment {

    public interface MessagesInterface {
        void tempLogin();
    }

    public static final String KEY_MESSAGE_TYPE = "VALUE_MESSAGE_TYPE";
    public static final String VALUE_MESSAGE_TYPE_MESSAGE = "MESSAGE_TYPE_MESSAGE";
    public static final String VALUE_MESSAGE_TYPE_NOTIFICATION = "MESSAGE_TYPE_NOTIFICATION";


    @BindView(R.id.tempLoginButton) Button tempLoginButton;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_messages, container, false);
        ButterKnife.bind(this, view);
        final MessagesInterface messagesInterface = (MessagesInterface) getActivity();

        tempLoginButton.setOnClickListener(v -> {
            messagesInterface.tempLogin();
        });
        return view;
    }
}
