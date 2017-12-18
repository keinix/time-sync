package io.keinix.timesync.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.keinix.timesync.R;


public class CommentsFragment extends Fragment {

    @BindView(R.id.textView) TextView mTextView;

    public static final String KEY_INDEX = "KEY_INDEX";

    public int mIndex;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_comments, container, false);
        mIndex = getArguments().getInt(KEY_INDEX);
        ButterKnife.bind(this, view);

        return view;
    }
}
