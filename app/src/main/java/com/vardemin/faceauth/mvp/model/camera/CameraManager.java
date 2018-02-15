package com.vardemin.faceauth.mvp.model.camera;

import com.google.android.gms.vision.face.LargestFaceFocusingProcessor;
import com.vardemin.faceauth.mvp.model.ICameraManager;

public class CameraManager implements ICameraManager {


    private final FaceDetector detector;
    private final LargestFaceFocusingProcessor processor;
    private final FaceTracker tracker;

    public CameraManager(FaceDetector detector, LargestFaceFocusingProcessor processor, FaceTracker tracker) {
        this.detector = detector;
        this.processor = processor;
        this.tracker = tracker;
    }
}
