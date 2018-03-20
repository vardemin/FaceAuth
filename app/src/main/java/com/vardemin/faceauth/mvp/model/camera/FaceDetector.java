package com.vardemin.faceauth.mvp.model.camera;

import android.content.Context;
import android.util.Log;
import android.util.SparseArray;

import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.my.jni.dlib.DLibLandmarks68Detector;
import com.vardemin.faceauth.R;
import com.vardemin.faceauth.data.FaceData;
import com.vardemin.faceauth.util.Constants;
import com.vardemin.faceauth.util.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class FaceDetector extends Detector<Face> {
    private final static String TAG = "FACE DETECTOR";

    private DLibLandmarks68Detector dlibDetector;

    private boolean isLoaded;

    private FaceListener listener;

    private com.google.android.gms.vision.face.FaceDetector detector;

    private FacePosition desiredPose = FacePosition.UNRECOGNIZED;

    private Disposable disposable;

    private boolean isAllowed = false;

    public void setTracking(boolean state) {
        isAllowed = state;
    }

    public FaceDetector(Context context) {
        detector = new com.google.android.gms.vision.face.FaceDetector.Builder(context)
                .setClassificationType(com.google.android.gms.vision.face.FaceDetector.NO_CLASSIFICATIONS)
                .setLandmarkType(com.google.android.gms.vision.face.FaceDetector.NO_LANDMARKS)
                .setMode(com.google.android.gms.vision.face.FaceDetector.ACCURATE_MODE)
                .setTrackingEnabled(true)
                .build();
        dlibDetector = new DLibLandmarks68Detector();
    }

    public void loadLibraries(Context context) {
        if (!isLoaded) {
            disposable =
                    Completable.merge(getInitList(context))
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(() -> isLoaded = true, throwable -> Log.d("DLIB", throwable.getLocalizedMessage()));
        }
    }

    private List<Completable> getInitList(Context context) {
        List<Completable> completableList = new ArrayList<>();
        completableList.add(initFaceLandmarksDetector(context));
        completableList.add(initFaceDetector(context));
        return completableList;
    }

    private Completable initFaceLandmarksDetector(final Context context) {
        final String targetPath = Constants.getFaceShapeModelPath();
        if (!new File(targetPath).exists()) {
            FileUtils.copyFileFromRawToOthers(context, R.raw.shape_predictor_68_face_landmarks, targetPath);
        }
        dlibDetector.prepareLandmark(targetPath);
        Log.d("DLIB LANDMARK", "LOADED");
        return Completable.complete();
    }

    private Completable initFaceDetector(final Context context) {
        final String targetPath = Constants.getFaceModelPath();
        if (!new File(targetPath).exists()) {
            FileUtils.copyFileFromRawToOthers(context, R.raw.dlib_face_recognition_resnet_model_v1, targetPath);
        }
        dlibDetector.prepareRecognition(targetPath);
        Log.d("DLIB FACE DETECTOR", "LOADED");
        return Completable.complete();
    }

    @Override
    public void release() {
        disposable.dispose();
        super.release();
    }

    @Override
    public SparseArray<Face> detect(Frame frame) {
        SparseArray<Face> faces = new SparseArray<>();
        if (isAllowed) {
            if (disposable == null || disposable.isDisposed()) {
                faces = detector.detect(frame);
                if (faces.size() > 0) {
                    Face face = faces.valueAt(0);
                    FaceData.Builder builder = FaceData.newBuilder();
                    FacePosition pose = detectPose(face);
                    builder.setFace(face);
                    builder.setNV21(frame.getGrayscaleImageData().array());
                    builder.setPosition(pose);
                    //if (pose != FacePosition.UNRECOGNIZED && pose == desiredPose) {
                        float[] descriptors = getDescriptor(frame, face);
                        builder.setDescriptors(descriptors);
                    //}
                    notifyFace(builder.build());
                }
            }
        }
        return faces;
    }

    @Override
    public boolean isOperational() {
        return isLoaded && detector.isOperational();
    }

    private float[] getDescriptor(Frame frame, Face face) {
        long left = (long) face.getPosition().x;
        long top = (long) face.getPosition().y;
        long right = (long) (face.getPosition().x + face.getWidth());
        long bottom = (long) (face.getPosition().y + face.getHeight());
        float[] descriptor = dlibDetector.findDescriptors(frame.getGrayscaleImageData().array(), frame.getMetadata().getWidth(), frame.getMetadata().getHeight(),
                left, top, right, bottom);
        return descriptor;
    }

    private FacePosition detectPose(Face face) {
        float y = face.getEulerY();
        float z = face.getEulerZ();
        if (y >= -70 && y <= -50) {
            if (z >= -55 && z <= -35)
                return FacePosition.TOP_RIGHT;
            else if (z >= -10 && z <= 10)
                return FacePosition.RIGHT;
            else if (z > 35 && z <= 55)
                return FacePosition.BOTTOM_RIGHT;
        } else if (y >= -10 && y <= 10) {
            if (z >= -10 && z <= 10)
                return FacePosition.STRAIGHT;
        } else if (y >= 50 && y <= 70) {
            if (z >= -55 && z <= -35)
                return FacePosition.BOTTOM_LEFT;
            else if (z >= -10 && z <= 10)
                return FacePosition.LEFT;
            else if (z > 35 && z <= 55)
                return FacePosition.TOP_LEFT;
        }
        return FacePosition.UNRECOGNIZED;
    }

    private void notifyFace(FaceData data) {
        if (listener != null)
            listener.onFaceUpdate(data);
    }


    public FaceListener getListener() {
        return listener;
    }

    public void setListener(FaceListener listener) {
        this.listener = listener;
    }

    public void setDesiredPose(FacePosition pose) {
        this.desiredPose = pose;
    }

    public boolean compareDescriptors(float[] source, float[] destination, float limit) {
        return dlibDetector.compareDescriptors(source, destination, limit);
    }
}
