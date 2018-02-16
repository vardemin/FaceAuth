package com.vardemin.faceauth;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.my.jni.dlib.DLibLandmarks68Detector;
import com.vardemin.faceauth.di.component.AppComponent;
import com.vardemin.faceauth.di.component.DaggerAppComponent;
import com.vardemin.faceauth.di.module.AppModule;
import com.vardemin.faceauth.di.module.DataModule;
import com.vardemin.faceauth.di.module.DetectorModule;
import com.vardemin.faceauth.util.Constants;
import com.vardemin.faceauth.util.FileUtils;

import java.io.File;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;

/**
 * Created by user on 22.01.18.
 */

public class App extends Application {

    private static AppComponent appComponent;

    public static AppComponent getAppComponent() {
        return appComponent;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
        appComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .dataModule(new DataModule())
                .detectorModule(new DetectorModule())
                .build();
    }
}
