package com.vardemin.faceauth.mvp.model;

import com.vardemin.faceauth.mvp.model.camera.FaceDetector;
import com.vardemin.faceauth.mvp.model.camera.FaceListener;

public interface ICameraManager{
    FaceDetector getDetector();
}
