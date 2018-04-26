package io.keinix.timesync.adapters;

import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.keinix.timesync.Activities.SubredditActivity;
import io.keinix.timesync.R;
import io.keinix.timesync.reddit.Api;
import io.keinix.timesync.reddit.RedditAuthInterceptor;
import io.keinix.timesync.reddit.RedditConstants;
import io.keinix.timesync.reddit.TokenAuthenticator;
import io.keinix.timesync.reddit.model.SubReddit;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SubRedditAdapter extends Adapter {
    private static final String TAG = SubRedditAdapter.class.getSimpleName();
    private List<SubReddit> mSubReddits;
    private Api mApi;
    private Context mContext;

    public SubRedditAdapter(Context context) {
        mSubReddits = new ArrayList<>();
        mContext = context;
        mApi = initApi();
        getSubReddits();

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.sub_reddit_item, parent, false);
        return new SubRedditViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((SubRedditViewHolder) holder).bindView(position);
    }

    @Override
    public int getItemCount() {
        return mSubReddits.size();
    }

    private Api initApi() {
        AccountManager am = AccountManager.get(mContext);
        OkHttpClient.Builder client = new OkHttpClient.Builder()
                .addInterceptor(new RedditAuthInterceptor(am, mContext))
                .authenticator(new TokenAuthenticator(am));

        return new Retrofit.Builder().baseUrl(RedditConstants.REDDIT_BASE_URL_OAUTH2)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client.build())
                .build()
                .create(Api.class);
    }

    public void getSubReddits() {

        mApi.getSubReddits("100").enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {

                if (response.isSuccessful()) {
                    populateSubReddits(response.body());
                    sortSubReddits(mSubReddits);
                    notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                Log.d(TAG, "onFail: " + t.toString());
            }
        });
    }


    public void populateSubReddits(JsonElement json) {
        Gson gson = new Gson();
        JsonArray subRedditChildren =  json.getAsJsonObject().getAsJsonObject("data").getAsJsonArray("children");
        for (JsonElement child : subRedditChildren) {
            JsonObject subreddit = child.getAsJsonObject().getAsJsonObject("data");
            mSubReddits.add(gson.fromJson(subreddit, SubReddit.class));
        }
    }

    public void sortSubReddits(List<SubReddit> subreddits) {

        Collections.sort(subreddits, (sub1, sub2) ->
                String.CASE_INSENSITIVE_ORDER.compare(sub1.getDisplayName(), sub2.getDisplayName()));

        Collections.sort(subreddits, (sub1, sub2) ->
                Boolean.compare(sub2.isFavorited(), sub1.isFavorited()));
    }

    public List<SubReddit> getSubList() { return mSubReddits; }



    public class SubRedditViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.subNameTextView) TextView subNameTextView;
        @BindView(R.id.subImageView) SimpleDraweeView subDraweeView;
        @BindView(R.id.starImageView) ImageButton starImageView;
        @BindView(R.id.subredditLinearLayout) LinearLayout subredditLayout;

        int starSelectedColor;

        public SubRedditViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            starSelectedColor = ContextCompat.getColor(mContext, R.color.upVoteColor);
        }

        public void bindView(int position) {
            setStarColor(position);
            SubReddit subreddit = mSubReddits.get(position);
            subNameTextView.setText(subreddit.getDisplayNamePrefixed());
            if (subreddit.getIconImg() != null) subDraweeView.setImageURI(subreddit.getIconImg());

            starImageView.setOnClickListener(v -> {
                if (subreddit.isFavorited()) {
                    starImageView.getDrawable().clearColorFilter();
                    subreddit.setFavorited(false);
                } else {
                    starImageView.getDrawable().setColorFilter(starSelectedColor, PorterDuff.Mode.MULTIPLY);
                    subreddit.setFavorited(true);
                }
            });

            subredditLayout.setOnClickListener(v -> {
                Intent intent = new Intent(mContext, SubredditActivity.class);
                intent.putExtra(SubredditActivity.KEY_SUBREDDIT, subreddit);
                mContext.startActivity(intent);
            });
        }

        private void setStarColor(int position) {
            starImageView.getDrawable().mutate();
            if (mSubReddits.get(position).isFavorited()) {
                starImageView.getDrawable().setColorFilter(starSelectedColor, PorterDuff.Mode.MULTIPLY);
            } else {
                starImageView.getDrawable().clearColorFilter();
            }
        }
    }
}
