/*
 * Copyright (C) 2015 The Android Open Source Project
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

import android.content.Intent;
import android.os.Bundle;
import android.support.v17.leanback.app.BrowseFragment;

import com.jindo.FPTTV.TVList.TVListGroupActivity;
import com.jindo.FPTTV.R;

import android.support.v17.leanback.widget.OnItemViewClickedListener;
import android.support.v17.leanback.widget.OnItemViewSelectedListener;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.RowPresenter;
import android.support.v4.app.ActivityOptionsCompat;


public class MainFragment extends BrowseFragment {

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setupUIElements();
        //setupEventListeners();
    }



    private void setupUIElements() {
        setTitle(getString(R.string.browse_title));
       // setBadgeDrawable(getResources().getDrawable(R.drawable.title_android_tv, null));
        setHeadersState(HEADERS_DISABLED);
        setHeadersTransitionOnBackEnabled(false);
        setBrandColor(getResources().getColor(R.color.fastlane_background));
    }

    private void setupEventListeners() {
        setOnItemViewClickedListener(new ItemViewClickedListener());
        setOnItemViewSelectedListener(new ItemViewSelectedListener());
    }

    private final class ItemViewClickedListener implements OnItemViewClickedListener {

        @Override
        public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item,
                                  RowPresenter.ViewHolder rowViewHolder, Row row) {
            Intent intent = null;
            int id =0;
            switch (id) {
                case 0: {

                    break;
                }
                case 1:
                    intent = new Intent(getActivity().getBaseContext(),
                            TVListGroupActivity.class);
                    break;
                case 2: {

                    break;
                }
                case 3: {

                    break;
                }
                case 4: {


                    break;
                }
                case 5: {

                    break;
                }
                case 6: {

                    break;
                }
                case 7: {

                    break;
                }
                case 8: {
                    break;
                }
                case 9: {
                    // Let's create a new Wizard for a given Movie. The movie can come from any sort
                    // of data source. To simplify this example we decode it from a JSON source
                    // which might be loaded from a server in a real world example.

                    // Prepare extras which contains the Movie and will be passed to the Activity
                    // which is started through the Intent/.
                    Bundle extras = new Bundle();
                   // String json = Utils.inputStreamToString(
                     //       getResources().openRawResource(R.raw.wizard_example));
                   // Movie movie = new Gson().fromJson(json, Movie.class);
                   // extras.putSerializable("movie", movie);
                    intent.putExtras(extras);

                    // Finally, start the wizard Activity.
                    break;
                }
                case 10: {

                    return;
                }
                case 11: {

                    break;
                }
                case 12: {
                    return;
                }
                case 13: {
                    startActivity(intent);
                    return;
                }
                default:
                    break;
            }
            if (intent != null) {
                Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity())
                        .toBundle();
                startActivity(intent, bundle);
            }
        }
    }

    private final class ItemViewSelectedListener implements OnItemViewSelectedListener {

        @Override
        public void onItemSelected(Presenter.ViewHolder itemViewHolder, Object item,
                                   RowPresenter.ViewHolder rowViewHolder, Row row) {
        }
    }
}
