package io.keinix.timesync.Activities;

import android.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import io.keinix.timesync.Fragments.CommentsFragment;
import io.keinix.timesync.R;


public class CommentsActivity extends AppCompatActivity {

    public static final String TAG_COMMENTS_FRAGMENT = "TAG_COMMENTS_FRAGMENT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        CommentsFragment savedFragment = (CommentsFragment) getSupportFragmentManager().findFragmentByTag(TAG_COMMENTS_FRAGMENT);

        if (savedFragment == null) {
            CommentsFragment commentsFragment = new CommentsFragment();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.add(R.id.commentsPlaceHolder, commentsFragment, TAG_COMMENTS_FRAGMENT);
            fragmentTransaction.commit();
        }
    }
}
