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

package com.jindo.LivetvVn.app;

import android.app.Activity;
import android.app.Fragment;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.KeyEvent;

import com.jindo.LivetvVn.R;
import com.jindo.LivetvVn.app.page.PageAndListRowFragment;

/*
 * MainActivity class that loads MainFragment
 */
public class MainActivity extends Activity{


    private static Resources res;
    /**
     * Called when the activity is first created.
     */
    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        res = getResources();
       if (savedInstanceState == null) {
            Fragment fragment = new PageAndListRowFragment();
            getFragmentManager().beginTransaction().replace(android.R.id.content, fragment)
                    .commit();
        }

    }

    public static Resources getResourses() {
        return res;
    }


}
