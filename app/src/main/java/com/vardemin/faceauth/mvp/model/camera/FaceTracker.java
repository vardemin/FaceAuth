package com.vardemin.faceauth.mvp.model.camera;

import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;


public class FaceTracker extends Tracker<Face> {

    private Face face;

    private FaceListener listener;

    @Override
    public void onNewItem(int i, Face face) {
        face = face;
        if (listener != null)
            listener.onNewFace(face);
        super.onNewItem(i, face);
    }

    @Override
    public void onUpdate(Detector.Detections<Face> detections, Face face) {
        if (this.face.getId() == face.getId()) {
            listener.onFaceUpdate(detectPose());
        }
        super.onUpdate(detections, face);
    }

    @Override
    public void onMissing(Detector.Detections<Face> detections) {
        listener.onMissingFace();
        super.onMissing(detections);
    }

    @Override
    public void onDone() {
        super.onDone();
    }

    private FacePosition detectPose() {
        float y = face.getEulerY();
        float z = face.getEulerZ();
        if (y >= -70 && y <= -50) {
            if (z >= -55 && z <= -35)
                return FacePosition.TOP_RIGHT;
            else if (z >= -10 && z <= 10)
                return FacePosition.RIGHT;
            else if (z > 35 && z <= 55)
                return FacePosition.BOTTOM_RIGHT;
        }
        else if (y >= -10 && y <= 10) {
            if (z >= -10 && z <= 10)
                return FacePosition.STRAIGHT;
        }
        else if (y >= 50 && y <= 70) {
            if (z >= -55 && z <= -35)
                return FacePosition.BOTTOM_LEFT;
            else if (z >= -10 && z <= 10)
                return FacePosition.LEFT;
            else if (z > 35 && z <= 55)
                return FacePosition.TOP_LEFT;
        }
        return FacePosition.UNRECOGNIZED;
    }

    public void setListener(FaceListener listener) {
        this.listener = listener;
    }
}
