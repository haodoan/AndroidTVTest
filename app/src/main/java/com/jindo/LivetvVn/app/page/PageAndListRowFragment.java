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

package com.jindo.LivetvVn.app.page;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.support.v17.leanback.app.BackgroundManager;
import android.support.v17.leanback.app.BrowseFragment;
import android.support.v17.leanback.app.RowsFragment;

import com.jindo.LivetvVn.M3uParser.M3UFile;
import com.jindo.LivetvVn.M3uParser.M3UItem;
import com.jindo.LivetvVn.M3uParser.M3UToolSet;
import com.jindo.LivetvVn.models.CardRow;

import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v17.leanback.widget.PageRow;
import android.support.v17.leanback.widget.Row;
import android.util.Log;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

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

    private HashMap<Integer , String> map = new HashMap<>();

    private String dataContent = null;


    void IptvPostRequest(String postUrl) throws IOException {

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(postUrl)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                call.cancel();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    dataContent = response.body().string();
                    getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run()
                    {
                        String[] lines = dataContent.split(System.getProperty("line.separator"));

                        m3ufile = M3UToolSet.load(lines);

                        setupUi();

                        loadData();

                        mBackgroundManager = BackgroundManager.getInstance(
                                getActivity());
                        mBackgroundManager.attach(getActivity().getWindow());
                        getMainFragmentRegistry().registerFragment(PageRow.class, new PageRowFragmentFactory(mBackgroundManager));

                    }
                    });
                } catch (final IllegalArgumentException e) {
                    Log.d("Tag", "ERROR onResponse");
                }
            }
        });
    }

    public String getNetworkName()
    {
        String fullString = "";
       try{
           URL url = new URL("http://ip-api.com/json");
           BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
           String line;
           while ((line = reader.readLine()) != null) {
               fullString += line;
           }
           reader.close();

        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
       return  fullString;

    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

            new Thread(){
                @Override
               public  void run()
                {
                    String json = getNetworkName();
                    ISPinfo data = new Gson().fromJson(json, ISPinfo.class);

                    String ispName = data.getIspName();

                    String urlIPTV;
                    switch (ispName)
                    {
                        case "FPTDYNAMICIP":
                            urlIPTV = "http://bit.ly/ListFpt";
                            break;
                        case "VNPT":
                        case "VIETTEL":
                            urlIPTV = "http://gg.gg/8zy29";
                            break;
                        default:
                            urlIPTV = "http://gg.gg/8zy29";
                    }

                    try {
                        IptvPostRequest(urlIPTV);
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }.start();

    }

    private void setupUi() {
        setHeadersState(HEADERS_ENABLED);
        setHeadersTransitionOnBackEnabled(true);
        setBrandColor(getResources().getColor(android.R.color.holo_green_dark));
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

        map.put(i,"All Channel");
        HeaderItem headerItem = new HeaderItem(i++, "All Channel");
        PageRow pageRow = new PageRow(headerItem);
        mRowsAdapter.add(pageRow);

        while(itr.hasNext()){
            String tmpString = (String) itr.next();
            map.put(i,tmpString);
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
            Fragment mFragment = new TVlist();
            Bundle args = new Bundle();
            int id = (int) row.getHeaderItem().getId();
            String GroupChannel = map.get(id);
            args.putString("Header", GroupChannel);

            mFragment.setArguments(args);


             return mFragment;
          //  throw new IllegalArgumentException(String.format("Invalid row %s", rowObj));
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
