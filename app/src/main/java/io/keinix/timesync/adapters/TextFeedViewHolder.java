package io.keinix.timesync.adapters;

import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import io.keinix.timesync.Fragments.FeedFragment;
import io.keinix.timesync.R;
import io.keinix.timesync.reddit.model.Data_;

public class TextFeedViewHolder extends BaseFeedViewHolder {

    @Nullable @BindView(R.id.selfTextTextView) TextView selfTextView;

    public TextFeedViewHolder(View itemView, FeedAdapter adapter, FeedFragment.FeedItemInterface feedItemInterface) {
        super(itemView, adapter, feedItemInterface);
    }

    @Override
    public void bindView(int position) {
        super.bindView(position);
        Data_ post = mAdapter.getRedditFeed().getData().getChildren().get(position).getData();
        selfTextView.setText(post.getSelfText());
    }
}
