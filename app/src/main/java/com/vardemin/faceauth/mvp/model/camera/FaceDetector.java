package com.vardemin.faceauth.mvp.model.camera;

import android.content.Context;
import android.util.SparseArray;

import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;

public class FaceDetector extends Detector<Face> {

    private com.google.android.gms.vision.face.FaceDetector detector;

    public FaceDetector(Context context) {
        detector = new com.google.android.gms.vision.face.FaceDetector.Builder(context)
                .setClassificationType(com.google.android.gms.vision.face.FaceDetector.ALL_CLASSIFICATIONS)
                .setMode(com.google.android.gms.vision.face.FaceDetector.ACCURATE_MODE)
                .setTrackingEnabled(true)
                .setProminentFaceOnly(true)
                .build();
    }

    @Override
    public SparseArray<Face> detect(Frame frame) {
        return detector.detect(frame);
    }
}
