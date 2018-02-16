package com.vardemin.faceauth.data;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.vardemin.faceauth.mvp.model.camera.FacePosition;

public class FaceData {
    private Face face;
    private byte[] nv21;
    private float[] descriptors;
    private FacePosition position;

    public FaceData(Face face, Frame frame) {
        this.face = face;
        this.nv21 = frame.getGrayscaleImageData().array();
    }

    public Face getFace() {
        return face;
    }

    public void setFace(Face face) {
        this.face = face;
    }

    public byte[] getNV21() {
        return nv21;
    }

    public void setNV21(byte[] nv21) {
        this.nv21 = nv21;
    }

    public float[] getDescriptors() {
        return descriptors;
    }

    public void setDescriptors(float[] descriptors) {
        this.descriptors = descriptors;
    }

    public FacePosition getPosition() {
        return position;
    }

    public void setPosition(FacePosition position) {
        this.position = position;
    }
}
