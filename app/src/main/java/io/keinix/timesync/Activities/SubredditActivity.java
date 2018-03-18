package io.keinix.timesync.Activities;

import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import org.w3c.dom.Text;

import java.text.DecimalFormat;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.keinix.timesync.R;
import io.keinix.timesync.adapters.FeedAdapter;
import io.keinix.timesync.reddit.model.SubReddit;

public class SubredditActivity extends AppCompatActivity {

    @BindView(R.id.subredditRecyclerView) RecyclerView mRecyclerView;
    @BindView(R.id.subredditBannerDraweeView) SimpleDraweeView subredditBannerView;
    @BindView(R.id.subredditIconDraweeView) SimpleDraweeView subredditIconDraweeView;
    @BindView(R.id.subredditNameTextView) TextView subredditNameTextView;
    @BindView(R.id.subscribeButton) Button subscribeButton;
    @BindView(R.id.subCountTextView) TextView subCountTextView;
    @BindView(R.id.subDescriptionTextView) TextView subDescriptionTextView;

    public static final String KEY_SUBREDDIT = "KEY_SUBREDDIT";

    private SubReddit mSubReddit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subreddit);
        ButterKnife.bind(this);
        mSubReddit = getIntent().getParcelableExtra(KEY_SUBREDDIT);
        bindSubredditView();
    }

    private void bindSubredditView() {
        if (mSubReddit.getBannerImage() != null) subredditBannerView.setImageURI(mSubReddit.getBannerImage());
        if (mSubReddit.getIconImg() !=null) {
            subredditIconDraweeView.setImageURI(mSubReddit.getIconImg());
            subredditIconDraweeView.getDrawable().clearColorFilter();
        }

        subredditNameTextView.setText(mSubReddit.getDisplayNamePrefixed());
        if (mSubReddit.isSubcriber()) {
            int subColor = ContextCompat.getColor(this, R.color.colorAccent);
            subscribeButton.setText("   Subscribed   ");
            subscribeButton.setTextColor(subColor);
        }
        DecimalFormat decimalFormat = new DecimalFormat("#,###,###");
        String formatedSubCount = decimalFormat.format(mSubReddit.getSubscriber());
        String subText = formatedSubCount + " Subscribers";
        subCountTextView.setText(subText);

        if (mSubReddit.getPublicDescription() != null) subDescriptionTextView.setText(mSubReddit.getPublicDescription());

    }


}
