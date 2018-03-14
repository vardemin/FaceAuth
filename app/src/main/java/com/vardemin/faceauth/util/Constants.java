package com.vardemin.faceauth.util;

import android.os.Environment;

import java.io.File;

public class Constants {

    public static final float FACE_SIMILAR_LIMIT = 0.6f;

    public static String getFaceShapeModelPath() {
        File sdcard = Environment.getExternalStorageDirectory();
        String targetPath = sdcard.getAbsolutePath() + "/" + "shape_predictor_68_face_landmarks.dat";
        return targetPath;
    }

    public static String getFaceModelPath() {
        File sdcard = Environment.getExternalStorageDirectory();
        String targetPath = sdcard.getAbsolutePath() + "/" + "dlib_face_recognition_resnet_model_v1.dat";
        return targetPath;
    }
}
