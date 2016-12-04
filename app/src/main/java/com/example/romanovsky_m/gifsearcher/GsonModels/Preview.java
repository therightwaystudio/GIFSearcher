
package com.example.romanovsky_m.gifsearcher.GsonModels;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class Preview {

    @SerializedName("mp4")
    @Expose
    private String mp4;
    @SerializedName("mp4_size")
    @Expose
    private String mp4Size;
    @SerializedName("width")
    @Expose
    private String width;
    @SerializedName("height")
    @Expose
    private String height;

    /**
     * 
     * @return
     *     The mp4
     */
    public String getMp4() {
        return mp4;
    }

    /**
     * 
     * @param mp4
     *     The mp4
     */
    public void setMp4(String mp4) {
        this.mp4 = mp4;
    }

    /**
     * 
     * @return
     *     The mp4Size
     */
    public String getMp4Size() {
        return mp4Size;
    }

    /**
     * 
     * @param mp4Size
     *     The mp4_size
     */
    public void setMp4Size(String mp4Size) {
        this.mp4Size = mp4Size;
    }

    /**
     * 
     * @return
     *     The width
     */
    public String getWidth() {
        return width;
    }

    /**
     * 
     * @param width
     *     The width
     */
    public void setWidth(String width) {
        this.width = width;
    }

    /**
     * 
     * @return
     *     The height
     */
    public String getHeight() {
        return height;
    }

    /**
     * 
     * @param height
     *     The height
     */
    public void setHeight(String height) {
        this.height = height;
    }

}
