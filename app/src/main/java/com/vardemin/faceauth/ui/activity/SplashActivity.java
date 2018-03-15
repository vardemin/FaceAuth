package com.vardemin.faceauth.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import com.vardemin.faceauth.R;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;


public class SplashActivity extends AppCompatActivity {

    private Disposable subscriber;

    private static final int REQUEST_CODE = 77;

    private boolean isAllowed = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            isAllowed = false;
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA},
                    REQUEST_CODE);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isAllowed)
            callNext();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (subscriber != null)
            subscriber.dispose();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode != REQUEST_CODE) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }
        if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            isAllowed = true;
            callNext();
        }
    }

    private void callNext() {
        subscriber = Observable
                .just(true)
                .delay(750, TimeUnit.MILLISECONDS)
                .subscribe(ignored -> {
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    finish();
                });
    }
}
