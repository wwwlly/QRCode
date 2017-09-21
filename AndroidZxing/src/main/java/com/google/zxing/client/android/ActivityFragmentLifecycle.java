package com.google.zxing.client.android;

import android.os.Bundle;

/**
 * Created by wangkp on 2017/9/15.
 */

public interface ActivityFragmentLifecycle {

    void onCreate(Bundle savedInstanceState);

    void onStart();

    void onRestart();

    void onResume();

    void onPause();

    void onStop();

    void onDestroy();
}
