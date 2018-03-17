package io.keinix.timesync.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.keinix.timesync.R;
import io.keinix.timesync.reddit.model.SubReddit;

public class SubredditActivity extends AppCompatActivity {

    @BindView(R.id.subredditBannerDraweeView) SimpleDraweeView subredditBannerView;
    @BindView(R.id.subredditIconDraweeView) SimpleDraweeView subredditIconDraweeView;
    @BindView(R.id.subredditNameTextView) TextView subredditNameTextView;
    @BindView(R.id.subscribeButton) Button subscribeButton;
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
        if (mSubReddit.
    }

}
