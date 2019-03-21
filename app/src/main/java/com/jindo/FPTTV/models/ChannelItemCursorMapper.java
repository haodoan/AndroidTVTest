package com.jindo.FPTTV.models;

import android.database.Cursor;
import android.support.v17.leanback.database.CursorMapper;

import com.jindo.FPTTV.data.ChannelContracts;


public class ChannelItemCursorMapper extends CursorMapper {

    private int mChannelName;
    private int mStreamURL;
    private int mLogoURL;
    private int mGroupTitle;
    private int flagGroup;
    private int mType;
    private int mPlugin;

    public ChannelItemCursorMapper() {

        super();

    }

    @Override
    protected void bindColumns(Cursor cursor) {
        mChannelName = cursor.getColumnIndex(ChannelContracts.ItemTable.mChannelName);
        mStreamURL = cursor.getColumnIndex(ChannelContracts.ItemTable.mStreamURL);
        mLogoURL = cursor.getColumnIndex(ChannelContracts.ItemTable.mLogoURL);
        mGroupTitle = cursor.getColumnIndex(ChannelContracts.ItemTable.mGroupTitle);
        flagGroup   = cursor.getColumnIndex(ChannelContracts.ItemTable.flag_groupChanel);

    }
    @Override
    protected Object bind(Cursor cursor) {
        ChannelItem cds = new ChannelItem();
        cds.channelName =    cursor.getString(mChannelName);
        cds.groupTitle =   cursor.getString(mGroupTitle);
        cds.logoURL =  cursor.getString(mLogoURL);
        cds.streamURL =  cursor.getString(mStreamURL);
        cds.flag_groupChanel =  cursor.getInt(flagGroup);


        return cds;
    }
}
