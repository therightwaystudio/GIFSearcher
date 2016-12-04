package com.example.romanovsky_m.gifsearcher.Services;

import android.app.IntentService;
import android.content.Intent;

import com.example.romanovsky_m.gifsearcher.DataBase.DaoSession;
import com.example.romanovsky_m.gifsearcher.DataBase.LikedGifData;
import com.example.romanovsky_m.gifsearcher.GsonModels.Datum;
import com.example.romanovsky_m.gifsearcher.GsonModels.FixedHeight;
import com.example.romanovsky_m.gifsearcher.GsonModels.FixedHeightStill;
import com.example.romanovsky_m.gifsearcher.GsonModels.Giphy;
import com.example.romanovsky_m.gifsearcher.GsonModels.Images;
import com.example.romanovsky_m.gifsearcher.Models.GifData;
import com.example.romanovsky_m.gifsearcher.Models.UrlResourcesSingleton;
import com.example.romanovsky_m.gifsearcher.Activities.ScrollingActivity;
import com.example.romanovsky_m.gifsearcher.Utils.GIFSearcherApp;
import com.example.romanovsky_m.gifsearcher.Utils.GiphyService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class LoadingService extends IntentService {

    public static final String BASE_URL = "http://api.giphy.com/v1/gifs/";
    public static final String KEY = "dc6zaTOxFJmzC";

    public static final String ACTION_LOADING_SERVICE = "com.example.romanovsky_m.testproject";
    public static final String EXTRA_KEY_OUT = "EXTRA_OUT";
    public static final String RESPONSE_OK_TRENDING = "ok_trending";
    public static final String RESPONSE_OK_SEARCH = "ok_search";
    private String mResponse;
    private Intent mIntent;
    private String mLoadType;
    public LoadingService(){
        super("LoadingService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        mIntent = intent;
        if(mIntent.getBooleanExtra(ScrollingActivity.LOAD_FROM_DB,false)){
            loadFromDbInSingleton();
        } else {
            loadGifData();
        }
    }

    private void loadGifData(){

        String searchQuery;
        String searchLang;

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        mLoadType = mIntent.getStringExtra(ScrollingActivity.LOAD_TYPE);
        GiphyService giphyService = retrofit.create(GiphyService.class);
        Call<Giphy> request;
        switch(mLoadType){
            case "trending":
                request = giphyService.getTrending(KEY);
                mResponse = RESPONSE_OK_TRENDING;
                break;
            case "search":
                searchQuery = mIntent.getStringExtra(ScrollingActivity.SEARCH_QUERY);
                mResponse = RESPONSE_OK_SEARCH;
                searchLang = mIntent.getStringExtra(ScrollingActivity.SEARCH_LANG);
                request = giphyService.getSearch(searchQuery, KEY, searchLang);
                break;
            default:
                request = giphyService.getTrending(KEY);
                break;
        }
        request.enqueue(new Callback<Giphy>() {
            @Override
            public void onResponse(Call<Giphy> call, Response<Giphy> response) {
                Giphy giphy = response.body();
                List<Datum> datumList = giphy.getData();
                List<GifData> urlSourceList = new ArrayList<GifData>();
                String previewUrl;
                String gifUrl;
                String width;
                String height;
                String ID;
                GifData gifData;
                for(Datum datum : datumList){
                    Images images = datum.getImages();
                    FixedHeightStill fixedHeightStill = images.getFixedHeightStill();
                    FixedHeight fixedHeight = images.getFixedHeight();
                    ID = datum.getId();
                    previewUrl = fixedHeightStill.getUrl();
                    gifUrl = fixedHeight.getUrl();
                    width = fixedHeight.getWidth();
                    height = fixedHeight.getHeight();
                    gifData = new GifData(previewUrl, gifUrl, width, height, ID);
                    urlSourceList.add(gifData);
                }
                switch(mLoadType){
                    case "trending":
                        UrlResourcesSingleton.get(getBaseContext()).setTrendingGifDataList(urlSourceList);
                        break;
                    case "search":
                        UrlResourcesSingleton.get(getBaseContext()).setSearchGifDataList(urlSourceList);
                        break;
                    default:
                        UrlResourcesSingleton.get(getBaseContext()).setTrendingGifDataList(urlSourceList);
                        break;
                }

                Intent responseIntent = new Intent();
                responseIntent.setAction(ACTION_LOADING_SERVICE);
                responseIntent.addCategory(Intent.CATEGORY_DEFAULT);
                responseIntent.putExtra(EXTRA_KEY_OUT, mResponse);
                sendBroadcast(responseIntent);
            }

            @Override
            public void onFailure(Call<Giphy> call, Throwable t) {

            }
        });
    }

    private void loadFromDbInSingleton(){
        List<LikedGifData> likedGifDataList = getAppDaoSession().getLikedGifDataDao().loadAll();
        List<GifData> gifLikedDataList = new ArrayList<>();
        GifData gifData;

        String previewUrl;
        String gifUrl;
        String width;
        String height;
        String ID;

        for(LikedGifData likedGifData : likedGifDataList){
            previewUrl = likedGifData.getPREVIEW_URL();
            gifUrl = likedGifData.getGIF_URL();
            width = likedGifData.getWIDTH();
            height = likedGifData.getHEIGHT();
            ID = likedGifData.getID();
            gifData = new GifData(previewUrl,gifUrl,width,height,ID);

            gifLikedDataList.add(gifData);
        }
        UrlResourcesSingleton.get(getBaseContext()).setLikedGifDataList(gifLikedDataList);
    }

    private DaoSession getAppDaoSession(){
        return ((GIFSearcherApp) getApplication()).getDaoSession();
    }
}
