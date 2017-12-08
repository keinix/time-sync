package io.keinix.timesync.Activities;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.keinix.timesync.R;
import io.keinix.timesync.reddit.Constants;

public class AddAccountActivity extends AppCompatActivity {

    private boolean isAddingRedditAccount = false;

    @BindView(R.id.redditLoginButton) Button mRedditLoginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_account);
        ButterKnife.bind(this);

        mRedditLoginButton.setOnClickListener(v -> {
            redditLogin();
        });
    }

    private void redditLogin() {
        isAddingRedditAccount = true;
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.REDDIT_URL));
        startActivity(intent);
    }

    private void redditConsentCallback() {
        isAddingRedditAccount = false;

        if(getIntent()!= null && Intent.ACTION_VIEW.equals(getIntent().getAction())) {
            Uri uri = getIntent().getData();
            if(uri.getQueryParameter("error") != null) {
                String error = uri.getQueryParameter("error");
                Log.e("findme", "An error has occurred : " + error);
            } else {
                String state = uri.getQueryParameter("state");
                if(state.equals(Constants.REDDIT_STATE)) {
                    String code = uri.getQueryParameter("code");
                    Toast.makeText(this, "we did it", Toast.LENGTH_SHORT).show();
                    Log.d("findme", "REDDIT COD: " + code);
                }
            }
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (isAddingRedditAccount) {
            redditConsentCallback();
        }
    }
}
