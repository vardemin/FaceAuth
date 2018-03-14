package com.vardemin.faceauth.mvp.model.camera;

import com.vardemin.faceauth.data.FaceData;

public interface FaceListener {

    void onMissingFace();

    void onFaceUpdate(FaceData data);
}
