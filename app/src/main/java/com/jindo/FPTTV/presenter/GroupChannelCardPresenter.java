/*
 * Copyright (C) 2014 The Android Open Source Project
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

package com.jindo.FPTTV.presenter;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.v17.leanback.widget.ImageCardView;
import android.support.v17.leanback.widget.Presenter;
import android.support.v4.content.res.ResourcesCompat;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.jindo.FPTTV.R;
import com.jindo.FPTTV.models.ChannelItem;

/*
 * A CardPresenter is used to generate Views and bind Objects to them on demand.
 * It contains an Image CardView
 */
public class GroupChannelCardPresenter extends Presenter {
    private static final String TAG = GroupChannelCardPresenter.class.getSimpleName();

    private int mSelectedBackgroundColor = -1;
    private int mDefaultBackgroundColor = -1;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent) {
        Resources resources = parent.getResources();
        mDefaultBackgroundColor = ResourcesCompat.getColor(resources, R.color.card_default_background, null);
        mSelectedBackgroundColor = ResourcesCompat.getColor(resources, R.color.card_selected_background, null);
      //  defaultCardThumbnail = new DefaultCardThumbnail(parent.getContext());

        ImageCardView cardView = new ImageCardView(parent.getContext()) {
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
        int color = selected ? mSelectedBackgroundColor : mDefaultBackgroundColor;

        // Both background colors should be set because the view's
        // background is temporarily visible during animations.
        view.setBackgroundColor(color);
        view.findViewById(R.id.info_field).setBackgroundColor(color);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Object item) {
        ChannelItem groupItem = (ChannelItem) item;

        ImageCardView cardView = (ImageCardView) viewHolder.view;
        cardView.setTitleText(groupItem.groupTitle);

        Resources res = cardView.getResources();
        int width = res.getDimensionPixelSize(R.dimen.card_width);
        int height = res.getDimensionPixelSize(R.dimen.card_height);
        cardView.setMainImageDimensions(width, height);


        Drawable localIcon;
        switch (((ChannelItem) item).groupTitle)
        {
            case "VTV-VTVCab":
                localIcon = ResourcesCompat.getDrawable(res, R.drawable.vtv, null);
                cardView.getMainImageView().setImageDrawable(localIcon);
                break;
            case "HTV-HTVC" :
                localIcon = ResourcesCompat.getDrawable(res, R.drawable.htv, null);
                cardView.getMainImageView().setImageDrawable(localIcon);
                break;
            case "VTC" :
                localIcon = ResourcesCompat.getDrawable(res, R.drawable.vtc, null);
                cardView.getMainImageView().setImageDrawable(localIcon);
                break;
            case "SCTV" :
                localIcon = ResourcesCompat.getDrawable(res, R.drawable.sctv, null);
                cardView.getMainImageView().setImageDrawable(localIcon);
                break;
            case "Kênh quốc tế" :
                localIcon = ResourcesCompat.getDrawable(res, R.drawable.quocte, null);
                cardView.getMainImageView().setImageDrawable(localIcon);
                break;
            case "Hanoicab" :
                localIcon = ResourcesCompat.getDrawable(res, R.drawable.hanoicab, null);
                cardView.getMainImageView().setImageDrawable(localIcon);
                break;
            default:
                Glide.with(viewHolder.view.getContext())
                        .load(groupItem.logoURL)
                        .into(cardView.getMainImageView());
                break;
        }

    }

    @Override
    public void onUnbindViewHolder(ViewHolder viewHolder) {
        ImageCardView cardView = (ImageCardView) viewHolder.view;

        // Remove references to images so that the garbage collector can free up memory.
        cardView.setBadgeImage(null);
        cardView.setMainImage(null);
    }
}
