package com.vardemin.faceauth.util;

import android.os.Environment;

import java.io.File;

/**
 * Created by user on 24.01.18.
 */

public class Constants {

    public static String getFaceShapeModelPath() {
        File sdcard = Environment.getExternalStorageDirectory();
        String targetPath = sdcard.getAbsolutePath() + File.separator + "shape_predictor_68_face_landmarks.dat";
        return targetPath;
    }
}
