package subatom.eden_beta.model;

import android.graphics.Bitmap;

import java.io.Serializable;

public class VideoObject implements Serializable {
    private String videoId;
    private String title;
    private String description;
    private String def;
    private String length;
    private Bitmap image;

    public VideoObject() {
    }
    /*Setters*/
    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDef(String def) {
        this.def = def;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public Bitmap setImage(Bitmap image){
        return this.image = image;
    }

    /*Getters*/

    public String getVideoId() {
        return videoId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getDef() { return def; }

    public Bitmap getImage(){
        return image;
    }

    public String getLength() {
        return length;
    }
}
