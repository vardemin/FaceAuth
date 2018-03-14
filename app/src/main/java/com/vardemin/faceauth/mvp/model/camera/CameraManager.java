package com.vardemin.faceauth.mvp.model.camera;

import com.my.jni.dlib.DLibLandmarks68Detector;
import com.vardemin.faceauth.data.FaceData;
import com.vardemin.faceauth.mvp.model.ICameraManager;
import com.vardemin.faceauth.util.Constants;

public class CameraManager implements ICameraManager {

    private final FaceDetector detector;

    private float[] lastDescriptors = null;

    public CameraManager(FaceDetector detector) {
        this.detector = detector;
    }


    @Override
    public FaceDetector getDetector() {
        return detector;
    }

    @Override
    public boolean onFaceData(FaceData data) {
        if(lastDescriptors == null) {
            lastDescriptors = data.getDescriptors();
            return true;
        }
        else {
            boolean result = detector.compareDescriptors(lastDescriptors, data.getDescriptors(), Constants.FACE_SIMILAR_LIMIT);
            lastDescriptors = data.getDescriptors();
            return result;
        }
    }

    @Override
    public void resetScan() {
        lastDescriptors = null;
    }
}
