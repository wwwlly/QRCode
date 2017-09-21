package com.google.zxing.client.android.config;

/**
 * Created by wangkp on 2017/9/19.
 */

public class ConfigUnit {

    private static ConfigUnit instance;

    private int screenOrientation = Constants.SCREEN_ORIENTATION_LANDSCAPE;

    private ConfigUnit (){

    }

    public static ConfigUnit getInstance(){
        if(instance == null){
            instance = new ConfigUnit();
        }
        return instance;
    }

    public int getScreenOrientation() {
        return screenOrientation;
    }

    public void setScreenOrientation(int screenOrientation) {
        this.screenOrientation = screenOrientation;
    }
}
