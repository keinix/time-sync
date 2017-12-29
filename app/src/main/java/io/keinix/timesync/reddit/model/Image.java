package io.keinix.timesync.reddit.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Image {

    //same structure as resolution but the highest resolution
    @SerializedName("source")
    @Expose
    private Source source;

    // resolutions are in order of lowest to highest
    @SerializedName("resolutions")
    @Expose
    private List<Resolution> resolutions = null;
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("variants")
    @Expose
    private Variants variants;

    public Variants getVariants() {
        return variants;
    }

    public void setVariants(Variants variants) {
        this.variants = variants;
    }

    public Source getSource() {
        return source;
    }

    public void setSource(Source source) {
        this.source = source;
    }

    public List<Resolution> getResolutions() {
        return resolutions;
    }

    public void setResolutions(List<Resolution> resolutions) {
        this.resolutions = resolutions;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Image{" +
                "source=" + source +
                ", resolutions=" + resolutions +
                ", id='" + id + '\'' +
                ", variants=" + variants +
                '}';
    }
}
