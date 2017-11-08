package io.keinix.timesync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    private static final String REDDIT_AUTH_URL =
            "https://www.reddit.com/api/v1/authorize.compact?client_id=%s" +
                    "&response_type=code&state=%s&redirect_uri=%s&" +
                    "duration=permanent&scope=identity";
    public static final String REDDIT_CLIENT_ID = "gX4PnW7oHz7dgQ";

    public static final String REDDIT_REDIRECT_URL = "https://www.keinix.io/timesync";

    //TODO: check if this needs to be changed to actual random string
    public static final String REDDIT_STATE = "RANDOM_STRING";

    public static final String REDDIT_ACCESS_TOKEN = "https://www.reddit.com/api/v1/access_token";

    public static final String REDDIT_URL = String.format(REDDIT_AUTH_URL, REDDIT_CLIENT_ID, REDDIT_STATE, REDDIT_REDIRECT_URL);


    @BindView(R.id.redditButton) Button redditSignInButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);


        redditSignInButton.setOnClickListener(v -> attemptAccountManager());
    }

    public void attemptAccountManager() {
        AccountManager am = AccountManager.get(this);

        am.addAccount(AccountConstants.ACCOUNT_TYPE_REDDIT,
                "perminat",
                null,
                null,
                null,
                null,
                null);
    }

    public void startRedditSignIn() {
//        String url = String.format(REDDIT_AUTH_URL, REDDIT_CLIENT_ID, REDDIT_STATE, REDDIT_REDIRECT_URL);
//        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
//        startActivity(intent);
        AccountManager am = AccountManager.get(this);
        Account[] accounts = am.getAccountsByType("https://www.reddit.com/");

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
