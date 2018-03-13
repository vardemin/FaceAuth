package com.vardemin.faceauth.mvp.presenter;

import com.arellomobile.mvp.MvpPresenter;
import com.vardemin.faceauth.App;
import com.vardemin.faceauth.mvp.model.ICameraManager;
import com.vardemin.faceauth.mvp.model.ILocalRepository;
import com.vardemin.faceauth.mvp.model.camera.FaceDetector;
import com.vardemin.faceauth.mvp.view.ScanView;

import javax.inject.Inject;

public class ScanPresenter extends MvpPresenter<ScanView> {
    public static final String TAG = "SCAN_PRESENTER";

    @Inject
    ILocalRepository localRepository;

    @Inject
    ICameraManager cameraManager;

    public ScanPresenter() {
        App.getAppComponent().inject(this);
    }

    public FaceDetector getDetector() {
        return cameraManager.getDetector();
    }
}
