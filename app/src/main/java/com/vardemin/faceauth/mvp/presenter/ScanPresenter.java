package com.vardemin.faceauth.mvp.presenter;

import android.os.Handler;
import android.os.Looper;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.vardemin.faceauth.App;
import com.vardemin.faceauth.data.DataModel;
import com.vardemin.faceauth.data.FaceData;
import com.vardemin.faceauth.mvp.model.ICameraManager;
import com.vardemin.faceauth.mvp.model.ILocalRepository;
import com.vardemin.faceauth.mvp.model.camera.FaceDetector;
import com.vardemin.faceauth.mvp.model.camera.FaceListener;
import com.vardemin.faceauth.mvp.view.ScanView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Observer;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

@InjectViewState
public class ScanPresenter extends MvpPresenter<ScanView> {
    public static final String TAG = "SCAN_PRESENTER";

    @Inject
    ILocalRepository localRepository;

    @Inject
    ICameraManager cameraManager;

    private DataModel dataModel;
    float[] descriptors;
    private Disposable disposable;

    public ScanPresenter() {
        App.getAppComponent().inject(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        cameraManager.resetScan();
        if (disposable != null)
            disposable.dispose();
    }

    public void callReady() {
        getViewState().showLoading(true);
        if (getDetector().isOperational()) {
            getViewState().showLoading(false);
            startScan();
        } else {
            disposable = Completable.timer(500, TimeUnit.MILLISECONDS)
                    .repeatUntil(() -> getDetector().isOperational())
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(() -> {
                        getViewState().showLoading(false);
                        startScan();
                    }, throwable -> {
                        getViewState().showLoading(false);
                        getViewState().showMessage(throwable.getMessage());
                    });
            getDetector().loadLibraries(App.getAppComponent().context());
        }
    }

    private void startScan() {
        getDetector().setListener(listener);
        getViewState().onScanStart();
    }

    public void callOnJSON(String json) {
        try {
            JSONObject object = new JSONObject(json);
            String id = object.getString("id");
            String application = object.getString("application");
            String user = object.getString("user");
            dataModel = localRepository.findObjectById(DataModel.class, id);
            List<Float> _descriptors = dataModel.getFace().getDescriptors();
            descriptors = new float[_descriptors.size()];
            for (int i=0; i<descriptors.length; i++) {
                descriptors[i] = _descriptors.get(i);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            getViewState().onResult("{message: 'Error parsing json',result: false}");
        }
    }

    public void startTracking() {
        cameraManager.setTracking(true);
    }

    public void stopTracking() {
        cameraManager.setTracking(false);
    }

    public FaceDetector getDetector() {
        return cameraManager.getDetector();
    }

    private FaceListener listener = new FaceListener() {
        @Override
        public void onFaceUpdate(FaceData data) {
            if (data.getDescriptors() != null && descriptors != null) {
                if (cameraManager.onFaceData(data, descriptors)) {
                    new Handler(Looper.getMainLooper()).post(() -> {
                        getViewState().onResult(dataModel.toString());
                        stopTracking();
                    });
                }
            }
        }
    };

}
