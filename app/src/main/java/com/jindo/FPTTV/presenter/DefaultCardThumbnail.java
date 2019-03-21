package com.jindo.FPTTV.presenter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;


/**
 * CardPresenterで使用するデフォルトのサムネールを取得する
 */
public class DefaultCardThumbnail {
    private Drawable mThumbServer;
    private Drawable mThumbFolder;


    public DefaultCardThumbnail(Context context) {
        Resources resources = context.getResources();
       // mThumbServer = ResourcesCompat.getDrawable(resources, R.drawable.def_thumb_server, null);
       // mThumbFolder = ResourcesCompat.getDrawable(resources, R.drawable.def_thumb_folder, null);

    }


    public Drawable getDeviceThumbnail() {
        return mThumbServer;
    }
}
