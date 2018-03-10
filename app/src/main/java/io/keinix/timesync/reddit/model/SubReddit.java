package io.keinix.timesync.reddit.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SubReddit {

    @SerializedName("banner_img")
    @Expose
    private String bannerImage;

    @SerializedName("description")
    @Expose
    private String description;

    @SerializedName("title")
    @Expose
    private String title;

    @SerializedName("user_has_favorited")
    @Expose
    private boolean isFavorited;

    @SerializedName("icon_img")
    @Expose
    private String iconImg;

    @SerializedName("display_name_prefixed")
    @Expose
    private String displayNamePrefixed;

    @SerializedName("subscribers")
    @Expose
    private int subscriber;

    @SerializedName("key_color")
    @Expose
    private String keyColor;

    @SerializedName("header_title")
    @Expose
    private String headerTitle;

    public String getBannerImage() {
        return bannerImage;
    }

    public void setBannerImage(String bannerImage) {
        this.bannerImage = bannerImage;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isFavorited() {
        return isFavorited;
    }

    public void setFavorited(boolean favorited) {
        isFavorited = favorited;
    }

    public String getIconImg() {
        return iconImg;
    }

    public void setIconImg(String iconImg) {
        this.iconImg = iconImg;
    }

    public String getDisplayNamePrefixed() {
        return displayNamePrefixed;
    }

    public void setDisplayNamePrefixed(String displayNamePrefixed) {
        this.displayNamePrefixed = displayNamePrefixed;
    }

    public int getSubscriber() {
        return subscriber;
    }

    public void setSubscriber(int subscriber) {
        this.subscriber = subscriber;
    }

    public String getKeyColor() {
        return keyColor;
    }

    public void setKeyColor(String keyColor) {
        this.keyColor = keyColor;
    }

    public String getHeaderTitle() {
        return headerTitle;
    }

    public void setHeaderTitle(String headerTitle) {
        this.headerTitle = headerTitle;
    }

    @Override
    public String toString() {
        return "SubReddit{" +
                "bannerImage='" + bannerImage + '\'' +
                ", description='" + description + '\'' +
                ", title='" + title + '\'' +
                ", isFavorited=" + isFavorited +
                ", iconImg='" + iconImg + '\'' +
                ", displayNamePrefixed='" + displayNamePrefixed + '\'' +
                ", subscriber=" + subscriber +
                ", keyColor='" + keyColor + '\'' +
                ", headerTitle='" + headerTitle + '\'' +
                '}';
    }
}
