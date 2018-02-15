package com.vardemin.faceauth;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.my.jni.dlib.DLibLandmarks68Detector;
import com.my.jni.dlib.IDLibFaceDetector;
import com.vardemin.faceauth.di.component.AppComponent;
import com.vardemin.faceauth.di.component.DaggerAppComponent;
import com.vardemin.faceauth.di.module.AppModule;
import com.vardemin.faceauth.di.module.DataModule;
import com.vardemin.faceauth.di.module.DetectorModule;
import com.vardemin.faceauth.mvp.model.repository.LocalRepository;
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


    private static IDLibFaceDetector detector;

    public static IDLibFaceDetector getDetector() {
        return detector;
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
        detector = new DLibLandmarks68Detector();
        initFaceLandmarksDetector()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.newThread())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        Log.d("DLIB", "LOADED");
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.d("DLIB", throwable.getLocalizedMessage());
                    }
                });

    }

    private Observable<Boolean> initFaceLandmarksDetector() {
        final String targetPath = Constants.getFaceShapeModelPath();
        if (!new File(targetPath).exists()) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(App.this, "Copy landmark model to " + targetPath, Toast.LENGTH_SHORT).show();
                }
            });

            FileUtils.copyFileFromRawToOthers(getApplicationContext(), R.raw.shape_predictor_68_face_landmarks, targetPath);
        }
        if (!detector.isFaceDetectorReady()) {
            detector.prepareFaceDetector();
        }
        if (!detector.isFaceLandmarksDetectorReady()) {
            detector.prepareFaceLandmarksDetector(targetPath);
        }
        return Observable.just(false);
    }

}
