package com.android.ndkdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Example of a call to a native method
        TextView tv = (TextView) findViewById(R.id.sample_text);
        Log.e("dem1o", "static initializer: ");
        tv.setText(new NativeUtils().stringFromJNI());

        tv.setText(NativeUtils.getString());
        tv.setText(NativeUtils.changeString("2222"));
        ArrayList<String> lists = new ArrayList<>();
        lists.add("12");
        lists.add("13");
        lists.add("14");
        lists.add("15");
        lists.add("16");
        ArrayList<String> list =
                NativeUtils.changeStringList(lists);
        for (String str : list) {
            Log.e("onCreate: ", str);

        }
    }


}
