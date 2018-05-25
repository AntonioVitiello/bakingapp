package av.udacity.bakingapp.net.jsonstubs;

import android.text.TextUtils;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Step {

    @SerializedName("id")
    @Expose
    private Integer id;

    @SerializedName("shortDescription")
    @Expose
    private String shortDescription;

    @SerializedName("description")
    @Expose
    private String description;

    @SerializedName("videoURL")
    @Expose
    private String videoURL;

    @SerializedName("thumbnailURL")
    @Expose
    private String thumbnailURL;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    // filter any characters
    public String getDescription() {
        return description.replaceAll("[^A-Za-zäöüÄÖÜß?!$,. 0-9\\-\\+\\*\\?=&%\\$§\"\\!\\^#:;,_²³°\\[\\]\\{\\}<>\\|~]", "");
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // return thumbnailURL if contains a video url
    public String getVideoURL() {
        if(TextUtils.isEmpty(videoURL) && thumbnailURL.endsWith(".mp4")){
            videoURL = thumbnailURL;
        }
        return videoURL;
    }

    public void setVideoURL(String videoURL) {
        this.videoURL = videoURL;
    }

    public String getThumbnailURL() {
        return thumbnailURL;
    }

    public void setThumbnailURL(String thumbnailURL) {
        this.thumbnailURL = thumbnailURL;
    }

}
