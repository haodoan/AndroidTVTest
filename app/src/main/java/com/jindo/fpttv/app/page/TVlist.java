package com.jindo.fpttv.app.page;

import android.app.Activity;
import android.os.Bundle;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.FocusHighlight;
import android.util.Log;

import com.google.gson.Gson;
import com.jindo.fpttv.M3uParser.M3UFile;
import com.jindo.fpttv.M3uParser.M3UToolSet;
import com.jindo.fpttv.R;
import com.jindo.fpttv.cards.presenters.CardPresenterSelector;
import com.jindo.fpttv.models.Card;
import com.jindo.fpttv.models.CardRow;
import com.jindo.fpttv.utils.Utils;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import android.support.v17.leanback.app.BackgroundManager;
import android.support.v17.leanback.app.BrowseFragment;
import android.support.v17.leanback.app.RowsFragment;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.FocusHighlight;
import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v17.leanback.widget.OnItemViewClickedListener;
import android.support.v17.leanback.widget.PageRow;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.PresenterSelector;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.RowPresenter;
import android.support.v17.leanback.widget.VerticalGridPresenter;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

/**
 * Created by dthao on 12/3/2018.
 */

public class TVlist extends GridFragment {

    private static final int COLUMNS = 4;
    private final int ZOOM_FACTOR = FocusHighlight.ZOOM_FACTOR_SMALL;
    private ArrayObjectAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupAdapter();
        loadData();
        getMainFragmentAdapter().getFragmentHost().notifyDataReady(getMainFragmentAdapter());
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
                Card card = (Card)item;
                Toast.makeText(getActivity(),
                        "Clicked on "+card.getTitle(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadData() {
        String json = Utils.inputStreamToString(getResources().openRawResource(
                R.raw.grid_example));
        CardRow cardRow = new Gson().fromJson(json, CardRow.class);
        mAdapter.addAll(0, cardRow.getCards());
    }
}
