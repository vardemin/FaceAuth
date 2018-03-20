package com.vardemin.faceauth.mvp.model;

import com.vardemin.faceauth.data.FaceData;
import com.vardemin.faceauth.mvp.model.camera.FaceDetector;
import com.vardemin.faceauth.mvp.model.camera.FaceListener;

public interface ICameraManager {
    FaceDetector getDetector();

    boolean onFaceData(FaceData data);
    void resetScan();
    void setTracking(boolean state);
}
