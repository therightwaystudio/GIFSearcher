package com.example.romanovsky_m.gifsearcher.Activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.romanovsky_m.gifsearcher.DataBase.DaoSession;
import com.example.romanovsky_m.gifsearcher.DataBase.LikedGifData;
import com.example.romanovsky_m.gifsearcher.Fragments.GIFsFragment;
import com.example.romanovsky_m.gifsearcher.Models.GifData;
import com.example.romanovsky_m.gifsearcher.Models.UrlResourcesSingleton;
import com.example.romanovsky_m.gifsearcher.R;
import com.example.romanovsky_m.gifsearcher.Services.LoadingService;
import com.example.romanovsky_m.gifsearcher.Utils.GIFSearcherApp;


import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ScrollingActivity extends AppCompatActivity {

    private static final String BACK_STACK_ROOT_TAG = "root_fragment";
    public static final String LOG_TAG = "MyTag";
    public static final String LOAD_TYPE = "load_type";
    public static final String LOAD_FROM_DB = "load_from_db";
    public static final String SEARCH_QUERY = "search_query";
    public static final String SEARCH_LANG = "search_lang";
    public static final String LIKE_GIFS_LIST_TYPE = "like_gifs_list_type";
    public static final String TRENDING_GIFS_LIST_TYPE = "trending_gifs_list_type";
    public static final String SEARCH_GIFS_LIST_TYPE = "search_gifs_list_type";
    private static final  String TITLE_NAME = "title_name";
    private static boolean STARTED = false;
    private static boolean LOADED_DB = false;
    private static String search=null;
    private MenuItem searchMenuItem;
    private Intent mIntent;
    private LoadedBroadcastReceiver mLoadedBroadcastReceiver;
    private CollapsingToolbarLayout mCollapsingToolbarLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_scrolling);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mCollapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);


        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {

            }
        });

        if(savedInstanceState != null){
            String titleName = savedInstanceState.getString(TITLE_NAME);
            mCollapsingToolbarLayout.setTitle(titleName);
        }

    }

    @Override
    public void onBackPressed() {
        if(getSupportFragmentManager().getBackStackEntryCount()==1) {
            finishAffinity();
            STARTED = false;
        } else {
            super.onBackPressed();
        }
    }

    public MenuItem getSearchMenuItem() {
        return searchMenuItem;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_scrolling, menu);

        searchMenuItem = menu.findItem(R.id.action_search);

        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                search = String.valueOf(searchView.getQuery());

                mIntent = new Intent(getApplicationContext(), LoadingService.class);
                mIntent.putExtra(LOAD_TYPE, "search");
                if(rusCheck(search)){
                    mIntent.putExtra(SEARCH_LANG,"ru");
                } else{
                    mIntent.putExtra(SEARCH_LANG,"en");
                }
                mIntent.putExtra(SEARCH_QUERY, search.replace(" ","+"));
                startService(mIntent);
                MenuItem searchMenuItem = getSearchMenuItem();
                if (searchMenuItem != null) {
                    searchMenuItem.collapseActionView();
                }

                mCollapsingToolbarLayout.setTitle(search);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        MenuItem searchMenuItem = getSearchMenuItem();
        if (searchMenuItem != null) {
            searchMenuItem.collapseActionView();
        }

        int id = item.getItemId();

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment;

        if (id == R.id.action_search) {
            if(UrlResourcesSingleton.get(getBaseContext()).getSearchGifDataList()!=null) {
                fragment = GIFsFragment.newInstance(SEARCH_GIFS_LIST_TYPE);
                fragmentManager.popBackStack(BACK_STACK_ROOT_TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                fragmentManager.beginTransaction()
                        .replace(R.id.content_scrolling_container, fragment)
                        .addToBackStack(BACK_STACK_ROOT_TAG)
                        .commit();
            }
            mCollapsingToolbarLayout.setTitle(search);
        } else if(id == R.id.action_liked){
            fragment = GIFsFragment.newInstance(LIKE_GIFS_LIST_TYPE);
            fragmentManager.popBackStack(BACK_STACK_ROOT_TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            fragmentManager.beginTransaction()
                    .replace(R.id.content_scrolling_container,fragment)
                    .addToBackStack(BACK_STACK_ROOT_TAG)
                    .commit();
            mCollapsingToolbarLayout.setTitle("Liked");
        } else if(id == R.id.action_home){
            if(UrlResourcesSingleton.get(getBaseContext()).getTrendingGifDataList()!=null) {
                fragment = GIFsFragment.newInstance(TRENDING_GIFS_LIST_TYPE);
                fragmentManager.popBackStack(BACK_STACK_ROOT_TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                fragmentManager.beginTransaction()
                        .replace(R.id.content_scrolling_container, fragment)
                        .addToBackStack(BACK_STACK_ROOT_TAG)
                        .commit();
            }
            mCollapsingToolbarLayout.setTitle("GIFSearcher");
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(!LOADED_DB){
            mIntent = new Intent(this, LoadingService.class);
            mIntent.putExtra(LOAD_FROM_DB, true);
            startService(mIntent);
            LOADED_DB = true;
        }
        if(isNetworkEnable(getBaseContext())){
            startLoading();
        }else{
            reload();
        }
        mLoadedBroadcastReceiver = new LoadedBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter(LoadingService.ACTION_LOADING_SERVICE);
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
        this.registerReceiver(mLoadedBroadcastReceiver, intentFilter);
    }

    @Override
    public void onStop() {
        super.onStop();

        this.unregisterReceiver(mLoadedBroadcastReceiver);
        loadFromSingletonInDB();
    }

    public class LoadedBroadcastReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            String result = intent.getStringExtra(LoadingService.EXTRA_KEY_OUT);
            FragmentManager fragmentManager = getSupportFragmentManager();
            Fragment fragment;
            switch (result){
                case LoadingService.RESPONSE_OK_SEARCH:
                    fragment = GIFsFragment.newInstance(SEARCH_GIFS_LIST_TYPE);
                    fragmentManager.popBackStack(BACK_STACK_ROOT_TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    fragmentManager.beginTransaction()
                            .replace(R.id.content_scrolling_container,fragment)
                            .addToBackStack(BACK_STACK_ROOT_TAG)
                            .commit();
                    break;

                case LoadingService.RESPONSE_OK_TRENDING:
                    fragment = GIFsFragment.newInstance(TRENDING_GIFS_LIST_TYPE);
                    fragmentManager.popBackStack(BACK_STACK_ROOT_TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    fragmentManager.beginTransaction()
                            .replace(R.id.content_scrolling_container,fragment)
                            .addToBackStack(BACK_STACK_ROOT_TAG)
                            .commit();
                    mCollapsingToolbarLayout.setTitle("GIFSearcher");
                    break;

                default:
                    break;
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(TITLE_NAME,String.valueOf(mCollapsingToolbarLayout.getTitle()));
    }

    private void loadFromSingletonInDB(){
        List<LikedGifData> likedGifDataList = new ArrayList<>();
        LikedGifData likedGifData;

        String ID;
        String PREVIEW_URL;
        String GIF_URL;
        String WIDTH;
        String HEIGHT;
        if(UrlResourcesSingleton.get(getBaseContext()).getLikedGifDataList()!=null) {
            for (GifData gifData : UrlResourcesSingleton.get(getBaseContext()).getLikedGifDataList()){
                ID = gifData.getID();
                PREVIEW_URL = gifData.getPreviewUrl();
                GIF_URL = gifData.getGifUrl();
                WIDTH = String.valueOf(gifData.getWidth());
                HEIGHT = String.valueOf(gifData.getHeight());

                likedGifData = new LikedGifData(ID,PREVIEW_URL,GIF_URL,WIDTH,HEIGHT);
                likedGifDataList.add(likedGifData);
            }
            getAppDaoSession().getLikedGifDataDao().deleteAll();
            getAppDaoSession().getLikedGifDataDao().insertInTx(likedGifDataList);
        }

    }

    private DaoSession getAppDaoSession(){
        return ((GIFSearcherApp) getApplication()).getDaoSession();
    }

    public boolean isNetworkEnable(Context context){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting())
        {
            return true;
        }
        return false;
    }

    public void startLoading(){
        if(!STARTED) {
            mIntent = new Intent(this, LoadingService.class);
            mIntent.putExtra(LOAD_TYPE, "trending");
            startService(mIntent);
            STARTED = true;
        }
    }

    public void reload(){
        Snackbar.make(findViewById(R.id.coordinator_layout),"Нет доступа к Интернету",Snackbar.LENGTH_INDEFINITE).setAction("ПОВТОРИТЬ", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isNetworkEnable(getBaseContext())){
                    startLoading();
                } else {
                    reload();
                }
            }
        }).show();
    }

    private boolean rusCheck(String string){
        Pattern pattern = Pattern.compile("[" + "а-яА-ЯёЁ" + "\\d" + "\\s" + "\\p{Punct}" + "]" + "*");
        Matcher matcher = pattern.matcher(string);
        return matcher.matches();
    }
}
