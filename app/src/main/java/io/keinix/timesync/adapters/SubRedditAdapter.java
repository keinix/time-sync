package io.keinix.timesync.adapters;

import android.accounts.AccountManager;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

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
        mApi.getSubReddits().enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Responce: " + response.body());
                    notifyDataSetChanged();
                }
                Log.d(TAG, "Not Successful: " + response);
                Log.d(TAG, "Response body: " + response.body());
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d(TAG, "onFail: " + t.toString());
            }
        });

    }

    public class SubRedditViewHolder extends RecyclerView.ViewHolder {

        public SubRedditViewHolder(View itemView) {
            super(itemView);
        }

        public void bindView(int position) {

        }
    }
}
