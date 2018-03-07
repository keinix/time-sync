package io.keinix.timesync.adapters;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import io.keinix.timesync.R;
import io.keinix.timesync.reddit.model.SubReddit;

public class SubRedditAdapter extends Adapter {
    private List<SubReddit> mSubReddits;

    public SubRedditAdapter() {
        mSubReddits = new ArrayList<>();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.sub_reddit_item, parent, false);
        return new SubRedditViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((SubRedditViewHolder) holder).bindView(position);
    }

    @Override
    public int getItemCount() {
        return mSubReddits.size();
    }

    public class SubRedditViewHolder extends RecyclerView.ViewHolder {

        public SubRedditViewHolder(View itemView) {
            super(itemView);
        }

        public void bindView(int position) {

        }
    }
}
