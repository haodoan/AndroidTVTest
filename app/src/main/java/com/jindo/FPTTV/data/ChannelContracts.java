package com.jindo.FPTTV.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * CDSブラウズ結果DBのカラム、テーブル、アクセスURIの定義。
 */
public class ChannelContracts {


    private static Uri createContentUri(String authority, String table) {
        return new Uri.Builder().scheme("content").authority(authority)
                .path(table).build();
    }

    public interface ItemColumns {
        /**
         * The channel name.
         */
        String mChannelName = "mChannelName";
        /**
         * The stream duration time, it's unit is second.
         */
        int mDuration = 0;
        /**
         * The stream url.
         */
        String mStreamURL = "mStreamURL";
        /**
         * The url to the logo icon.
         */
        String mLogoURL = "mLogoURL";
        /**
         * The group name.
         */
        String mGroupTitle= "mGroupTitle";

        String flag_groupChanel = "flag_groupChanel";
        /**
         * The media type. It can be one of the following types: avi, asf, wmv, mp4,
         * mpeg, mpeg1, mpeg2, ts, mp2t, mp2p, mov, mkv, 3gp, flv, aac, ac3, mp3,
         * ogg, wma.
         */
        String mType = "mType";

    }


    public static final class ItemTable implements ItemColumns, BaseColumns {
        static final String PATH = "item";

        public static Uri getContentUri(String authority) {
            return createContentUri(authority, PATH);
        }
    }
}
