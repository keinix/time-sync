package io.keinix.timesync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.keinix.timesync.Activities.AddAccountActivity;

public class MainActivity extends AppCompatActivity {

   @BindView(R.id.redditButton) Button redditSignInButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        redditSignInButton.setOnClickListener(v -> launchLogin());


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
}
