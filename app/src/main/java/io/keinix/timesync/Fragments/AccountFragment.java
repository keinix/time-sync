package io.keinix.timesync.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import butterknife.BindView;
import io.keinix.timesync.Activities.AddAccountActivity;
import io.keinix.timesync.R;


public class AccountFragment extends Fragment {

    @BindView(R.id.tempLoginButton)
    Button tempLoginButton;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        tempLoginButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AddAccountActivity.class);
            getActivity().startActivity(intent);
        });
        return view;
    }
}
