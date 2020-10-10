package com.wuc.voice.view.discovery.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import androidx.viewpager.widget.PagerAdapter;
import com.wuc.lib_image_loader.ImageLoaderManager;
import java.util.ArrayList;

public class BannerPagerAdapter extends PagerAdapter {

  private Context mContext;
  private ArrayList<String> mData;

  public BannerPagerAdapter(Context context, ArrayList<String> list) {
    mContext = context;
    mData = list;
  }

  @Override
  public int getCount() {
    return mData.size();
  }

  @Override
  public View instantiateItem(ViewGroup container, int position) {

    ImageView photoView = new ImageView(mContext);
    photoView.setScaleType(ScaleType.FIT_XY);
    photoView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        //Intent intent = new Intent(mContext, CourseDetailActivity.class);
        //mContext.startActivity(intent);
      }
    });

    ImageLoaderManager.getInstance().displayImageForView(photoView, mData.get(position));
    container.addView(photoView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    return photoView;
  }

  @Override
  public void destroyItem(ViewGroup container, int position, Object object) {
    container.removeView((View) object);
  }

  @Override
  public boolean isViewFromObject(View view, Object object) {
    return view == object;
  }
}
