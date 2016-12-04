package com.example.romanovsky_m.gifsearcher.DataBase;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;



/**
 * Created by Romanovsky_m on 20.11.2016.
 */
@Entity
public class LikedGifData {
    @Id
    private String ID;

    private String PREVIEW_URL;

    private String GIF_URL;

    private String WIDTH;

    private String HEIGHT;

    @Generated(hash = 2019122565)
    public LikedGifData(String ID, String PREVIEW_URL, String GIF_URL, String WIDTH,
            String HEIGHT) {
        this.ID = ID;
        this.PREVIEW_URL = PREVIEW_URL;
        this.GIF_URL = GIF_URL;
        this.WIDTH = WIDTH;
        this.HEIGHT = HEIGHT;
    }

    @Generated(hash = 732342352)
    public LikedGifData() {
    }

    public String getID() {
        return this.ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getPREVIEW_URL() {
        return this.PREVIEW_URL;
    }

    public void setPREVIEW_URL(String PREVIEW_URL) {
        this.PREVIEW_URL = PREVIEW_URL;
    }

    public String getGIF_URL() {
        return this.GIF_URL;
    }

    public void setGIF_URL(String GIF_URL) {
        this.GIF_URL = GIF_URL;
    }

    public String getWIDTH() {
        return this.WIDTH;
    }

    public void setWIDTH(String WIDTH) {
        this.WIDTH = WIDTH;
    }

    public String getHEIGHT() {
        return this.HEIGHT;
    }

    public void setHEIGHT(String HEIGHT) {
        this.HEIGHT = HEIGHT;
    }


}
