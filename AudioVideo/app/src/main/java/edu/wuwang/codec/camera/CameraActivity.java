package edu.wuwang.codec.camera;

import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

import edu.wuwang.codec.R;
import edu.wuwang.codec.coder.CameraRecorder;

/**
 * Description:
 */
public class CameraActivity extends AppCompatActivity implements FrameCallback {

    private CameraView mCameraView;
    private CircularProgressView mCapture;
    //    private ExecutorService mExecutor;
    private boolean recordFlag = false;

    private CameraRecorder mp4Recorder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }


    void initView() {
//        mExecutor = Executors.newSingleThreadExecutor();
        setContentView(R.layout.activity_camera);
        mCameraView = (CameraView) findViewById(R.id.mCameraView);
        mCameraView.setFrameCallback(384, 640, CameraActivity.this);
        mCapture = (CircularProgressView) findViewById(R.id.mCapture);
        mCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recordFlag = !recordFlag;
                if (recordFlag) {
                    startRecord();
                } else {
                    finishRecord();
                }
            }
        });
    }


    private String savePath;

    private void finishRecord() {
        try {
            mp4Recorder.stop();
            mCameraView.stopRecord();
            recordComplete(savePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startRecord() {
        if (mp4Recorder == null) {
            mp4Recorder = new CameraRecorder();
        }
        Toast.makeText(this, "开始录制视频", Toast.LENGTH_LONG).show();
        long time = System.currentTimeMillis();
        savePath = getPath("video/", time + ".mp4");
        mp4Recorder.setSavePath(getPath("video/", time + ""), "mp4");
        try {

            mp4Recorder.prepare(384, 640);
            mp4Recorder.start();
            mCameraView.setFrameCallback(384, 640, CameraActivity.this);
            mCameraView.startRecord();

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private String getBaseFolder() {
        String baseFolder = Environment.getExternalStorageDirectory() + "/Codec/";
        File f = new File(baseFolder);
        if (!f.exists()) {
            boolean b = f.mkdirs();
            if (!b) {
                baseFolder = getExternalFilesDir(null).getAbsolutePath() + "/";
            }
        }
        return baseFolder;
    }

    //获取VideoPath
    private String getPath(String path, String fileName) {
        String p = getBaseFolder() + path;
        File f = new File(p);
        if (!f.exists() && !f.mkdirs()) {
            return getBaseFolder() + fileName;
        }
        return p + fileName;
    }


    private void recordComplete(final String path) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mCapture.setProcess(0);
                Toast.makeText(CameraActivity.this, "文件保存路径：" + path, Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        mCameraView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mCameraView.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add("切换摄像头").setTitle("切换摄像头").setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String name = item.getTitle().toString();
        if (name.equals("切换摄像头")) {
            mCameraView.switchCamera();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFrame(byte[] bytes, long time) {
        mp4Recorder.feedData(bytes, time);
    }
}
