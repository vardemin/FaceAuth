package com.vardemin.faceauth.di.module;

import android.content.Context;

import com.google.android.gms.vision.face.LargestFaceFocusingProcessor;
import com.vardemin.faceauth.mvp.model.ICameraManager;
import com.vardemin.faceauth.mvp.model.ILocalRepository;
import com.vardemin.faceauth.mvp.model.camera.CameraManager;
import com.vardemin.faceauth.mvp.model.camera.FaceDetector;
import com.vardemin.faceauth.mvp.model.repository.LocalRepository;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class DataModule {
    @Provides
    @Singleton
    public ICameraManager provideCameraManager(FaceDetector detector) {
        return new CameraManager(detector);
    }

    @Provides
    @Singleton
    public ILocalRepository provideLocalDataRepository(Context context) {
        return new LocalRepository(context);
    }

}
