package com.wuc.ft_home.view.discovery;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import com.wuc.ft_home.R;

public class DiscoveryFunctionView extends RelativeLayout {
  private Context mContext;

  /**
   * UI
   */
  public DiscoveryFunctionView(Context context) {
    this(context, null);
  }

  public DiscoveryFunctionView(Context context, AttributeSet attrs) {
    super(context, attrs);
    mContext = context;
    initView();
  }

  private void initView() {
    View rootView =
        LayoutInflater.from(mContext).inflate(R.layout.item_discory_head_function_layout, this);
  }
}
