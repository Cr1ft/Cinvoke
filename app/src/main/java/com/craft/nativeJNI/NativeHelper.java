package com.craft.nativeJNI;

import org.json.JSONObject;

public class NativeHelper {
    public native static void startNewPhone(JSONObject obj);
    public native static void startMethodTrace();
}
