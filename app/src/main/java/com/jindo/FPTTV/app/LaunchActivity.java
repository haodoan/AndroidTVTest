package com.jindo.FPTTV.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class LaunchActivity  extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
