package io.keinix.timesync.Activities;

import android.graphics.PorterDuff;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.keinix.timesync.R;

public class PostActivity extends AppCompatActivity {

    @BindView(R.id.postTextImageButton) ImageButton textImageButton;
    @BindView(R.id.postLinkImageButton) ImageButton linkImageButton;
    @BindView(R.id.postPicImageButton) ImageButton picImageButton;
    @BindView(R.id.postLibraryImageButton) ImageButton libraryImageButton;
    @BindView(R.id.postBodyEditText) EditText bodyEditText;
    @BindView(R.id.postLibraryDescripTextView) TextView libraryDescripTextView;


    public static final String POST_TYPE_TEXT = "POST_TYPE_TEXT";
    public static final String POST_TYPE_LINK = "POST_TYPE_LINK";
    public static final String POST_TYPE_PIC = "POST_TYPE_PIC";

    private String postType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        ButterKnife.bind(this);
        postType = POST_TYPE_TEXT;
        setIconColor();
        setVisibility();
        setUpOnClick();
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
}
