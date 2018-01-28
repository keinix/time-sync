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
import io.keinix.timesync.Activities.CommentsActivity;
import io.keinix.timesync.R;


public class CommentsFragment extends Fragment {

    @BindView(R.id.commentsSubRedditName) TextView mTextView;

    public static final String KEY_INDEX = "KEY_INDEX";



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_comments_image, container, false);
        ButterKnife.bind(this, view);
        mTextView.setText(getActivity().getIntent().getStringExtra(CommentsActivity.KEY_COMMENTS_VIEW_TYPE));
        return view;
    }
}
