package io.keinix.timesync;

import android.accounts.AccountManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import butterknife.ButterKnife;
import io.keinix.timesync.Activities.AddAccountActivity;
import io.keinix.timesync.Fragments.CommentsFragment;
import io.keinix.timesync.Fragments.FeedFragment;
import io.keinix.timesync.Fragments.ViewPagerFragment;

public class MainActivity extends AppCompatActivity implements FeedFragment.FeedItemInterface {

   // @BindView(R.id.redditButton) Button redditSignInButton;
    public static final String TAG_FEED_FRAGMENT = "TAG_FEED_FRAGMENT";
    public static final String TAG_VIEW_PAGER_FRAGMENT = "TAG_VIEW_PAGER_FRAGMENT";
    public static final String TAG_COMMENTS_FRAGMENT = "TAG_COMMENTS_FRAGMENT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        // redditSignInButton.setOnClickListener(v -> launchLogin());

        ViewPagerFragment savedFragment = (ViewPagerFragment) getSupportFragmentManager()
                .findFragmentByTag(TAG_VIEW_PAGER_FRAGMENT);
        if (savedFragment == null) {
            ViewPagerFragment viewPagerFragment = new ViewPagerFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(R.id.placeHolder, viewPagerFragment, TAG_VIEW_PAGER_FRAGMENT);
            fragmentTransaction.commit();
        }

    }

    private void launchLogin() {
        Intent intent = new Intent(this, AddAccountActivity.class);
        startActivity(intent);
    }


    public void startRedditSignIn() {
//        String url = String.format(REDDIT_AUTH_URL, REDDIT_CLIENT_ID, REDDIT_STATE, REDDIT_REDIRECT_URL);
//        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
//        startActivity(intent);
    }

    public void redditApiSignIn() {
        String scope = "identity edit flair history mysubreddits privatemessages read report save submit subscribe vote";
        String authUrl = "https://www.reddit.com/api/v1/authorize?client_id=CLIENT_ID&response_type=TYPE&" +
                "state=RANDOM_STRING&redirect_uri=URI&duration=DURATION&scope=SCOPE_STRING";
        String clientId = "gX4PnW7oHz7dgQ";
        String responceType = "code";
        String state = "some random string";
        String redirectUrl = "https://www.keinix.io/timesync";

        AccountManager am = AccountManager.get(this);

    }

    @Override
    public void voteUp(int index) {

    }

    @Override
    public void voteDown(int index) {

    }

    @Override
    public void share(int index) {

    }

    @Override
    public void launchCommentFragment(int index) {
        CommentsFragment commentsFragment = new CommentsFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(CommentsFragment.KEY_INDEX, index);
        commentsFragment.setArguments(bundle);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.placeHolder, commentsFragment, TAG_COMMENTS_FRAGMENT);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}
