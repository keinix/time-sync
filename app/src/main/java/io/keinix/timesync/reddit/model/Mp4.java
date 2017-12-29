package io.keinix.timesync.reddit.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Mp4 {

    @SerializedName("source")
    @Expose
    private Source Source;
    @SerializedName("resolutions")
    @Expose
    private List<Resolution> resolutions = null;

    public io.keinix.timesync.reddit.model.Source getSource() {
        return Source;
    }

    public void setSource(io.keinix.timesync.reddit.model.Source source) {
        Source = source;
    }

    public List<Resolution> getResolutions() {
        return resolutions;
    }

    public void setResolutions(List<Resolution> resolutions) {
        this.resolutions = resolutions;
    }

    @Override
    public String toString() {
        return "Mp4{" +
                "Source=" + Source +
                ", resolutions=" + resolutions +
                '}';
    }
}
