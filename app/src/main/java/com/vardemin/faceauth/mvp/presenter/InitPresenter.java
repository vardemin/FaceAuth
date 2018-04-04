package com.vardemin.faceauth.mvp.presenter;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.vardemin.faceauth.App;
import com.vardemin.faceauth.data.DataModel;
import com.vardemin.faceauth.data.FaceData;
import com.vardemin.faceauth.data.FaceModel;
import com.vardemin.faceauth.data.FieldModel;
import com.vardemin.faceauth.mvp.model.ICameraManager;
import com.vardemin.faceauth.mvp.model.ILocalRepository;
import com.vardemin.faceauth.mvp.model.camera.FaceDetector;
import com.vardemin.faceauth.mvp.model.camera.FaceListener;
import com.vardemin.faceauth.mvp.view.InitView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

@InjectViewState
public class InitPresenter extends MvpPresenter<InitView> {
    public static final String TAG = "INIT_PRESENTER";

    @Inject
    ILocalRepository localRepository;

    @Inject
    ICameraManager cameraManager;

    private DataModel dataModel;
    private Disposable disposable;

    public InitPresenter() {
        App.getAppComponent().inject(this);
    }

    public void callReady(){
        getViewState().showLoading(true);
        if(getDetector().isOperational()) {
            getViewState().showLoading(false);
            startScan();
        }
        else {
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
        JSONObject result = new JSONObject();
        try {
            JSONObject object = new JSONObject(json);
            String id = object.getString("id");
            String application = object.getString("application");
            String user = object.getString("user");
            JSONArray fields = object.getJSONArray("fields");
            List<FieldModel> fieldModels = new ArrayList<>();
            for (int i = 0; i < fields.length(); i++) {
                JSONObject obj = fields.getJSONObject(i);
                String field = obj.getString("field");
                int type = obj.getInt("type");
                fieldModels.add(new FieldModel(field, type));
            }
            dataModel = new DataModel(id, application, user, fieldModels, new Date());
            callSaveData(false);

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
        getViewState().onInputStart();
    }

    public FaceDetector getDetector() {
        return cameraManager.getDetector();
    }

    public List<FieldModel> getFields() {
        return dataModel.getFields();
    }

    private FaceListener listener = new FaceListener() {
        @Override
        public void onFaceUpdate(FaceData data) {
            if (data.getDescriptors() != null && dataModel != null) {
                FaceModel faceModel = new FaceModel(data.getDescriptors());
                dataModel.setFace(faceModel);
                callSaveData(false);
                stopTracking();
            }
        }
    };

    public void callSaveData(boolean finish) {
        localRepository.save(dataModel);
        if(finish) {
            getViewState().onResult(dataModel.toString());
        }
    }
}
