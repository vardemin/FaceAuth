package com.vardemin.faceauth;

import android.app.Application;

import com.vardemin.faceauth.di.component.AppComponent;
import com.vardemin.faceauth.di.component.DaggerAppComponent;
import com.vardemin.faceauth.di.module.AppModule;
import com.vardemin.faceauth.di.module.DataModule;
import com.vardemin.faceauth.di.module.DetectorModule;

import io.realm.Realm;


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
                .detectorModule(new DetectorModule())
                .dataModule(new DataModule())
                .build();
    }
}
