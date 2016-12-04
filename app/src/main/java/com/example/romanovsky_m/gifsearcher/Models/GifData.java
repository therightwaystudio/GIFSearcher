package com.example.romanovsky_m.gifsearcher.Models;

/**
 * Created by Romanovsky_m on 19.11.2016.
 */

public class GifData {

    private String previewUrl;
    private String gifUrl;
    private String width;
    private String height;
    private String ID;

    public GifData(String previewUrl, String gifUrl, String width, String height, String ID){
        this.previewUrl= previewUrl;
        this.gifUrl = gifUrl;
        this.width = width;
        this.height = height;
        this.ID = ID;
    }

    public String getPreviewUrl() {
        return previewUrl;
    }

    public String getGifUrl() {
        return gifUrl;
    }

    public int getWidth() {
        return Integer.parseInt(width);
    }

    public int getHeight() {
        return Integer.parseInt(height);
    }

    public String getID() {
        return ID;
    }
}
