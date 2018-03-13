package com.vardemin.faceauth.mvp.model.camera;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.SparseArray;
import android.widget.Toast;

import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.my.jni.dlib.DLibLandmarks68Detector;
import com.vardemin.faceauth.App;
import com.vardemin.faceauth.R;
import com.vardemin.faceauth.data.FaceData;
import com.vardemin.faceauth.util.Constants;
import com.vardemin.faceauth.util.FileUtils;

import java.io.File;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class FaceDetector extends Detector<Face> {

    private DLibLandmarks68Detector dlibDetector;

    private FaceData faceData;

    private boolean isFaceLoaded = false;
    private boolean isLandmarkLoaded = false;

    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    private com.google.android.gms.vision.face.FaceDetector detector;

    private FaceListener listener;

    private Disposable disposable;

    public FaceDetector(Context context) {

        this.listener = listener;

        detector = new com.google.android.gms.vision.face.FaceDetector.Builder(context)
                .setClassificationType(com.google.android.gms.vision.face.FaceDetector.ALL_CLASSIFICATIONS)
                .setMode(com.google.android.gms.vision.face.FaceDetector.ACCURATE_MODE)
                .setTrackingEnabled(true)
                .setProminentFaceOnly(true)
                .build();
        dlibDetector = new DLibLandmarks68Detector();

        compositeDisposable.add(initFaceLandmarksDetector(context)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.newThread())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        Log.d("DLIB LANDMARK", "LOADED");
                        isLandmarkLoaded = true;
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.d("DLIB", throwable.getLocalizedMessage());
                    }
                }));

        compositeDisposable.add(initFaceDetector(context)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.newThread())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        Log.d("DLIB FACE", "LOADED");
                        isFaceLoaded = true;
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.d("DLIB", throwable.getLocalizedMessage());
                    }
                }));
    }

    private Observable<Boolean> initFaceLandmarksDetector(final Context context) {
        final String targetPath = Constants.getFaceShapeModelPath();
        if (!new File(targetPath).exists()) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, "Copy landmark model to " + targetPath, Toast.LENGTH_SHORT).show();
                }
            });

            FileUtils.copyFileFromRawToOthers(context, R.raw.shape_predictor_68_face_landmarks, targetPath);
        }
        dlibDetector.prepareLandmark(targetPath);
        return Observable.just(false);
    }

    private Observable<Boolean> initFaceDetector(final Context context) {
        final String targetPath = Constants.getFaceModelPath();
        if (!new File(targetPath).exists()) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, "Copy face model to " + targetPath, Toast.LENGTH_SHORT).show();
                }
            });

            FileUtils.copyFileFromRawToOthers(context, R.raw.dlib_face_recognition_resnet_model_v1, targetPath);
        }
        dlibDetector.prepareRecognition(targetPath);
        return Observable.just(false);
    }

    @Override
    public void release() {
        compositeDisposable.dispose();
        super.release();
    }

    @Override
    public SparseArray<Face> detect(Frame frame) {
        SparseArray<Face> faces = new SparseArray<>();
        if (disposable == null || disposable.isDisposed()) {
            faces = detector.detect(frame);
            if (faces.size() > 0) {
                if (faceData == null) {
                    faceData = new FaceData(faces.get(0), frame);
                    listener.onNewFace(faces.get(0));
                } else {
                    boolean found = false;
                    for (int i = 0; i < faces.size(); i++) {
                        int key = faces.keyAt(i);
                        if (faces.get(key).getId() == faceData.getFace().getId()) {
                            FacePosition pose = detectPose(faces.get(key));
                            if (pose != FacePosition.UNRECOGNIZED) {
                                found = true;
                                faceData.setFace(faces.get(key));
                                faceData.setNV21(frame.getGrayscaleImageData().array());
                                faceData.setPosition(pose);
                            } else listener.onUnrecognizedPose();
                            break;
                        }
                    }
                    if (found) {
                        float[] descriptors = getDescriptor(frame, faceData.getFace());
                        faceData.setDescriptors(descriptors);
                    } else listener.onMissingFace();
                }
            }
        }
        return faces;
    }

    @Override
    public boolean isOperational() {
        return !isFaceLoaded && !isLandmarkLoaded && detector.isOperational();
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

    public void setListener(FaceListener listener) {
        this.listener = listener;
    }
}
