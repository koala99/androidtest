package com.android.video;

import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements CameraWrapper.CamOpenOverCallback {

    CameraTexturePreview mCameraTexturePreview;
    int degrees = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        mCameraTexturePreview = (CameraTexturePreview) findViewById(R.id.camera_textureview);
        mCameraTexturePreview.setAlpha(1f);

//        Paint blurPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
//        ColorMatrix cm = new ColorMatrix();
//        cm.setSaturation(0);
//        blurPaint.setColorFilter(new ColorMatrixColorFilter(cm));
//        blurPaint.setMaskFilter(new BlurMaskFilter(25, BlurMaskFilter.Blur.NORMAL));
//        blurPaint.setShader(new RadialGradient(0.5f, 0.5f, 0.2f, Color.BLACK, Color.TRANSPARENT, Shader.TileMode.MIRROR));
//        mCameraTexturePreview.setLayerType(View.LAYER_TYPE_HARDWARE, blurPaint);


        int rotation = getWindowManager().getDefaultDisplay().getRotation();

        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }
        findViewById(R.id.startAndStop).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAndStop(v);
            }
        });
        findViewById(R.id.switchCamera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchCamera(v);
            }
        });

    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    public void switchCamera(View view) {
        CameraWrapper.getInstance().doStopCamera();
        CameraWrapper.getInstance().switchCameraId();
        openCamera();
    }


    public void startAndStop(View view) {
        String tag = (String) view.getTag();
        if (tag.equalsIgnoreCase("stop")) {
//            finish();
            CameraWrapper.getInstance().doStopCamera();
            view.setTag("start");
            ((TextView) view).setText("开始");
        } else {
            openCamera();
            view.setTag("stop");
            ((TextView) view).setText("停止");
        }
    }


    @Override
    protected void onStart() {
        super.onStart();

        openCamera();
    }

    private void openCamera() {
        Thread openThread = new Thread() {
            @Override
            public void run() {
                CameraWrapper.getInstance().doOpenCamera(MainActivity.this, degrees);
            }
        };
        openThread.start();
    }

    @Override
    public void cameraHasOpened() {
        SurfaceTexture surface = this.mCameraTexturePreview.getSurfaceTexture();
        CameraWrapper.getInstance().doStartPreview(surface);
    }
}
