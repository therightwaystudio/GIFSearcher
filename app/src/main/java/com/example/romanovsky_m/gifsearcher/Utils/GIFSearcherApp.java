package com.example.romanovsky_m.gifsearcher.Utils;

import android.app.Application;

import com.example.romanovsky_m.gifsearcher.DataBase.DaoMaster;
import com.example.romanovsky_m.gifsearcher.DataBase.DaoSession;

import org.greenrobot.greendao.database.Database;

/**
 * Created by Romanovsky_m on 20.11.2016.
 */

public class GIFSearcherApp extends Application {

    private DaoSession mDaoSession;

    @Override
    public void onCreate() {
        super.onCreate();

        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "gif-db");
        Database database = helper.getWritableDb();
        mDaoSession = new DaoMaster(database).newSession();
    }

    public DaoSession getDaoSession() {
        return mDaoSession;
    }
}
