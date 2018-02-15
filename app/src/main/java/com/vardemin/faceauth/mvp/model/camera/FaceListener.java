package com.vardemin.faceauth.mvp.model.camera;

import com.google.android.gms.vision.face.Face;

public interface FaceListener {
    void onNewFace(Face face);

    void onMissingFace();

    void onFaceUpdate(FacePosition position);
}
