package io.keinix.timesync.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.keinix.timesync.R;
import io.keinix.timesync.reddit.ItemDetailsHelper;
import io.keinix.timesync.utils.MarkDownParser;

public class ReplyActivity extends AppCompatActivity {

    @BindView(R.id.replyBodyTextView) TextView bodyTextView;
    @BindView(R.id.replyAuthorTextView) TextView authorTextView;
    @BindView(R.id.replyEditText) EditText replyEditText;

    public static final String KEY_AUTHOR = "KEY_AUTHOR";
    public static final String KEY_BODY = "KEY_BODY";
    public static final String KEY_REPLY_BODY = "KEY_REPLY_BODY ";
    public static final String KEY_CREATED_UTC = "KEY_CREATED_UTC";
    public static final int REQUEST_CODE = 102;

    private String mAuthor;
    private String mBody;
    private long mCreatedUtc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reply);
        getSupportActionBar().setHomeButtonEnabled(true);
        ButterKnife.bind(this);
        unPackIntent();
        bindView();
    }

    public void unPackIntent() {
        Intent intent = getIntent();
        mAuthor = intent.getStringExtra(KEY_AUTHOR);
        mBody = intent.getStringExtra(KEY_BODY);
        mCreatedUtc = intent.getLongExtra(KEY_CREATED_UTC, 0);
    }

    public void bindView() {
        MarkDownParser.parse(this, bodyTextView, mBody);
        authorTextView.setText(ItemDetailsHelper.getReplyDetails(mAuthor, mCreatedUtc));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.reply_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.replyMenuPost:
                Intent intent = new Intent();
                intent.putExtra(KEY_REPLY_BODY, replyEditText.getText().toString());
                setResult(RESULT_OK, intent);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}