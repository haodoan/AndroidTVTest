package com.jindo.LivetvVn.app.page;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.FocusHighlight;

import com.jindo.LivetvVn.M3uParser.M3UItem;
import com.jindo.LivetvVn.M3uParser.M3UParser;
import com.jindo.LivetvVn.cards.presenters.CardPresenterSelector;

import java.util.ArrayList;
import java.util.HashMap;

import android.support.v17.leanback.widget.OnItemViewClickedListener;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.RowPresenter;
import android.support.v17.leanback.widget.VerticalGridPresenter;
import android.widget.Toast;

/**
 * Created by dthao on 12/3/2018.
 */

public class TVlist extends GridFragment {

    private static final int COLUMNS = 6;
    private final int ZOOM_FACTOR = FocusHighlight.ZOOM_FACTOR_LARGE;
    private ArrayObjectAdapter mAdapter;
    private String GroupChanel;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = this.getArguments();
        GroupChanel = args.getString("Header");
        setupAdapter();
        loadData(GroupChanel);
        getMainFragmentAdapter().getFragmentHost().notifyDataReady(getMainFragmentAdapter());
    }

    private void StartMxPlayer(String channelUrl)
    {
        Intent myIntent;
        PackageManager pm = getActivity().getPackageManager();

        Intent intent = new Intent(Intent.ACTION_VIEW);
        try {
            myIntent = pm.getLaunchIntentForPackage("com.mxtech.videoplayer.pro");
            if (null != myIntent) {
                intent.setPackage( "com.mxtech.videoplayer.pro" );
                intent.setData(Uri.parse(channelUrl));
                startActivity(intent);
            }
            else {
                myIntent = pm.getLaunchIntentForPackage("com.mxtech.videoplayer.ad");
                if (null != myIntent) {
                    intent.setPackage( "com.mxtech.videoplayer.ad" );
                    intent.setData(Uri.parse(channelUrl));
                    startActivity(intent);
                } else {
                    Toast.makeText(getActivity(),
                            " Please install MxPlayer before",
                            Toast.LENGTH_LONG).show();

                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + "com.mxtech.videoplayer.ad")));
                }
            }
 
        } catch (ActivityNotFoundException e) {
            Toast.makeText(getActivity(),
                    " Play channel fail: " + e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }
    }

    private void setupAdapter() {
        VerticalGridPresenter presenter = new VerticalGridPresenter(ZOOM_FACTOR);
        presenter.setNumberOfColumns(COLUMNS);

        setGridPresenter(presenter);

        CardPresenterSelector cardPresenter = new CardPresenterSelector(getActivity());
        mAdapter = new ArrayObjectAdapter(cardPresenter);
        setAdapter(mAdapter);

        setOnItemViewClickedListener(new OnItemViewClickedListener() {
            @Override
            public void onItemClicked(
                    Presenter.ViewHolder itemViewHolder,
                    Object item,
                    RowPresenter.ViewHolder rowViewHolder,
                    Row row) {
                M3UItem card = (M3UItem)item;
                Toast.makeText(getActivity(),
                        "Clicked on "+ card.getStreamURL(),
                        Toast.LENGTH_LONG).show();

                StartMxPlayer(card.getStreamURL());
            }
        });
    }


    private void loadData(String groupChanel) {

        HashMap<String, ArrayList<M3UItem>> map;

        if(groupChanel.equals("All Channel"))
        {
            map = M3UParser.getInstance().GetAllChannel();
        }
        else {
            map = M3UParser.getInstance().GetChannelListInGroup();
        }

        ArrayList<M3UItem> list = map.get(groupChanel);

        if(list != null)
        {
            mAdapter.addAll(0,list);
        }

    }
}
