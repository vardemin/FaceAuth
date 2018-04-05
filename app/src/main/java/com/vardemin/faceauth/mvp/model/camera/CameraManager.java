package com.vardemin.faceauth.mvp.model.camera;

import android.util.Log;

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
    public boolean onFaceData(FaceData data, float[] original) {
        /*float[] descriptors = data.getDescriptors();
        if (descriptors != null) {
            if (lastDescriptors == null) {
                lastDescriptors = descriptors;
                return true;
            } else {
                boolean result = detector.compareDescriptors(lastDescriptors, descriptors, Constants.FACE_SIMILAR_LIMIT);
                lastDescriptors = descriptors;
                return result;
            }
        }
        return false;*/
        float similarity = detector.getSimilarity(data.getDescriptors(), original);
        Log.d("SIMILARITY", String.valueOf(similarity));
        return similarity < Constants.FACE_SIMILAR_LIMIT;
    }

    @Override
    public void resetScan() {
        lastDescriptors = null;
    }

    @Override
    public void setTracking(boolean state) {
        detector.setTracking(state);
    }
}
