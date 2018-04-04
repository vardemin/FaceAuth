package com.vardemin.faceauth.ui.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;
import com.vardemin.faceauth.R;
import com.vardemin.faceauth.data.FieldModel;
import com.vardemin.faceauth.mvp.model.camera.FaceDetector;
import com.vardemin.faceauth.mvp.presenter.InitPresenter;
import com.vardemin.faceauth.mvp.view.InitView;
import com.vardemin.faceauth.ui.graphic.FaceGraphic;
import com.vardemin.faceauth.ui.view.CameraSourcePreview;
import com.vardemin.faceauth.ui.view.GraphicOverlay;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class InitActivity extends MvpAppCompatActivity implements InitView {

    private static final String TAG = "INIT_ACTIVITY";
    private static final int RC_HANDLE_GMS = 9001;

    @BindView(R.id.scan_layout)
    View scanlayout;

    @BindView(R.id.input_layout)
    LinearLayout inputLayout;

    @BindView(R.id.btn_capture)
    Button btnCapture;

    @BindView(R.id.preview)
    CameraSourcePreview preview;

    @BindView(R.id.overlay)
    GraphicOverlay overlay;

    @InjectPresenter(tag = InitPresenter.TAG)
    InitPresenter presenter;

    private Snackbar waitingSnackbar;
    private CameraSource cameraSource = null;

    private boolean isInScan = true;

    private Map<FieldModel, EditText> inputMap = new HashMap<>();
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init);
        ButterKnife.bind(this);

        waitingSnackbar = Snackbar.make(findViewById(android.R.id.content), "Loading libraries...", Snackbar.LENGTH_INDEFINITE);
        Snackbar.SnackbarLayout snack_view = (Snackbar.SnackbarLayout) waitingSnackbar.getView();
        snack_view.addView(new ProgressBar(this));
        Intent intent = getIntent();
        String json = getIntent().getStringExtra("json");
        if (json != null) {
            presenter.callReady();
            presenter.callOnJSON(json);
        } else onResult("{message: 'no json parameter', result: false}");

        handler= new Handler(Looper.getMainLooper());
    }

    @Override
    public void onResult(String json) {
        handler.post(() -> {
            Intent intent = new Intent();
            intent.putExtra("json", json);
            setResult(RESULT_OK, intent);
            finish();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (cameraSource != null && isInScan)
            startCameraSource();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (cameraSource != null && isInScan)
            preview.stop();
    }

    @Override
    protected void onDestroy() {
        if (isInScan && cameraSource != null) {
            cameraSource.release();
        }
        super.onDestroy();
    }

    @Override
    public void onInputStart() {
        handler.post(() -> {
            preview.stop();
            cameraSource.release();
            isInScan = false;
            scanlayout.setVisibility(View.GONE);
            inputLayout.setVisibility(View.VISIBLE);
            generateInputFields(presenter.getFields());
        });
    }

    @Override
    public void onScanStart() {
        createCameraSource();
        startCameraSource();
    }

    @Override
    public void onMissingFace() {
        handler.post(() -> btnCapture.setEnabled(false));

    }

    @Override
    public void onFace() {
        handler.post(() -> btnCapture.setEnabled(true));

    }

    @Override
    public void showMessage(String msg) {
        Snackbar.make(findViewById(android.R.id.content), msg, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void showLoading(boolean state) {
        if (state) {
            waitingSnackbar.show();
        } else waitingSnackbar.dismiss();
    }

    private void createCameraSource() {
        Context context = getApplicationContext();

        presenter.getDetector().setProcessor(
                new MultiProcessor.Builder<>(new InitActivity.GraphicPoseTrackerFactory())
                        .build());

        if (!presenter.getDetector().isOperational()) {
            //TODO: wait for native library
            Log.w(TAG, "Face detector dependencies are not yet available.");
        }

        cameraSource = new CameraSource.Builder(context, presenter.getDetector())
                .setRequestedPreviewSize(640, 480)
                .setFacing(CameraSource.CAMERA_FACING_FRONT)
                .setRequestedFps(30f)
                .build();
    }

    private void startCameraSource() {

        int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(
                getApplicationContext());
        if (code != ConnectionResult.SUCCESS) {
            Dialog dlg =
                    GoogleApiAvailability.getInstance().getErrorDialog(this, code, RC_HANDLE_GMS);
            dlg.show();
        }

        if (cameraSource != null) {
            try {
                preview.start(cameraSource, overlay);
            } catch (IOException e) {
                Log.e(TAG, "Unable to start camera source.", e);
                cameraSource.release();
                cameraSource = null;
            }
        }
    }

    @OnClick(R.id.btn_capture)
    void onCapture() {
        presenter.startTracking();
        btnCapture.setEnabled(false);
    }

    @OnClick(R.id.btn_enter)
    void onBtnEnter() {
        for (Map.Entry<FieldModel, EditText> entry : inputMap.entrySet()) {
            entry.getKey().setValue(entry.getValue().getText().toString());
        }
        presenter.callSaveData(true);
    }


    private void generateInputFields(List<FieldModel> fields) {
        for (FieldModel model : fields) {
            EditText editTextView = new EditText(this);
            editTextView.setGravity(Gravity.CENTER);

            TextInputLayout textInputLayout = new TextInputLayout(this);
            textInputLayout.setGravity(Gravity.CENTER);
            textInputLayout.setHint(model.getField());

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT, 1);

            editTextView.setLayoutParams(params);

/*          TODO: set correct input type ?
            if(model.getInputType() == InputType.TYPE_TEXT_VARIATION_PASSWORD) {
                editTextView.setInputType( InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD );
                editTextView.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
            else if (model.getInputType() == InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS) {
                editTextView.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
            }
            else*/
                editTextView.setInputType(model.getInputType());

            textInputLayout.addView(editTextView, 0, params);
            inputLayout.addView(textInputLayout, inputLayout.getChildCount()-1);
            inputMap.put(model, editTextView);
        }
    }

    private class GraphicPoseTrackerFactory implements MultiProcessor.Factory<Face> {
        @Override
        public Tracker<Face> create(Face pose) {
            return new InitActivity.GraphicPoseTracker(overlay);
        }
    }

    private class GraphicPoseTracker extends Tracker<Face> {
        private GraphicOverlay overlay;
        private FaceGraphic faceGraphic;

        GraphicPoseTracker(GraphicOverlay overlay) {
            this.overlay = overlay;
            faceGraphic = new FaceGraphic(overlay);
        }

        /**
         * Start tracking the detected face instance within the face overlay.
         */
        @Override
        public void onNewItem(int faceId, Face item) {
            faceGraphic.setId(faceId);
            InitActivity.this.onFace();
        }

        /**
         * Update the position/characteristics of the face within the overlay.
         */
        @Override
        public void onUpdate(FaceDetector.Detections<Face> detectionResults, Face pose) {
            overlay.add(faceGraphic);
            faceGraphic.updatePose(pose);
        }

        /**
         * Hide the graphic when the corresponding face was not detected.  This can happen for
         * intermediate frames temporarily (e.g., if the face was momentarily blocked from
         * view).
         */
        @Override
        public void onMissing(FaceDetector.Detections<Face> detectionResults) {
            InitActivity.this.onMissingFace();
            overlay.remove(faceGraphic);
        }

        /**
         * Called when the face is assumed to be gone for good. Remove the graphic annotation from
         * the overlay.
         */
        @Override
        public void onDone() {
            overlay.remove(faceGraphic);
        }
    }
}
