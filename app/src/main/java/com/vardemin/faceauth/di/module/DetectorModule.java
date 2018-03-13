package com.vardemin.faceauth.di.module;

import android.content.Context;

import com.google.android.gms.vision.face.LargestFaceFocusingProcessor;
import com.vardemin.faceauth.mvp.model.ICameraManager;
import com.vardemin.faceauth.mvp.model.camera.FaceDetector;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Provides detection components
 * PoseDetector, PoseProcessor
 */
@Module
public class DetectorModule {

    @Provides
    @Singleton
    public FaceDetector provideDetector(Context context) {
        return new FaceDetector(context);
    }


}