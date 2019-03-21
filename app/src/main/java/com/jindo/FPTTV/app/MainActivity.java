/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.jindo.FPTTV.app;

import android.app.Activity;
import android.app.Fragment;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.jindo.FPTTV.R;
import com.jindo.FPTTV.TVList.TVListGroupFragment;



/*
 * MainActivity class that loads MainFragment
 */
public class MainActivity extends Activity{

    /**
     * Called when the activity is first created.
     */
    private ProgressBar spinner;
    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        spinner=(ProgressBar)findViewById(R.id.progressBar);
        spinner.setVisibility(View.GONE);

        TVListGroupFragment.registerCallback(new TVListGroupFragment.LoadingProgress() {
            @Override
            public void ProgressLoadingVisible(boolean visible) {
                if(visible == true)
                {

                    spinner.setVisibility(View.VISIBLE);
                }
                else
                {
                   spinner.setVisibility(View.INVISIBLE);
                }
            }
        });
        //spinner.setVisibility(View.VISIBLE);
        try {
            versionChecker VersionChecker = new versionChecker();
            String versionUpdated = VersionChecker.execute().get().toString();
            Log.i("version code is", versionUpdated);


            PackageInfo packageInfo = null;
            try {
                packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            int version_code = packageInfo.versionCode;
            String version_name = packageInfo.versionName;
            Log.i("updated version code", String.valueOf(version_code) + "  " + version_name);
            if (!version_name.equals(versionUpdated)) {
                String packageName = getApplicationContext().getPackageName();//
                UpdateNewVersionDialog updateMeeDialog = new UpdateNewVersionDialog();
                updateMeeDialog.showDialogUpdateNewVersion(MainActivity.this, packageName);
//                Toast.makeText(getApplicationContext(), "please updated", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.getStackTrace();
        }

       if (savedInstanceState == null) {
            Fragment fragment = new TVListGroupFragment();
            getFragmentManager().beginTransaction().replace(android.R.id.content, fragment)
                    .commit();

        }
    }
    @Override public void onBackPressed()
    {
        super.onBackPressed();

        finish();
    }
//    @Override
//    public boolean onKeyUp(int Keycode,KeyEvent event)
//    {
//
//        int key = Keycode;
//        super.onKeyDown(Keycode,event);
//        return true;
//    }
}
