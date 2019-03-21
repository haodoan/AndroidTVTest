package com.jindo.FPTTV.TVList;

import com.google.gson.annotations.SerializedName;

/**
 * Created by dthao on 12/28/2018.
 */

public class ISPinfo {
    @SerializedName("as") private String mAs = "";
    @SerializedName("city") private String mText = "";
    @SerializedName("country") private String mCountry = null;
    @SerializedName("countryCode") private String mCountryCode = null;
    @SerializedName("isp") private String mIspName = null;
    @SerializedName("lat") private String mlat = null;
    @SerializedName("lon") private String mLom = null;
    @SerializedName("org") private String morg = null;
    @SerializedName("query") private String mip = null;
    @SerializedName("region") private String mRegion = null;
    @SerializedName("regionName") private String mRegionCode = null;
    @SerializedName("status") private String mStatus = null;
    @SerializedName("timezone") private String mTimezone = null;
    @SerializedName("zip") private String mZip = null;

    public String getIspName() {
        return mIspName;
    }
}
