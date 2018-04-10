package io.keinix.timesync.Activities;

import android.graphics.PorterDuff;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.JsonElement;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.keinix.timesync.R;
import io.keinix.timesync.reddit.Api;
import io.keinix.timesync.reddit.ApiHelper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostActivity extends AppCompatActivity {

    @BindView(R.id.postTextImageButton) ImageButton textImageButton;
    @BindView(R.id.postLinkImageButton) ImageButton linkImageButton;
    @BindView(R.id.postPicImageButton) ImageButton picImageButton;
    @BindView(R.id.postLibraryImageButton) ImageButton libraryImageButton;
    @BindView(R.id.postBodyEditText) EditText bodyEditText;
    @BindView(R.id.postTitleEditText) EditText postTitleEditText;
    @BindView(R.id.postLibraryDescripTextView) TextView libraryDescripTextView;
    @BindView(R.id.subredditSpinner) Spinner subredditSpinner;

    public static final String TAG = PostActivity.class.getSimpleName();
    public static final String POST_TYPE_TEXT = "POST_TYPE_TEXT";
    public static final String POST_TYPE_LINK = "POST_TYPE_LINK";
    public static final String POST_TYPE_PIC = "POST_TYPE_PIC";
    public static final String KEY_SUB_LIST = "KEY_SUB_LIST";

    private Api mApi;
    private String postType;
    private ArrayList<String> mSubNames;
    private String selectedSubreddit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        ButterKnife.bind(this);
        mSubNames = getIntent().getStringArrayListExtra(KEY_SUB_LIST);
        postType = POST_TYPE_TEXT;
        mApi = ApiHelper.initApi(this);
        setIconColor();
        setVisibility();
        setUpOnClick();
        setUpSpinner();
    }

    public void setUpOnClick() {
        textImageButton.setOnClickListener(v -> {
            postType = POST_TYPE_TEXT;
            setIconColor();
            setVisibility();
        });

        linkImageButton.setOnClickListener(v -> {
            postType = POST_TYPE_LINK;
            setIconColor();
            setVisibility();
        });
        picImageButton.setOnClickListener(view -> {
            postType = POST_TYPE_PIC;
            setIconColor();
            setVisibility();
        });
    }

    private void setIconColor() {
        int accentColor = ContextCompat.getColor(this, R.color.colorAccent);

        switch (postType) {
            case POST_TYPE_TEXT:
                textImageButton.getDrawable().setColorFilter(accentColor, PorterDuff.Mode.MULTIPLY);
                linkImageButton.getDrawable().clearColorFilter();
                picImageButton.getDrawable().clearColorFilter();
                break;
            case POST_TYPE_LINK:
                linkImageButton.getDrawable().setColorFilter(accentColor, PorterDuff.Mode.MULTIPLY);
                textImageButton.getDrawable().clearColorFilter();
                picImageButton.getDrawable().clearColorFilter();
                break;
            case POST_TYPE_PIC:
                picImageButton.getDrawable().setColorFilter(accentColor, PorterDuff.Mode.MULTIPLY);
                textImageButton.getDrawable().clearColorFilter();
                linkImageButton.getDrawable().clearColorFilter();
        }
    }

    public void setVisibility() {
        switch (postType) {
            case POST_TYPE_TEXT:
                bodyEditText.setVisibility(View.VISIBLE);
                libraryImageButton.setVisibility(View.INVISIBLE);
                libraryDescripTextView.setVisibility(View.INVISIBLE);
                break;
            case POST_TYPE_LINK:
                bodyEditText.setVisibility(View.VISIBLE);
                libraryImageButton.setVisibility(View.INVISIBLE);
                libraryDescripTextView.setVisibility(View.INVISIBLE);
                break;
            case POST_TYPE_PIC:
                bodyEditText.setVisibility(View.INVISIBLE);
                libraryImageButton.setVisibility(View.VISIBLE);
                libraryDescripTextView.setVisibility(View.VISIBLE);
        }
    }

    public void setUpSpinner() {
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(this, R.layout.spinner_item, mSubNames);
        subredditSpinner.setAdapter(adapter);

        subredditSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
                int accentColor = ContextCompat.getColor(PostActivity.this, R.color.colorAccent);
                selectedSubreddit = adapterView.getItemAtPosition(pos).toString();
                ((TextView) adapterView.getChildAt(0)).setTextColor(accentColor);
                ((TextView) adapterView.getChildAt(0)).setTextSize(20);
                adapterView.getChildAt(0).setPadding(10, 16,0,0);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.post_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.postMenuButton:
            submitPost();
        }
        return super.onOptionsItemSelected(item);
    }

    private void submitPost() {
       String kind = null;
       String subReddit = selectedSubreddit.substring(1);
       String title = postTitleEditText.getText().toString();
       String text = null;
       String url = null;

       switch (postType) {
           case POST_TYPE_TEXT:
               kind = "self";
               text = bodyEditText.getText().toString();
               break;
           case POST_TYPE_LINK:
               kind = "link";
               url = bodyEditText.getText().toString();
               break;
       }

       mApi.post(kind, subReddit, title, text, url).enqueue(new Callback<JsonElement>() {
           @Override
           public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
               if (response.isSuccessful()) {
                   Log.d(TAG, "Response was successful");
                   Log.d(TAG, "response: " +  response);
               Log.d(TAG, "response body: " +  response.body());
               } else {
                   Log.d(TAG, "Response was NOT successful");
                   Log.d(TAG, "response: " +  response);
                   Log.d(TAG, "response body: " +  response.body());
               }
           }

           @Override
           public void onFailure(Call<JsonElement> call, Throwable t) {
               Log.d(TAG, "onFail Called from Post");
           }
       });
    }
}
