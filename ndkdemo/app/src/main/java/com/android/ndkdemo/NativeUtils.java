package com.android.ndkdemo;

import java.util.ArrayList;

/**
 * Created by lilei on 2018/1/20.
 */

public class NativeUtils {


    public native String stringFromJNI();

    public native static String getString();

    public native static String changeString(String str);

    public native static ArrayList<String> changeStringList(ArrayList<String> list);
    static {
        System.loadLibrary("native-lib");
    }
}
