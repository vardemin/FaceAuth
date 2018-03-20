package com.vardemin.faceauth.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.vardemin.faceauth.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE = 77;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                String json = data.getStringExtra("json");
                Log.d("INIT RESULT", json);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.btn_start)
    void onBtnStart() {
        //startActivity(new Intent(this, ScanActivity.class));
        Intent intent = new Intent(this, InitActivity.class);
        intent.putExtra("json", "{id:'test123', application:'storonnee', user:'vasya', fields: [{field: 'email', type: 524321}, {field: 'password', type: 129}]}");
        startActivityForResult(intent, REQUEST_CODE);
    }

}
