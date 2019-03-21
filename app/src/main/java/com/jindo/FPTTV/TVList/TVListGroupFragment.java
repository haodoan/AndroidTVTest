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

package com.jindo.FPTTV.TVList;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v17.leanback.app.BrowseFragment;

import com.jindo.FPTTV.M3uParser.M3UParser;

import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.CursorObjectAdapter;
import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v17.leanback.widget.OnItemViewClickedListener;
import android.support.v17.leanback.widget.PageRow;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.RowPresenter;
import android.util.Log;

import com.google.gson.Gson;
import com.jindo.FPTTV.R;
import com.jindo.FPTTV.data.ChannelContracts;
import com.jindo.FPTTV.data.ChannelProvider;
import com.jindo.FPTTV.models.ChannelItem;
import com.jindo.FPTTV.models.ChannelItemCursorMapper;
import com.jindo.FPTTV.presenter.GroupChannelCardPresenter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Sample {@link BrowseFragment} implementation showcasing the use of {@link PageRow} and
 * {@link ListRow}.
 */
public class TVListGroupFragment extends BrowseFragment {
    private ArrayObjectAdapter mRowsAdapter;
    private CursorObjectAdapter groupListAdapter;

    private String dataContent = null;

    M3UParser m3UParser;



    public interface LoadingProgress
    {
        void ProgressLoadingVisible(boolean visible);
    }
    private  static LoadingProgress listener;
    public static void registerCallback(LoadingProgress callback)
    {
        listener = callback;
    }

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
                        m3UParser.parse(lines);
                        listener.ProgressLoadingVisible(false);
                        createRows();
                    }
                    });
                } catch (final IllegalArgumentException e) {
                    Log.d("Tag", "ERROR onResponse");
                }
            }
        });
    }


    private LoaderManager.LoaderCallbacks<? extends Cursor> groupListLoaderCallbacks = new LoaderManager.LoaderCallbacks<Cursor>() {
        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {

            Uri uri  = ChannelContracts.ItemTable.getContentUri(ChannelProvider.AUTHORITY);
            String selection = ChannelContracts.ItemTable.flag_groupChanel + " = 1";
            String[] selectionArgs = {"=1"};
            String sortOrder = null;
            // サーバー一覧をソートする場合はsortOrderを指定する。
            // String sortOrder = UpnpDeviceContracts.UpnpDeviceEntry.FRIENDLY_NAME;
            String[] projection = new String[] { ChannelContracts.ItemTable.mGroupTitle };
            CursorLoader loader = new CursorLoader(getActivity(),
                    uri,
                    null,
                    selection,
                    null,
                    sortOrder);
            loader.setUpdateThrottle(500);
            return loader;
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

            groupListAdapter.swapCursor(data);

        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            groupListAdapter.swapCursor(null);
        }
    };

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



        listener.ProgressLoadingVisible(true);

        setupUi();

        m3UParser = new M3UParser(getActivity(),ChannelProvider.AUTHORITY);

            new Thread(){
                @Override
               public  void run()
                {
                    String json = getNetworkName();
                    ISPinfo data = new Gson().fromJson(json, ISPinfo.class);

                    String ispName = data.getIspName();

                    String urlIPTV;
                    switch ("FPTDYNAMICIP")
                    {
                        case "FPTDYNAMICIP":
                            urlIPTV = "http://bit.ly/ListFpt";
                            break;
//                        case "VNPT":
//                        case "VIETTEL":
//                            urlIPTV = "http://gg.gg/8zy29";
//                            break;
                        default:
                            //urlIPTV = "http://gg.gg/8zy29";
                            return;
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

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setupEventListeners();

        startEntranceTransition();
    }

    private void setupUi() {

       setHeadersState(HEADERS_DISABLED);
       setHeadersTransitionOnBackEnabled(true);
     //   setBrandColor(getResources().getColor(android.R.color.holo_green_dark));
      //  setTitle("SERVERS");
        prepareEntranceTransition();
    }


    private void createRows() {


        mRowsAdapter = new ArrayObjectAdapter(new ListRowPresenter());

        int i = 0;

        HeaderItem serverHeader = new HeaderItem(i, getString(R.string.Group_Lists));
        groupListAdapter = new CursorObjectAdapter(new GroupChannelCardPresenter());
        groupListAdapter.setMapper(new ChannelItemCursorMapper());
        mRowsAdapter.add(new ListRow(serverHeader, groupListAdapter));

        setAdapter(mRowsAdapter);


        getLoaderManager().initLoader(0, null, groupListLoaderCallbacks);

    }


    private void setupEventListeners() {
        setOnItemViewClickedListener(new ItemViewClickedListener());
    }

    private final class ItemViewClickedListener implements OnItemViewClickedListener {
        @Override
        public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item,
                                  RowPresenter.ViewHolder rowViewHolder, Row row) {

            if (item instanceof ChannelItem)
            {
                Intent intent = new Intent(getActivity(), TVListGroupActivity.class);
                intent.putExtra("GROUP_LIST", ((ChannelItem) item).groupTitle);
                getActivity().startActivity(intent);
            }
        }
    }


}
