package io.keinix.timesync.Activities;

import android.accounts.AccountManager;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import java.text.DecimalFormat;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.keinix.timesync.Fragments.FeedFragment;
import io.keinix.timesync.R;
import io.keinix.timesync.adapters.FeedAdapter;
import io.keinix.timesync.reddit.Api;
import io.keinix.timesync.reddit.RedditAuthInterceptor;
import io.keinix.timesync.reddit.RedditConstants;
import io.keinix.timesync.reddit.TokenAuthenticator;
import io.keinix.timesync.reddit.model.RedditFeed;
import io.keinix.timesync.reddit.model.SubReddit;
import io.keinix.timesync.reddit.model.VoteResult;
import io.keinix.timesync.utils.MarkDownParser;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SubredditActivity extends AppCompatActivity implements FeedFragment.FeedItemInterface {

    @BindView(R.id.subredditRecyclerView) RecyclerView mRecyclerView;
    @BindView(R.id.subredditBannerDraweeView) SimpleDraweeView subredditBannerView;
    @BindView(R.id.subredditIconDraweeView) SimpleDraweeView subredditIconDraweeView;
    @BindView(R.id.subredditNameTextView) TextView subredditNameTextView;
    @BindView(R.id.subscribeButton) Button subscribeButton;
    @BindView(R.id.subCountTextView) TextView subCountTextView;
    @BindView(R.id.subDescriptionTextView) TextView subDescriptionTextView;
    @BindView(R.id.subredditProgressBar) ProgressBar mProgressBar;
    @BindView(R.id.infoImageButton) ImageButton infoImageButton;
    @BindView(R.id.subredditNestedScrollView) NestedScrollView mNestedScrollView;


    public static final String KEY_SUBREDDIT = "KEY_SUBREDDIT";
    public static final String TAG = SubredditActivity.class.getSimpleName();

    private SubReddit mSubReddit;
    private Api mApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subreddit);
        ButterKnife.bind(this);
        mSubReddit = getIntent().getParcelableExtra(KEY_SUBREDDIT);
        bindSubredditView();
        initApi();
        prepareRecyclerView();
        setTitle(mSubReddit.getDisplayNamePrefixed());
        setUpSubButton();
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

        infoImageButton.setOnClickListener(v -> launchPopUP(bindPopUp()));

    }

    public void prepareRecyclerView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        FeedAdapter feedAdapter = new FeedAdapter(this, linearLayoutManager, mProgressBar, mNestedScrollView);
        mRecyclerView.setAdapter(feedAdapter);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        populateRedditFeed(feedAdapter);
    }

    public void initApi() {
        AccountManager am = AccountManager.get(this);
        OkHttpClient.Builder client = new OkHttpClient.Builder()
                .authenticator(new TokenAuthenticator(am))
                .addInterceptor(new RedditAuthInterceptor(am, this));

        mApi = new Retrofit.Builder()
                .baseUrl(RedditConstants.REDDIT_BASE_URL_OAUTH2)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client.build())
                .build()
                .create(Api.class);
    }

    public void launchPopUP(View view) {
        PopupWindow popupWindow = new PopupWindow(view,
                WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        popupWindow.showAsDropDown(view, 0, 0);
    }

    public View bindPopUp() {
        View view = LayoutInflater.from(this).inflate(R.layout.pop_up_details, null);
        TextView name = view.findViewById(R.id.popUpSubredditName);
        TextView details = view.findViewById(R.id.popUpDetailsTextView);

        name.setText(mSubReddit.getDisplayNamePrefixed());
        MarkDownParser.parse(this, details, mSubReddit.getDescription());
        return view;
    }

    private void setUpSubButton() {
        int white = ContextCompat.getColor(this, R.color.white);
        int accent = ContextCompat.getColor(this, R.color.colorAccent);
        subscribeButton.setOnClickListener(v -> {
            if (mSubReddit.isSubcriber()) {
                subscribeButton.setTextColor(white);
                subscribeButton.setText("   Subscribe   ");
                mSubReddit.setSubcriber(false);
            } else {
                subscribeButton.setText("   Subscribed   ");
                subscribeButton.setTextColor(accent);
                mSubReddit.setSubcriber(true);
            }
        });
    }


     // Interface methods //

    @Override
    public Call<VoteResult> vote(String id, String voteType) {
        return mApi.vote(voteType, id);
    }


    //TODO: put the sub Api call here and some boolean to trigger a different response
    @Override
    public void populateRedditFeed(FeedAdapter adapter) {
        Log.d(TAG, "REFRESH TRIGGERED");
        mApi.getSubRedditFeed(mSubReddit.getDisplayNamePrefixed()).enqueue(adapter);
    }

    @Override
    public Call<RedditFeed> appendFeed(String after) {
        return mApi.appendSubredditFeed(mSubReddit.getDisplayNamePrefixed(), after);
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public Api getApi() {
        return mApi;
    }

    @Override
    public int getCommentsResult() {
        return 0;
    }

    @Override
    public int getPostInitVoteType() {
        return 0;
    }

    @Override
    public int getOriginalPostPosition() {
        return 0;
    }
}
