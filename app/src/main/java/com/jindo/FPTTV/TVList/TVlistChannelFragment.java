package com.jindo.FPTTV.TVList;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v17.leanback.app.VerticalGridFragment;
import android.support.v17.leanback.widget.CursorObjectAdapter;
import android.support.v17.leanback.widget.FocusHighlight;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.jindo.FPTTV.BuildConfig;
import com.jindo.FPTTV.R;
import com.jindo.FPTTV.data.ChannelContracts;
import com.jindo.FPTTV.data.ChannelProvider;
import com.jindo.FPTTV.models.ChannelItem;
import com.jindo.FPTTV.models.ChannelItemCursorMapper;
import com.jindo.FPTTV.presenter.ChannelCardPresenter;

import android.support.v17.leanback.widget.OnItemViewClickedListener;
import android.support.v17.leanback.widget.OnItemViewSelectedListener;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.RowPresenter;
import android.support.v17.leanback.widget.VerticalGridPresenter;
import android.widget.Toast;

/**
 * Created by dthao on 12/3/2018.
 */

public class TVlistChannelFragment extends VerticalGridFragment {

    private static final int COLUMNS = 6;
    private final int ZOOM_FACTOR = FocusHighlight.ZOOM_FACTOR_LARGE;
    String Url;
    String Chanel;
    AdRequest adRequest;
    private static long click_cnt = 0;

    InterstitialAd mInterstitialAd;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        click_cnt = 0;
        MobileAds.initialize(getActivity(),
                getActivity().getString(R.string.APP_ID));//ca-app-pub-3940256099942544~3347511713
        mInterstitialAd = new InterstitialAd(getActivity());

        // set the ad unit ID
        mInterstitialAd.setAdUnitId(getActivity().getString(R.string.APP_UNITS));//ca-app-pub-3940256099942544/1033173712

        if(BuildConfig.DEBUG) {
            adRequest = new AdRequest.Builder()
                    .addTestDevice("34AC4DDDBE9C1BBF20BF16919B21AD7D")
                    .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                    .build();
        }
        else {
            adRequest = new AdRequest.Builder()
                    .build();
        }
        mInterstitialAd.loadAd(adRequest);



//        mInterstitialAd.setAdListener(new AdListener() {
//            public void onAdLoaded() {
//                showInterstitial();
//            }
//            @Override
//            public void onAdClosed() {
////                StartMxPlayer(Url,Chanel);
//            }
//        });

        setupFragment();

        Bundle args = this.getArguments();
        String GroupList = args.getString("GROUP_LIST");

        channelListAdapter = new CursorObjectAdapter(new ChannelCardPresenter(getActivity()));
        channelListAdapter.setMapper(new ChannelItemCursorMapper());
        setAdapter(channelListAdapter);

        getLoaderManager().initLoader(0, args, channelListLoaderCallbacks);

