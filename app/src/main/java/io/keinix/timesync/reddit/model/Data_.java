package io.keinix.timesync.reddit.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Data_ {

    @SerializedName("domain")
    @Expose
    private String domain;
    @SerializedName("subreddit_id")
    @Expose
    private String subredditId;
    @SerializedName("thumbnail_width")
    @Expose
    private int thumbnailWidth;
    @SerializedName("subreddit")
    @Expose
    private String subreddit;
    @SerializedName("likes")
    @Expose
    private boolean isLiked;
    @SerializedName("is_reddit_media_domain")
    @Expose
    private boolean isRedditMediaDomain;
    @SerializedName("link_flair_text")
    @Expose
    private String linkFlairText;
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("clicked")
    @Expose
    private boolean clicked;
    @SerializedName("author")
    @Expose
    private String author;
    @SerializedName("saved")
    @Expose
    private boolean saved;
    @SerializedName("can_mod_post")
    @Expose
    private boolean canModPost;
    @SerializedName("is_crosspostable")
    @Expose
    private boolean isCrosspostable;
    @SerializedName("pinned")
    @Expose
    private boolean pinned;
    @SerializedName("score")
    @Expose
    private int score;
    @SerializedName("over_18")
    @Expose
    private boolean over18;
    @SerializedName("preview")
    @Expose
    private Preview preview;
    @SerializedName("thumbnail")
    @Expose
    private String thumbnail;
//    @SerializedName("edited")
//    @Expose
//    private boolean edited;
    @SerializedName("contest_mode")
    @Expose
    private boolean contestMode;
    @SerializedName("gilded")
    @Expose
    private int gilded;
    @SerializedName("downs")
    @Expose
    private int downs;
    @SerializedName("post_hint")
    @Expose
    private String postHint;
    @SerializedName("author_flair_text")
    @Expose
    private String authorFlairText;
    @SerializedName("stickied")
    @Expose
    private boolean stickied;
    @SerializedName("can_gild")
    @Expose
    private boolean canGild;
    @SerializedName("thumbnail_height")
    @Expose
    private int thumbnailHeight;
    @SerializedName("parent_whitelist_status")
    @Expose
    private String parentWhitelistStatus;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("spoiler")
    @Expose
    private boolean spoiler;
    @SerializedName("permalink")
    @Expose
    private String permalink;
    @SerializedName("subreddit_type")
    @Expose
    private String subredditType;
    @SerializedName("locked")
    @Expose
    private boolean locked;
    @SerializedName("hide_score")
    @Expose
    private boolean hideScore;
    @SerializedName("created")
    @Expose
    private int created;
    @SerializedName("url")
    @Expose
    private String url;
    @SerializedName("whitelist_status")
    @Expose
    private String whitelistStatus;
    @SerializedName("quarantine")
    @Expose
    private boolean quarantine;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("created_utc")
    @Expose
    private long createdUtc;
    @SerializedName("subreddit_name_prefixed")
    @Expose
    private String subredditNamePrefixed;
    @SerializedName("ups")
    @Expose
    private int ups;
    @SerializedName("media")
    @Expose
    private Media media;
    @SerializedName("num_comments")
    @Expose
    private int numComments;
    @SerializedName("is_self")
    @Expose
    private boolean isSelf;
    @SerializedName("visited")
    @Expose
    private boolean visited;
    @SerializedName("is_video")
    @Expose
    private boolean isVideo;

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getSubredditId() {
        return subredditId;
    }

    public void setSubredditId(String subredditId) {
        this.subredditId = subredditId;
    }

    public int getThumbnailWidth() {
        return thumbnailWidth;
    }

    public void setThumbnailWidth(int thumbnailWidth) {
        this.thumbnailWidth = thumbnailWidth;
    }

    public String getSubreddit() {
        return subreddit;
    }

    public void setSubreddit(String subreddit) {
        this.subreddit = subreddit;
    }

    public boolean isLiked() {
        return isLiked;
    }

    public void setLiked(boolean liked) {
        isLiked = liked;
    }

    public boolean isRedditMediaDomain() {
        return isRedditMediaDomain;
    }

    public void setRedditMediaDomain(boolean redditMediaDomain) {
        isRedditMediaDomain = redditMediaDomain;
    }

    public String getLinkFlairText() {
        return linkFlairText;
    }

    public void setLinkFlairText(String linkFlairText) {
        this.linkFlairText = linkFlairText;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isClicked() {
        return clicked;
    }

    public void setClicked(boolean clicked) {
        this.clicked = clicked;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public boolean isSaved() {
        return saved;
    }

    public void setSaved(boolean saved) {
        this.saved = saved;
    }

    public boolean isCanModPost() {
        return canModPost;
    }

    public void setCanModPost(boolean canModPost) {
        this.canModPost = canModPost;
    }

    public boolean isCrosspostable() {
        return isCrosspostable;
    }

    public void setCrosspostable(boolean crosspostable) {
        isCrosspostable = crosspostable;
    }

    public boolean isPinned() {
        return pinned;
    }

    public void setPinned(boolean pinned) {
        this.pinned = pinned;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public boolean isOver18() {
        return over18;
    }

    public void setOver18(boolean over18) {
        this.over18 = over18;
    }

    public Preview getPreview() {
        return preview;
    }

    public void setPreview(Preview preview) {
        this.preview = preview;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

//    public boolean isEdited() {
//        return edited;
//    }
//
//    public void setEdited(boolean edited) {
//        this.edited = edited;
//    }

    public boolean isContestMode() {
        return contestMode;
    }

    public void setContestMode(boolean contestMode) {
        this.contestMode = contestMode;
    }

    public int getGilded() {
        return gilded;
    }

    public void setGilded(int gilded) {
        this.gilded = gilded;
    }

    public int getDowns() {
        return downs;
    }

    public void setDowns(int downs) {
        this.downs = downs;
    }

    public String getPostHint() {
        return postHint;
    }

    public void setPostHint(String postHint) {
        this.postHint = postHint;
    }

    public String getAuthorFlairText() {
        return authorFlairText;
    }

    public void setAuthorFlairText(String authorFlairText) {
        this.authorFlairText = authorFlairText;
    }

    public boolean isStickied() {
        return stickied;
    }

    public void setStickied(boolean stickied) {
        this.stickied = stickied;
    }

    public boolean isCanGild() {
        return canGild;
    }

    public void setCanGild(boolean canGild) {
        this.canGild = canGild;
    }

    public int getThumbnailHeight() {
        return thumbnailHeight;
    }

    public void setThumbnailHeight(int thumbnailHeight) {
        this.thumbnailHeight = thumbnailHeight;
    }

    public String getParentWhitelistStatus() {
        return parentWhitelistStatus;
    }

    public void setParentWhitelistStatus(String parentWhitelistStatus) {
        this.parentWhitelistStatus = parentWhitelistStatus;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isSpoiler() {
        return spoiler;
    }

    public void setSpoiler(boolean spoiler) {
        this.spoiler = spoiler;
    }

    public String getPermalink() {
        return permalink;
    }

    public void setPermalink(String permalink) {
        this.permalink = permalink;
    }

    public String getSubredditType() {
        return subredditType;
    }

    public void setSubredditType(String subredditType) {
        this.subredditType = subredditType;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public boolean isHideScore() {
        return hideScore;
    }

    public void setHideScore(boolean hideScore) {
        this.hideScore = hideScore;
    }

    public int getCreated() {
        return created;
    }

    public void setCreated(int created) {
        this.created = created;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getWhitelistStatus() {
        return whitelistStatus;
    }

    public void setWhitelistStatus(String whitelistStatus) {
        this.whitelistStatus = whitelistStatus;
    }

    public boolean isQuarantine() {
        return quarantine;
    }

    public void setQuarantine(boolean quarantine) {
        this.quarantine = quarantine;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getCreatedUtc() {
        return createdUtc;
    }

    public void setCreatedUtc(long createdUtc) {
        this.createdUtc = createdUtc;
    }

    public String getSubredditNamePrefixed() {
        return subredditNamePrefixed;
    }

    public void setSubredditNamePrefixed(String subredditNamePrefixed) {
        this.subredditNamePrefixed = subredditNamePrefixed;
    }

    public int getUps() {
        return ups;
    }

    public void setUps(int ups) {
        this.ups = ups;
    }

    public Media getMedia() {
        return media;
    }

    public void setMedia(Media media) {
        this.media = media;
    }

    public int getNumComments() {
        return numComments;
    }

    public void setNumComments(int numComments) {
        this.numComments = numComments;
    }

    public boolean isSelf() {
        return isSelf;
    }

    public void setSelf(boolean self) {
        isSelf = self;
    }

    public boolean isVisited() {
        return visited;
    }

    public void setVisited(boolean visited) {
        this.visited = visited;
    }

    public boolean isVideo() {
        return isVideo;
    }

    public void setVideo(boolean video) {
        isVideo = video;
    }

    @Override
    public String toString() {
        return "Data_{" +
                "domain='" + domain + '\'' +
                ", subredditId='" + subredditId + '\'' +
                ", thumbnailWidth=" + thumbnailWidth +
                ", subreddit='" + subreddit + '\'' +
                ", isLiked=" + isLiked +
                ", isRedditMediaDomain=" + isRedditMediaDomain +
                ", linkFlairText='" + linkFlairText + '\'' +
                ", id='" + id + '\'' +
                ", clicked=" + clicked +
                ", author='" + author + '\'' +
                ", saved=" + saved +
                ", canModPost=" + canModPost +
                ", isCrosspostable=" + isCrosspostable +
                ", pinned=" + pinned +
                ", score=" + score +
                ", over18=" + over18 +
                ", preview=" + preview +
                ", thumbnail='" + thumbnail + '\'' +
                //", edited=" + edited +
                ", contestMode=" + contestMode +
                ", gilded=" + gilded +
                ", downs=" + downs +
                ", postHint='" + postHint + '\'' +
                ", authorFlairText='" + authorFlairText + '\'' +
                ", stickied=" + stickied +
                ", canGild=" + canGild +
                ", thumbnailHeight=" + thumbnailHeight +
                ", parentWhitelistStatus='" + parentWhitelistStatus + '\'' +
                ", name='" + name + '\'' +
                ", spoiler=" + spoiler +
                ", permalink='" + permalink + '\'' +
                ", subredditType='" + subredditType + '\'' +
                ", locked=" + locked +
                ", hideScore=" + hideScore +
                ", created=" + created +
                ", url='" + url + '\'' +
                ", whitelistStatus='" + whitelistStatus + '\'' +
                ", quarantine=" + quarantine +
                ", title='" + title + '\'' +
                ", createdUtc=" + createdUtc +
                ", subredditNamePrefixed='" + subredditNamePrefixed + '\'' +
                ", ups=" + ups +
                ", media=" + media +
                ", numComments=" + numComments +
                ", isSelf=" + isSelf +
                ", visited=" + visited +
                ", isVideo=" + isVideo +
                '}';
    }
}
