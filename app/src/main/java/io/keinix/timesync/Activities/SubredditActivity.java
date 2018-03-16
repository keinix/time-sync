package io.keinix.timesync.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import io.keinix.timesync.R;

public class SubredditActivity extends AppCompatActivity {

    public static final String KEY_SUBREDDIT = "KEY_SUBREDDIT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subreddit);
    }

}