        setupAdapter();

    }


    private CursorObjectAdapter channelListAdapter;

    private LoaderManager.LoaderCallbacks<? extends Cursor> channelListLoaderCallbacks = new LoaderManager.LoaderCallbacks<Cursor>() {
        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {

            String GroupList = args.getString("GROUP_LIST");
            Uri uri  = ChannelContracts.ItemTable.getContentUri(ChannelProvider.AUTHORITY);
            String selection = ChannelContracts.ItemTable.mGroupTitle + " = ?";
            String[] selectionArgs = {GroupList};
            String sortOrder = null;
            // String sortOrder = UpnpDeviceContracts.UpnpDeviceEntry.FRIENDLY_NAME;
            CursorLoader loader = new CursorLoader(getActivity(),
                    uri,
                    null,
                    selection,
                    selectionArgs,
                    sortOrder);
            loader.setUpdateThrottle(500);
            return loader;
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            channelListAdapter.swapCursor(data);
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            channelListAdapter.swapCursor(null);
        }
    };


    private void showInterstitial() {
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        }
    }
    private void StartMxPlayer(String channelUrl, String ChannelName)
    {
        //final String MXPackageName = "com.mxtech.videoplayer.ad";
        final String MXPackageName = "org.videolan.vlc";
        Intent myIntent;

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());

        PackageManager pm = getActivity().getPackageManager();

        Intent intent = new Intent(Intent.ACTION_VIEW);
        try {
            //myIntent = pm.getLaunchIntentForPackage("com.mxtech.videoplayer.pro");
            myIntent = pm.getLaunchIntentForPackage(MXPackageName);
            if (null != myIntent) {
                //intent.setPackage( "com.mxtech.videoplayer.pro" );
                //intent.setPackage( MXPackageName );
                intent.setComponent(new ComponentName(MXPackageName, "org.videolan.vlc.gui.video.VideoPlayerActivity"));
                intent.setData(Uri.parse(channelUrl));
                intent.putExtra("title",ChannelName);
                startActivityForResult(intent,10);
            }
            else
            {
                alertDialogBuilder.setMessage("Bạn cần phải cài đặt ứng dụng VLC player");
                alertDialogBuilder.setPositiveButton("Có",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + MXPackageName)));
                            }
                        });

                alertDialogBuilder.setNegativeButton("Không",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getActivity(),"Không cài đặt VLC Player",Toast.LENGTH_LONG).show();
                    }
                });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
            /*else {
                myIntent = pm.getLaunchIntentForPackage(MXPackageName);
                if (null != myIntent) {
                    intent.setPackage(MXPackageName);
                } else {
                    alertDialogBuilder.setMessage("Bạn cần phải cài đặt ứng dụng MX player");
                    alertDialogBuilder.setPositiveButton("Có",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface arg0, int arg1) {
                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + MXPackageName)));
                                }
                            });

                    alertDialogBuilder.setNegativeButton("Không",new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(getActivity(),"Không cài đặt MX Player",Toast.LENGTH_LONG).show();
                        }
                    });

                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();

                }
            }*/
        } catch (ActivityNotFoundException e) {
//            Toast.makeText(getActivity(),
//                    " Play channel fail: " + e.getMessage(),
//                    Toast.LENGTH_LONG).show();
        }
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);

       if (data.getAction().equals("org.videolan.vlc.player.result") && requestCode == 10)
        {
            //String cause = data.getStringExtra("end_by"); //  Indicates reason of activity closure.
            //Toast.makeText(getActivity(), "Reason: "+  cause,Toast.LENGTH_LONG).show();
            if(click_cnt % 2 == 0) {
                RunAds();
            }
            else
            {
                if(!mInterstitialAd.isLoaded())
                {
                    mInterstitialAd.loadAd(adRequest);
                }

            }
            click_cnt++;
        }
    }

    public void RunAds()
    {
        showInterstitial();
    }
    private void setupAdapter() {
        VerticalGridPresenter presenter = new VerticalGridPresenter(ZOOM_FACTOR);
        presenter.setNumberOfColumns(COLUMNS);

        setGridPresenter(presenter);

    }


    private void setupFragment() {
        VerticalGridPresenter gridPresenter = new VerticalGridPresenter();
        gridPresenter.setNumberOfColumns(COLUMNS);
        setGridPresenter(gridPresenter);

        setOnItemViewClickedListener(new ItemViewClickedListener());
        setOnItemViewSelectedListener(new ItemViewSelectedListener());
    }

    private final class ItemViewClickedListener implements OnItemViewClickedListener {
        @Override
        public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item,
                                  RowPresenter.ViewHolder rowViewHolder, Row row) {

            ChannelItem card = (ChannelItem)item;
            Url = card.streamURL;
            Chanel = card.channelName;
            StartMxPlayer(Url,Chanel);

        }
    }
    private final class ItemViewSelectedListener implements OnItemViewSelectedListener {
        @Override
        public void onItemSelected(Presenter.ViewHolder itemViewHolder, Object item,
                                   RowPresenter.ViewHolder rowViewHolder, Row row) {
        }
    }
}
