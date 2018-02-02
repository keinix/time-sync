package io.keinix.timesync.reddit;

import android.accounts.AccountManager;
import android.content.Context;
import android.graphics.PorterDuff;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import io.keinix.timesync.reddit.model.Data_;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;



public class RedditVoteHelper {

//    private Api mApi;
//    private ImageButton mUPImageButton;
//    private ImageButton mDownImageButton;
//    private TextView mTextView;
//    private AccountManager mAccountManager;
//    private Context mContext;
//
//    public RedditVoteHelper(Context context) {
//        mContext = context;
//        mAccountManager = AccountManager.get(mContext);
//        initApi();
//    }
//
//    public void initApi() {
//        OkHttpClient.Builder client = new OkHttpClient.Builder()
//                .authenticator(new TokenAuthenticator(mAccountManager))
//                .addInterceptor(new RedditAuthInterceptor(mAccountManager, mContext));
//
//        mApi = new Retrofit.Builder()
//                .baseUrl(RedditConstants.REDDIT_BASE_URL_OAUTH2)
//                .addConverterFactory(GsonConverterFactory.create())
//                .client(client.build())
//                .build()
//                .create(Api.class);
//    }
//
//    public void initColors() {
//    }
//}
//
//    public void setVoteOnClick(int position, String id, Data_ post,
//                               ImageView upVoteImageButton, ImageView downVoteImageButton, TextView upVoteCountTextView) {
//        upVoteImageButton.setOnClickListener(v -> {
//            Log.d(TAG, "ID: " + id +  ": " + mAdapter.mLocalVoteTracker.get(id));
//            if (mAdapter.mLocalVoteTracker.get(id) != null) {
//                if (mAdapter.mLocalVoteTracker.get(id).equals(VALUE_UPVOTED)) {
//                    unVote(id, position, post);
//                    upVoteImageButton.setColorFilter(mColorWhite, PorterDuff.Mode.MULTIPLY);
//                    downVoteImageButton.setColorFilter(mColorWhite, PorterDuff.Mode.MULTIPLY);
//                    upVoteCountTextView.setTextColor(mDefaultCountTextColor);
//                } else {
//                    upVote(id, position, post);
//                    upVoteImageButton.getDrawable().setColorFilter(mUpVoteColor, PorterDuff.Mode.MULTIPLY);
//                    upVoteCountTextView.setTextColor(mUpVoteColor);
//                    downVoteImageButton.setColorFilter(mColorWhite, PorterDuff.Mode.MULTIPLY);
//                }
//            } else {
//                upVote(id, position, post);
//                upVoteImageButton.getDrawable().setColorFilter(mUpVoteColor, PorterDuff.Mode.MULTIPLY);
//                upVoteCountTextView.setTextColor(mUpVoteColor);
//                downVoteImageButton.setColorFilter(mColorWhite, PorterDuff.Mode.MULTIPLY);
//            }});
//}
}
