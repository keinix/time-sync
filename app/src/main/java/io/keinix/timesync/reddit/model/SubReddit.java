package io.keinix.timesync.reddit.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SubReddit implements Parcelable {


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

    @SerializedName("display_name")
    @Expose
    private String displayName;

    @SerializedName("subscribers")
    @Expose
    private int subscriber;

    @SerializedName("key_color")
    @Expose
    private String keyColor;

    @SerializedName("header_title")
    @Expose
    private String headerTitle;

    @SerializedName("user_is_subscriber")
    @Expose
    private boolean isSubcriber;

    @SerializedName("public_description")
    @Expose
    private String publicDescription;

    @SerializedName("name")
    @Expose
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPublicDescription() {
        return publicDescription;
    }

    public void setPublicDescription(String publicDescription) {
        this.publicDescription = publicDescription;
    }

    public boolean isSubcriber() {
        return isSubcriber;
    }

    public void setSubcriber(boolean subcriber) {
        isSubcriber = subcriber;
    }


    public String getBannerImage() {
        return bannerImage;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
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
                ", displayName='" + displayName + '\'' +
                ", subscriber=" + subscriber +
                ", keyColor='" + keyColor + '\'' +
                ", headerTitle='" + headerTitle + '\'' +
                ", isSubcriber=" + isSubcriber +
                ", publicDescription='" + publicDescription + '\'' +
                ", name='" + name + '\'' +
                '}';
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(bannerImage);
        parcel.writeString(description);
        parcel.writeString(title);
        parcel.writeByte((byte) (isFavorited ? 1 : 0));
        parcel.writeString(iconImg);
        parcel.writeString(displayNamePrefixed);
        parcel.writeString(displayName);
        parcel.writeString(keyColor);
        parcel.writeString(headerTitle);
        parcel.writeInt(subscriber);
        parcel.writeByte((byte) (isSubcriber ? 1 : 0));
        parcel.writeString(publicDescription);
        parcel.writeString(name);
    }

    private SubReddit(Parcel in) {
        bannerImage = in.readString();
        description = in.readString();
        title = in.readString();
        isFavorited = in.readByte() != 0;
        iconImg = in.readString();
        displayNamePrefixed = in.readString();
        displayName = in.readString();
        keyColor = in.readString();
        headerTitle = in.readString();
        subscriber = in.readInt();
        isSubcriber = in.readByte() != 0;
        publicDescription = in.readString();
        name = in.readString();
    }

    public static final Creator<SubReddit> CREATOR = new Creator<SubReddit>() {
        @Override
        public SubReddit createFromParcel(Parcel parcel) {
            return new SubReddit(parcel);
        }

        @Override
        public SubReddit[] newArray(int i) {
            return new SubReddit[i];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }
}
