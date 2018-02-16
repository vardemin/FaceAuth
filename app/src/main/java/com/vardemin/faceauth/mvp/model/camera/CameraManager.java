package com.vardemin.faceauth.mvp.model.camera;

import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.LargestFaceFocusingProcessor;
import com.vardemin.faceauth.mvp.model.ICameraManager;

public class CameraManager implements ICameraManager {


    private final FaceDetector detector;

    public CameraManager(FaceDetector detector) {
        this.detector = detector;
    }

    @Override
    public void onNewFace(Face face) {

    }

    @Override
    public void onMissingFace() {

    }

    @Override
    public void onFaceUpdate(FacePosition position) {

    }

    @Override
    public void onUnrecognizedPose() {

    }
}
