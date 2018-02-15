package com.vardemin.faceauth.di.component;

import android.content.Context;

import com.google.android.gms.vision.face.LargestFaceFocusingProcessor;
import com.vardemin.faceauth.di.module.AppModule;
import com.vardemin.faceauth.di.module.DataModule;
import com.vardemin.faceauth.di.module.DetectorModule;
import com.vardemin.faceauth.mvp.model.ICameraManager;
import com.vardemin.faceauth.mvp.model.ILocalRepository;
import com.vardemin.faceauth.mvp.model.camera.FaceDetector;
import com.vardemin.faceauth.mvp.model.camera.FaceTracker;
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
    FaceTracker tracker();

    void inject(ScanPresenter presenter);
}
