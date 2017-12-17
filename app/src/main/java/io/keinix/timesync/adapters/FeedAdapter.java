package io.keinix.timesync.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.zip.Inflater;

import io.keinix.timesync.Fragments.FeedFragment;
import io.keinix.timesync.Fragments.FeedFragment.FeedItemInterface;
import io.keinix.timesync.R;

public class FeedAdapter extends RecyclerView.Adapter {
    FeedItemInterface mFeedItemInterface;

    public FeedAdapter(FeedItemInterface feedItemInterface) {
        mFeedItemInterface = feedItemInterface;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.feed_item, parent, false);
        return new FeedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((FeedViewHolder) holder).bindView(position);
    }

    @Override
    public int getItemCount() {
        //TODO: find some way to solve this
        return 0;
    }

    private class FeedViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private int mIndex;

        public FeedViewHolder(View itemView) {
            super(itemView);
        }

        public void bindView(int position) {
            mIndex = position;
        }

        @Override
        public void onClick(View v) {
            //TODO:implemept methods from feed itemInterface
        }
    }
}
