package io.keinix.timesync.Activities;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;

import java.time.LocalDate;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.keinix.timesync.R;
import io.keinix.timesync.reddit.Api;
import io.keinix.timesync.reddit.ApiHelper;
import io.keinix.timesync.reddit.ItemDetailsHelper;
import io.keinix.timesync.utils.MarkDownParser;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReplyActivity extends AppCompatActivity {

    public static final String TAG = ReplyActivity.class.getSimpleName();

    @BindView(R.id.replyBodyTextView) TextView bodyTextView;
    @BindView(R.id.replyAuthorTextView) TextView authorTextView;
    @BindView(R.id.replyEditText) EditText replyEditText;

    public static final String KEY_AUTHOR = "KEY_AUTHOR";
    public static final String KEY_BODY = "KEY_BODY";
    public static final String KEY_REPLY_BODY = "KEY_REPLY_BODY ";
    public static final String KEY_CREATED_UTC = "KEY_CREATED_UTC";
    public static final String KEY_POSITION = "KEY_POSITION";
    public static final String KEY_DEPTH = "KEY_DEPTH";
    public static final String KEY_IS_REPLY_TO_OP = "KEY_IS_REPLY_TO_OP";
    public static final String KEY_PARENT_ID = "KEY_PARENT_ID";
    public static final int REQUEST_CODE = 102;

    // reply to a message
    public static final String KEY_MESSAGE_TYPE = "KEY_MESSAGE_TYPE";

    private String mAuthor;
    private String mBody;
    private String mParentId;
    private long mCreatedUtc;
    private int mPosition;
    private int mReplyDepth;
    private boolean mIsReplyToOp;
    private boolean mIsMessageReply;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reply);
        getSupportActionBar().setHomeButtonEnabled(true);
        ButterKnife.bind(this);
        // submitted form MessagesAdapter
        mIsMessageReply = getIntent().getBooleanExtra(KEY_MESSAGE_TYPE, false);
        if (mIsMessageReply) {
            unPackIntentFromMessage();
        } else {
            unPackIntent();
        }
        bindView();
    }

    private void unPackIntentFromMessage() {
        Intent intent = getIntent();
        mAuthor = intent.getStringExtra(KEY_AUTHOR);
        mBody = intent.getStringExtra(KEY_BODY);
        mCreatedUtc = intent.getLongExtra(KEY_CREATED_UTC, 0);
        mParentId = intent.getStringExtra(KEY_PARENT_ID);
        mIsReplyToOp = intent.getBooleanExtra(KEY_IS_REPLY_TO_OP, false);
    }

    public void unPackIntent() {
        Intent intent = getIntent();
        mAuthor = intent.getStringExtra(KEY_AUTHOR);
        mBody = intent.getStringExtra(KEY_BODY);
        mCreatedUtc = intent.getLongExtra(KEY_CREATED_UTC, 0);
        mPosition = intent.getIntExtra(KEY_POSITION, 0);
        mParentId = intent.getStringExtra(KEY_PARENT_ID);
        mIsReplyToOp = intent.getBooleanExtra(KEY_IS_REPLY_TO_OP, false);
        int depth =intent.getIntExtra(KEY_DEPTH, 0);
        mReplyDepth = mIsReplyToOp ? 0 : depth + 1;
    }

    public void bindView() {
        MarkDownParser.parse(this, bodyTextView, mBody);
        if (mIsReplyToOp) {
            authorTextView.setText(mAuthor);
        } else {
            authorTextView.setText(ItemDetailsHelper.getReplyDetails(mAuthor, mCreatedUtc));
        }
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
                ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

                if (cm.getActiveNetwork() == null) {
                    Toast.makeText(this, "No Network Connection", Toast.LENGTH_SHORT).show();
                } else {
                    if (mIsMessageReply) {
                        replyToMessage();
                    } else {
                        replyToAComment();
                    }
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void replyToAComment() {
        Intent intent = new Intent();
        intent.putExtra(KEY_REPLY_BODY, replyEditText.getText().toString());
        intent.putExtra(KEY_POSITION, mPosition);
        intent.putExtra(KEY_DEPTH, mReplyDepth);
        intent.putExtra(KEY_PARENT_ID, mParentId);
        intent.putExtra(KEY_IS_REPLY_TO_OP, mIsReplyToOp);
        setResult(RESULT_OK, intent);
        finish();
    }

    public void replyToMessage() {
        Api api = ApiHelper.initApi(this);
        api.comment(mParentId, replyEditText.getText().toString()).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Toast.makeText(ReplyActivity.this, "replied", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Response: " + response);
                Log.d(TAG, "Response body : " + response.body());

                finish();
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(ReplyActivity.this, "there was a problem :(", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
