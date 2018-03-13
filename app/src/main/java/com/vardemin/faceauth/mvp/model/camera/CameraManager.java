package com.vardemin.faceauth.mvp.model.camera;

import com.vardemin.faceauth.mvp.model.ICameraManager;

public class CameraManager implements ICameraManager {

    private final FaceDetector detector;

    public CameraManager(FaceDetector detector) {
        this.detector = detector;
    }


    @Override
    public FaceDetector getDetector() {
        return detector;
    }
}
