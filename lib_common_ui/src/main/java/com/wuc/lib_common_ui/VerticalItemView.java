package com.wuc.lib_common_ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;

/**
 * @author: wuchao
 * @date: 2019-10-17 10:02
 * @desciption: 垂直icon+msg
 */
public class VerticalItemView extends RelativeLayout {

    private Context mContext;

    /*
     * 所有样式属性
     */
    private int mIconWidth;
    private int mIconHeight;
    private Drawable mIcon;

    private int mTipPaddingTop;
    private int mTipPaddingRight;
    private Drawable mTipBg;
    private int mTipTextColor;
    private float mTipTextSize;
    private String mTipText;

    private float mInfoTextSize;
    private int mInfoTextColor;
    private int mInfoTextMarginTop;
    private String mInfoText;

    /*
     * 所有View
     */
    private AppCompatImageView mIconView;
    private AppCompatTextView mTipView;
    private AppCompatTextView mInfoView;

    public VerticalItemView(Context context) {
        this(context, null);
    }

    public VerticalItemView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VerticalItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        TypedArray typedArray = mContext.obtainStyledAttributes(attrs, R.styleable.VerticalItemView);
        mIconWidth = typedArray.getLayoutDimension(R.styleable.VerticalItemView_iconWidth, 35);
        mIconHeight = typedArray.getLayoutDimension(R.styleable.VerticalItemView_iconHeight, 35);
        mIcon = typedArray.getDrawable(R.styleable.VerticalItemView_icon);
        mTipPaddingTop = typedArray.getLayoutDimension(R.styleable.VerticalItemView_tipPaddingTop, 2);
        mTipPaddingRight = typedArray.getLayoutDimension(R.styleable.VerticalItemView_tipPaddingRight, 2);
        mTipBg = typedArray.getDrawable(R.styleable.VerticalItemView_tipBg);
        mTipTextColor = typedArray.getColor(R.styleable.VerticalItemView_tipTextColor, 0xffffff);
        mTipTextSize = typedArray.getDimension(R.styleable.VerticalItemView_tipTextSize, 12);
        mTipText = typedArray.getString(R.styleable.VerticalItemView_tipText);
        mInfoTextSize = typedArray.getDimension(R.styleable.VerticalItemView_infoTextSize, 12);
        mInfoTextColor = typedArray.getColor(R.styleable.VerticalItemView_infoTextColor, 0x333333);
        mInfoTextMarginTop = typedArray.getLayoutDimension(R.styleable.VerticalItemView_infoTextMarginTop, 10);
        mInfoText = typedArray.getString(R.styleable.VerticalItemView_infoText);
        typedArray.recycle();

        //居中添加到布局中
        RelativeLayout.LayoutParams params =
                new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.addRule(CENTER_IN_PARENT);
        addView(createItemView(), params);
    }

    /**
     * 构建自己的组合view
     */
    private View createItemView() {
        RelativeLayout rootLayout = new RelativeLayout(mContext);
        mIconView = new AppCompatImageView(mContext);
        mIconView.setImageDrawable(mIcon);
        mIconView.setId(R.id.vertical_image_id);
        RelativeLayout.LayoutParams iconParams =
                new RelativeLayout.LayoutParams(mIconWidth, mIconHeight);
        iconParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        rootLayout.addView(mIconView, iconParams);

        mInfoView = new AppCompatTextView(mContext);
        mInfoView.setId(R.id.vertical_text_id);
        mInfoView.setTextColor(mInfoTextColor);
        mInfoView.getPaint().setTextSize(mInfoTextSize);
        mInfoView.setText(mInfoText);
        RelativeLayout.LayoutParams textParams =
                new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        textParams.setMargins(0, mInfoTextMarginTop, 0, 0);
        textParams.addRule(RelativeLayout.BELOW, R.id.vertical_image_id);
        textParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        rootLayout.addView(mInfoView, textParams);

       /* mTipView = new AppCompatTextView(mContext);
        mTipView.setId(R.id.vertical_tip_id);
        mTipView.setBackground(mTipBg);
        mTipView.setText(mTipText);
        mTipView.getPaint().setTextSize(mTipTextSize);
        mTipView.setTextColor(mTipTextColor);
        RelativeLayout.LayoutParams tipParams =
                new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        tipParams.addRule(RelativeLayout.RIGHT_OF, R.id.vertical_image_id);
        tipParams.addRule(RelativeLayout.ABOVE, R.id.vertical_image_id);
        tipParams.setMargins(0, mTipPaddingTop, mTipPaddingRight, 0);
        rootLayout.addView(mTipView, tipParams);*/
        return rootLayout;
    }
}
