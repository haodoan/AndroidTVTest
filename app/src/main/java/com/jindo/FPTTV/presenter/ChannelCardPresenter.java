package com.jindo.FPTTV.presenter;

import android.content.Context;
import android.content.res.Resources;
import android.support.v17.leanback.widget.ImageCardView;
import android.support.v17.leanback.widget.Presenter;
import android.support.v4.content.ContextCompat;
import android.view.ContextThemeWrapper;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.jindo.FPTTV.R;
import com.jindo.FPTTV.models.ChannelItem;


public class ChannelCardPresenter extends Presenter {
    private static final String TAG = ChannelCardPresenter.class.getSimpleName();

    private final int selectedBackgroundColor;
    private final int defaultBackgroundColor;
    private final ContextThemeWrapper contextWrapper;
    //private final DefaultCardThumbnail defaultCardThumbnail;

    public ChannelCardPresenter(Context context) {
        defaultBackgroundColor = ContextCompat.getColor(context, R.color.card_default_background);
        selectedBackgroundColor = ContextCompat.getColor(context, R.color.card_selected_background);
        contextWrapper = new ContextThemeWrapper(context, R.style.CustomImageCardViewStyle);
      //  defaultCardThumbnail = new DefaultCardThumbnail(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent) {

        ImageCardView cardView = new ImageCardView(contextWrapper) {
            @Override
            public void setSelected(boolean selected) {
                updateCardBackgroundColor(this, selected);
                super.setSelected(selected);
            }
        };

        cardView.setFocusable(true);
        cardView.setFocusableInTouchMode(true);
        cardView.setMainImageScaleType(ImageView.ScaleType.FIT_CENTER);

        updateCardBackgroundColor(cardView, false);
        return new ViewHolder(cardView);
    }

    private void updateCardBackgroundColor(ImageCardView view, boolean selected) {
        int color = selected ? selectedBackgroundColor : defaultBackgroundColor;

        // Both background colors should be set because the view's
        // background is temporarily visible during animations.
        view.setBackgroundColor(color);
        view.findViewById(R.id.info_field).setBackgroundColor(color);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Object item) {
        ChannelItem channelItem = (ChannelItem) item;
        ImageCardView cardView = (ImageCardView) viewHolder.view;

        cardView.setTitleText(channelItem.channelName);

        Resources res = cardView.getResources();




        int width = res.getDimensionPixelSize(R.dimen.small_card_width);
        int height = res.getDimensionPixelSize(R.dimen.small_card_height);

        cardView.setMainImageDimensions(width, height);
        switch (((ChannelItem) item).channelName)
        {
            case "VTV2 HD":
                channelItem.logoURL = "https://upload.wikimedia.org/wikipedia/commons/7/7b/VTV2.png";
                break;
            case "VTV3 HD":
                channelItem.logoURL = "https://upload.wikimedia.org/wikipedia/vi/f/fc/VTV3HD.png";
                break;
            case "Today TV - VTC7":
                channelItem.logoURL = "http://mobion.vn/Files/Image/admin/2017/12/08/temp_621947320104103.jpg";
                break;
        }
        // Set card size from dimension resources.
        Glide.with(viewHolder.view.getContext())
                    .load(channelItem.logoURL)
                    .into(cardView.getMainImageView());
    }

    @Override
    public void onUnbindViewHolder(ViewHolder viewHolder) {
        ImageCardView cardView = (ImageCardView) viewHolder.view;
        // Remove references to images so that the garbage collector can free up memory
        cardView.setBadgeImage(null);
        cardView.setMainImage(null);
    }
}
