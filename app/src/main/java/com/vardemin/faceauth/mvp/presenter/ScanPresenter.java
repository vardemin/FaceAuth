package com.vardemin.faceauth.mvp.presenter;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.vardemin.faceauth.App;
import com.vardemin.faceauth.data.FaceData;
import com.vardemin.faceauth.mvp.model.ICameraManager;
import com.vardemin.faceauth.mvp.model.ILocalRepository;
import com.vardemin.faceauth.mvp.model.camera.FaceDetector;
import com.vardemin.faceauth.mvp.model.camera.FaceListener;
import com.vardemin.faceauth.mvp.model.camera.FacePosition;
import com.vardemin.faceauth.mvp.view.ScanView;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.BooleanSupplier;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

@InjectViewState
public class ScanPresenter extends MvpPresenter<ScanView> {
    public static final String TAG = "SCAN_PRESENTER";

    @Inject
    ILocalRepository localRepository;

    @Inject
    ICameraManager cameraManager;

    private Disposable disposable;

    public ScanPresenter() {
        App.getAppComponent().inject(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        cameraManager.resetScan();
        disposable.dispose();
    }

    public FaceDetector getDetector() {
        return cameraManager.getDetector();
    }

    public void callReady(){
        getViewState().showWaitingDialog(true);
        disposable = Completable.timer(500, TimeUnit.MILLISECONDS)
                .repeatUntil(() -> getDetector().isOperational())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    getViewState().showWaitingDialog(false);
                    startScan();
                    getDetector().setDesiredPose(FacePosition.STRAIGHT);
                }, throwable -> {
                    getViewState().showWaitingDialog(false);
                    getViewState().showMessage(throwable.getMessage());
                });
        getDetector().loadLibraries(App.getAppComponent().context());
    }

    private void startScan() {
        getDetector().setListener(listener);
        getViewState().onScanStart();
    }

    private FaceListener listener = new FaceListener() {
        @Override
        public void onMissingFace() {

        }

        @Override
        public void onFaceUpdate(FaceData data) {

        }

    };
}
