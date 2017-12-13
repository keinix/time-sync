package io.keinix.timesync.reddit.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RedditAccessToken {
    @SerializedName("scope")
    @Expose
    private String scope;

    @SerializedName("token_type")
    @Expose
    private String token_type;

    @SerializedName("expires_in")
    @Expose
    private long expires_in;

    @SerializedName("access_token")
    @Expose
    private String access_token;

    @SerializedName("refresh_token")
    @Expose
    private String refresh_token;

    public String getScope() {
        return scope;
    }

    public String getRefresh_token() {
        return refresh_token;
    }

    public void setRefresh_token(String refresh_token) {
        this.refresh_token = refresh_token;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getToken_type() {
        return token_type;
    }

    public void setToken_type(String token_type) {
        this.token_type = token_type;
    }

    public long getExpires_in() {
        return expires_in;
    }

    public void setExpires_in(long expires_in) {
        this.expires_in = expires_in;
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    @Override
    public String toString() {
        return "RedditAccessToken{" +
                "scope='" + scope + '\'' +
                ", token_type='" + token_type + '\'' +
                ", expires_in='" + expires_in + '\'' +
                ", access_token='" + access_token + '\'' +
                ", refresh_token='" + refresh_token + '\'' +
                '}';
    }
}
