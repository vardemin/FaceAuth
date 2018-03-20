package com.vardemin.faceauth.di.component;

import android.content.Context;

import com.vardemin.faceauth.di.module.AppModule;
import com.vardemin.faceauth.di.module.DataModule;
import com.vardemin.faceauth.di.module.DetectorModule;
import com.vardemin.faceauth.mvp.model.ICameraManager;
import com.vardemin.faceauth.mvp.model.ILocalRepository;
import com.vardemin.faceauth.mvp.model.camera.FaceDetector;
import com.vardemin.faceauth.mvp.presenter.InitPresenter;
import com.vardemin.faceauth.mvp.presenter.ScanPresenter;

import javax.inject.Singleton;

import dagger.Component;

@Component(modules = {AppModule.class, DataModule.class, DetectorModule.class})
@Singleton
public interface AppComponent {
    Context context();
    ILocalRepository localRepository();
    ICameraManager cameraManager();
    FaceDetector detector();

    void inject(ScanPresenter presenter);

    void inject(InitPresenter initPresenter);
}
