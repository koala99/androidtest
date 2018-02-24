package com.ne10.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView conTxt = findViewById(R.id.content);
        conTxt.setText(NE10RunTest());
    }

    /* A native method that is implemented by native library, which is packaged
     * with this application.
     */
    public native String NE10RunTest();


    static {
        System.loadLibrary("NE10_test_demo");
    }
}
