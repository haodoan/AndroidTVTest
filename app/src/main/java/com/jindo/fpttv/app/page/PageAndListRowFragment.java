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

package com.jindo.fpttv.app.page;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v17.leanback.app.BackgroundManager;
import android.support.v17.leanback.app.BrowseFragment;
import android.support.v17.leanback.app.RowsFragment;

import com.jindo.fpttv.M3uParser.M3UFile;
import com.jindo.fpttv.M3uParser.M3UItem;
import com.jindo.fpttv.M3uParser.M3UToolSet;
import com.jindo.fpttv.R;
import com.jindo.fpttv.models.Card;
import com.jindo.fpttv.models.CardRow;
import com.jindo.fpttv.utils.CardListRow;
import com.jindo.fpttv.utils.Utils;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v17.leanback.widget.PageRow;
import android.support.v17.leanback.widget.Row;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Sample {@link BrowseFragment} implementation showcasing the use of {@link PageRow} and
 * {@link ListRow}.
 */
public class PageAndListRowFragment extends BrowseFragment {

    private static final long HEADER_ID_1 = 0;
    private static final String HEADER_NAME_1 = "Servers";

    private static final long HEADER_ID_2 = 1;
    private static final String HEADER_NAME_2 = "Setting";
    private BackgroundManager mBackgroundManager;

    private ArrayObjectAdapter mRowsAdapter;

    private static Activity mActivity;

    public ArrayList<String>  TVGroupList;

    private URL url;

    public M3UFile m3ufile;

    public boolean onnBackPressed() {
        return false;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (activity == null)
            return;
      mActivity = activity;

    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try{
            url = new URL("https://textuploader.com/d1r89/raw");
        }catch(MalformedURLException e) {
            System.out.println("The url is not well formed: " + url);
        }
        new Thread()  {
            @Override
            public void run() {
                try {
                    URLConnection con = url.openConnection();
                    InputStream in = con.getInputStream();
                    String encoding = con.getContentEncoding();  // ** WRONG: should use "con.getContentType()" instead but it returns something like "text/html; charset=UTF-8" so this value must be parsed to extract the actual encoding
                    encoding = encoding == null ? "UTF-8" : encoding;
                    String body = IOUtils.toString(in, encoding);
                    String[] lines = body.split(System.getProperty("line.separator"));
                    // getIptvParam(lines);
                    m3ufile = M3UToolSet.load(lines);
                    Log.d("TAG",body);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();

        setupUi();
        loadData();
        mBackgroundManager = BackgroundManager.getInstance(getActivity());
        mBackgroundManager.attach(getActivity().getWindow());

        getMainFragmentRegistry().registerFragment(PageRow.class,
                new PageRowFragmentFactory(mBackgroundManager));

    }

    private void setupUi() {
        setHeadersState(HEADERS_ENABLED);
        setHeadersTransitionOnBackEnabled(true);
        setBrandColor(getResources().getColor(R.color.fastlane_background));
      //  setTitle("SERVERS");


        prepareEntranceTransition();


    }

    private void loadData() {
        mRowsAdapter = new ArrayObjectAdapter(new ListRowPresenter());
        setAdapter(mRowsAdapter);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                createRows();
                startEntranceTransition();
            }
        }, 2000);
    }

    private void getTVGroupList(ArrayList<M3UItem> Items)
    {
        TVGroupList = new ArrayList<String>();
        for(M3UItem item : Items)
        {
            if(item.getGroupTitle() != null)
            {
                if(!TVGroupList.contains(item.getGroupTitle()))
                {
                    TVGroupList.add(item.getGroupTitle());
                }

            }
        }
    }
    private void createRows() {

        int i=0;
        getTVGroupList(m3ufile.getItems());
        Iterator itr=TVGroupList.iterator();

//        HeaderItem headerItem1 = new HeaderItem(HEADER_ID_1, HEADER_NAME_1);
//        PageRow pageRow1 = new PageRow(headerItem1);
//        mRowsAdapter.add(pageRow1);

        while(itr.hasNext()){
            String tmpString = (String) itr.next();
            HeaderItem headerItem2 = new HeaderItem(i++, tmpString);
            PageRow pageRow2 = new PageRow(headerItem2);
            mRowsAdapter.add(pageRow2);
            Log.d("TAG",(String) tmpString);
        }



    }

    private class PageRowFragmentFactory extends BrowseFragment.FragmentFactory {
        private final BackgroundManager mBackgroundManager;

        PageRowFragmentFactory(BackgroundManager backgroundManager) {
            this.mBackgroundManager = backgroundManager;
        }

        @Override
        public Fragment createFragment(Object rowObj) {
            Row row = (Row)rowObj;
            mBackgroundManager.setDrawable(null);

            if (row.getHeaderItem().getId() == HEADER_ID_1) {

                FragmentManager fragmentManager = getFragmentManager();
                TVlist mFragment = (TVlist)fragmentManager.findFragmentByTag("task");

                if(mFragment == null)
                {
                    mFragment = new TVlist();
                    getFragmentManager().beginTransaction().add(mFragment, "task").commit();
                }

             return mFragment;
            }
            else if (row.getHeaderItem().getId() == HEADER_ID_2) {
                //return new SettingsFragment();
            }

            throw new IllegalArgumentException(String.format("Invalid row %s", rowObj));
        }
    }

    public static class PageFragmentAdapterImpl extends MainFragmentAdapter<TVlist> {

        public PageFragmentAdapterImpl(TVlist fragment) {
            super(fragment);
        }
    }

    public static class SettingsFragment extends RowsFragment {
        private final ArrayObjectAdapter mRowsAdapter;

        public SettingsFragment() {
            ListRowPresenter selector = new ListRowPresenter();
            selector.setNumRows(2);
            mRowsAdapter = new ArrayObjectAdapter(selector);
            setAdapter(mRowsAdapter);
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    loadData();
//                }
//            }, 200);
        }

        private void loadData() {

        }

        private ListRow createCardRow(CardRow cardRow) {

           return null;
        }
    }

}
