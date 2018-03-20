package com.vardemin.faceauth.ui.activity;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;
import com.vardemin.faceauth.R;
import com.vardemin.faceauth.mvp.model.camera.FaceDetector;
import com.vardemin.faceauth.mvp.model.camera.FacePosition;
import com.vardemin.faceauth.mvp.presenter.ScanPresenter;
import com.vardemin.faceauth.mvp.view.ScanView;
import com.vardemin.faceauth.ui.graphic.FaceGraphic;
import com.vardemin.faceauth.ui.view.CameraSourcePreview;
import com.vardemin.faceauth.ui.view.GraphicOverlay;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ScanActivity extends MvpAppCompatActivity implements ScanView {

    private static final String TAG = "SCAN_ACTIVITY";
    private static final int RC_HANDLE_GMS = 9001;

    @BindView(R.id.preview)
    CameraSourcePreview preview;

    @BindView(R.id.overlay)
    GraphicOverlay overlay;

    @BindView(R.id.tv_status)
    TextView status;

    @InjectPresenter(tag = ScanPresenter.TAG)
    ScanPresenter presenter;

    private CameraSource cameraSource = null;

    private Snackbar waitingSnackbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        ButterKnife.bind(this);

        waitingSnackbar = Snackbar.make(findViewById(android.R.id.content), "Loading libraries...", Snackbar.LENGTH_INDEFINITE);
        Snackbar.SnackbarLayout snack_view = (Snackbar.SnackbarLayout) waitingSnackbar.getView();
        snack_view.addView(new ProgressBar(this));

        presenter.callReady();
    }

    @Override
    public void showMessage(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    @Override
    public void showLoading(boolean state) {

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        preview.stop();
        finish();
    }

    @Override
    protected void onDestroy() {
        if (cameraSource != null) {
            cameraSource.release();
        }
        super.onDestroy();
    }

    private void createCameraSource() {
        Context context = getApplicationContext();

        presenter.getDetector().setProcessor(
                            new MultiProcessor.Builder<>(new GraphicPoseTrackerFactory())
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

    @Override
    public void showWaitingDialog(boolean state) {
        if (state) {
            waitingSnackbar.show();
        }
        else waitingSnackbar.dismiss();
    }

    @Override
    public void onScanStart() {
        createCameraSource();
        startCameraSource();
    }

    @Override
    public void showMissingFace() {
        showMessage(getString(R.string.error_missing));
    }

    @Override
    public void notifyPendingPose(FacePosition position) {
        String str = "";
        switch (position) {
            case STRAIGHT: str = getString(R.string.pose_straight); break;
            case TOP: str = getString(R.string.pose_top); break;
            case TOP_LEFT: str = getString(R.string.pose_top_left); break;
            case LEFT: str = getString(R.string.pose_left); break;
            case BOTTOM_LEFT: str = getString(R.string.pose_bottom_left); break;
            case BOTTOM: str = getString(R.string.pose_bottom); break;
            case BOTTOM_RIGHT: str = getString(R.string.pose_bottom_right); break;
            case RIGHT: str = getString(R.string.pose_right); break;
            case TOP_RIGHT: str = getString(R.string.pose_top_right); break;
        }
        String msg = getString(R.string.look_at) + " " + str;
        runOnUiThread(() -> status.setText(msg));
        //showMessage(getString(R.string.look_at) + " " + position.getDesription(this));
    }

    @Override
    public void notifyDifferentPerson() {
        showMessage(getString(R.string.error_person));
    }

    private class GraphicPoseTrackerFactory implements MultiProcessor.Factory<Face> {
        @Override
        public Tracker<Face> create(Face pose) {
            return new GraphicPoseTracker(overlay);
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
            ScanActivity.this.showMissingFace();
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
