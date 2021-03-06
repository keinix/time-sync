package io.keinix.timesync.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.keinix.timesync.R;
import io.keinix.timesync.adapters.SubRedditAdapter;
import io.keinix.timesync.reddit.model.SubReddit;

public class SubredditNavigationFragment extends android.support.v4.app.Fragment {

    @BindView(R.id.subRedditRecyclerView) RecyclerView subRecyclerView;

    SubRedditAdapter mSubRedditAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sub_navigation, container, false);
        ButterKnife.bind(this, view);
        setUpRecyclerView();
        return view;
    }

    public void setUpRecyclerView() {
        mSubRedditAdapter = new SubRedditAdapter(getActivity());
        subRecyclerView.setAdapter(mSubRedditAdapter);
        subRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    public List<SubReddit> getSubreddits() { return mSubRedditAdapter.getSubList(); }
}
