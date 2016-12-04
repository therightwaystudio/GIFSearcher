package com.example.romanovsky_m.gifsearcher.Models;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Romanovsky_m on 18.11.2016.
 */

public class UrlResourcesSingleton {
    private static UrlResourcesSingleton sUrlResourcesSingleton;
    private List<GifData> mTrendingGifDataList;
    private List<GifData> mSearchGifDataList;
    private List<GifData> mLikedGifDataList=new ArrayList<>();

    public static UrlResourcesSingleton get(Context context) {
        if (sUrlResourcesSingleton == null) {
            sUrlResourcesSingleton = new UrlResourcesSingleton(context);
        }
        return sUrlResourcesSingleton;
    }

    private UrlResourcesSingleton(Context context) {}

    public List<GifData> getTrendingGifDataList() {
        return mTrendingGifDataList;
    }

    public void setTrendingGifDataList(List<GifData> trendingGifDataList) {
        mTrendingGifDataList = trendingGifDataList;
    }

    public List<GifData> getLikedGifDataList() {
        return mLikedGifDataList;
    }

    public void setLikedGifDataList(List<GifData> likedGifDataList) {
        mLikedGifDataList = likedGifDataList;
    }

    public List<GifData> getSearchGifDataList() {
        return mSearchGifDataList;
    }

    public void setSearchGifDataList(List<GifData> searchGifDataList) {
        mSearchGifDataList = searchGifDataList;
    }

    public void removeInLikedGifDataList(String ID){
        for(GifData gifData : mLikedGifDataList){
            if(gifData.getID().equals(ID)){
                mLikedGifDataList.remove(gifData);
                return;
            }
        }
    }

    public boolean searchInLikedGifDataList(String ID) {
        for (GifData gifData : mLikedGifDataList) {
            if (gifData.getID().equals(ID)) {
                return true;
            }
        }
        return false;
    }
}