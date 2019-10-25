package com.wuc.lib_common_ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;

/**
 * @author: wuchao
 * @date: 2019-10-21 18:23
 * @desciption: 水平icon+msg
 */
public class HorizontalItemView extends RelativeLayout {

    private Context mContext;

    /**
     * 所有自定义属性
     */
    private int mPaddingRight;
    private int mPaddingLeft;
    private int mPaddingTop;
    private int mPaddingBottom;

    private int mIconWidth;
    private int mIconHeight;
    private Drawable mIcon;
    private int mIconPaddingRight;

    private float mTileTextSize;
    private int mTileTextColor;
    private String mTileText;

    private int mTipTextSize;
    private int mTipTextColor;
    private String mTipText;
    private int mTipPaddingLeft;
    private boolean mTipVisiblity;

    private float mRightTextSize;
    private int mRightTextColor;
    private String mRightText;

    private Drawable mRightIcon;
    private int mRightIconWidth;
    private int mRightIconHeight;
    private int mRightIconMarginLeft;

    /**
     * 所有自定义View
     */
    private AppCompatImageView mTitleView;
    private AppCompatTextView mTileView;
    private AppCompatTextView mTipView;
    private AppCompatTextView mRightView;
    private AppCompatImageView mRightImageView;

    public HorizontalItemView(Context context) {
        this(context, null);
    }

    public HorizontalItemView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HorizontalItemView(Context context, AttributeSet attrs, int defaultStyle) {
        super(context, attrs, defaultStyle);
        mContext = context;
        TypedArray a = mContext.obtainStyledAttributes(attrs, R.styleable.HorizontalItemView);
        mPaddingLeft = a.getLayoutDimension(R.styleable.HorizontalItemView_paddingLeft, 20);
        mPaddingRight = a.getLayoutDimension(R.styleable.HorizontalItemView_paddingRight, 20);
        mPaddingTop = a.getLayoutDimension(R.styleable.HorizontalItemView_paddingTop, 10);
        mPaddingBottom = a.getLayoutDimension(R.styleable.HorizontalItemView_paddingBottom, 10);

        mIconWidth = a.getLayoutDimension(R.styleable.HorizontalItemView_hIconWidth, 70);
        mIconHeight = a.getLayoutDimension(R.styleable.HorizontalItemView_hIconHeight, 70);
        mIcon = a.getDrawable(R.styleable.HorizontalItemView_hIcon);
        mIconPaddingRight = a.getLayoutDimension(R.styleable.HorizontalItemView_iconPaddingRight, 15);

        mTileTextSize = a.getDimension(R.styleable.HorizontalItemView_tileTextSize, 15);
        mTileTextColor = a.getColor(R.styleable.HorizontalItemView_tileTextColor, 0xff333333);
        mTileText = a.getString(R.styleable.HorizontalItemView_tileText);

        mTipTextSize = a.getLayoutDimension(R.styleable.HorizontalItemView_hTipTextSize, 15);
        mTipTextColor = a.getColor(R.styleable.HorizontalItemView_hTipTextColor, 0xff333333);
        mTipText = a.getString(R.styleable.HorizontalItemView_hTipText);
        mTipVisiblity = a.getBoolean(R.styleable.HorizontalItemView_hTipVisiblity, false);
        mTipPaddingLeft = a.getLayoutDimension(R.styleable.HorizontalItemView_hTipPaddingLeft, 2);

        mRightIcon = a.getDrawable(R.styleable.HorizontalItemView_rightIcon);
        mRightIconWidth = a.getLayoutDimension(R.styleable.HorizontalItemView_rightIconWidth, 20);
        mRightIconHeight = a.getLayoutDimension(R.styleable.HorizontalItemView_rightIconHeight, 30);
        mRightIconMarginLeft =
                a.getLayoutDimension(R.styleable.HorizontalItemView_rightIconMarginLeft, 20);

        mRightTextSize = a.getDimension(R.styleable.HorizontalItemView_rightTextSize, 12);
        mRightTextColor = a.getColor(R.styleable.HorizontalItemView_rightTextColor, 0xff666666);
        mRightText = a.getString(R.styleable.HorizontalItemView_rightText);
        a.recycle();

        createView();
    }

    private void createView() {
        RelativeLayout rootLayout = new RelativeLayout(mContext);

        RelativeLayout layout = new RelativeLayout(mContext);
        LayoutParams layoutParams =
                new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        layoutParams.setMargins(mPaddingLeft, mPaddingTop, mPaddingRight, mPaddingBottom);

        //添加最左侧的图标
        mTitleView = new AppCompatImageView(mContext);
        mTitleView.setId(R.id.horizontal_image_id);
        mTitleView.setScaleType(ImageView.ScaleType.FIT_XY);
        mTitleView.setImageDrawable(mIcon);
        LayoutParams titleParams =
                new LayoutParams(mIconWidth, mIconHeight);
        titleParams.setMargins(0, 0, mIconPaddingRight, 0);
        titleParams.addRule(RelativeLayout.CENTER_VERTICAL);
        layout.addView(mTitleView, titleParams);

        mTileView = new AppCompatTextView(mContext);
        mTileView.setId(R.id.horizontal_tile_id);
        mTileView.setText(mTileText);
        mTileView.setTextColor(mTileTextColor);
        mTileView.setTextSize(mTileTextSize);
        LayoutParams tileParams =
                new LayoutParams(LayoutParams.WRAP_CONTENT,
                        LayoutParams.WRAP_CONTENT);
        tileParams.addRule(RelativeLayout.RIGHT_OF, R.id.horizontal_image_id);
        tileParams.addRule(RelativeLayout.CENTER_VERTICAL);
        layout.addView(mTileView, tileParams);

        if (mTipVisiblity) {
            mTipView = new AppCompatTextView(mContext);
            mTipView.setText(mTipText);
            mTipView.setTextColor(mTipTextColor);
            mTipView.getPaint().setTextSize(mTipTextSize);
            LayoutParams tipParams =
                    new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            tipParams.addRule(RelativeLayout.RIGHT_OF, R.id.horizontal_tile_id);
            tipParams.addRule(RelativeLayout.CENTER_VERTICAL);
            tipParams.setMargins(mTipPaddingLeft, 0, 0, 0);
            layout.addView(mTipView, tipParams);
        }
        //
        mRightView = new AppCompatTextView(mContext);
        mRightView.setText(mRightText);
        mRightView.setTextColor(mRightTextColor);
        mRightView.setTextSize(mRightTextSize);
        LayoutParams rightParams =
                new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        //rightIcon不为空，添加到rightIcon左侧
        if (mRightIcon != null) {
            mRightImageView = new AppCompatImageView(mContext);
            mRightImageView.setId(R.id.horizontal_right_image_id);
            mRightImageView.setImageDrawable(mRightIcon);
            LayoutParams rightImageParams =
                    new LayoutParams(mRightIconWidth, mRightIconHeight);
            rightImageParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            rightImageParams.addRule(RelativeLayout.CENTER_VERTICAL);
            rightImageParams.setMargins(mRightIconMarginLeft, 0, 0, 0);
            layout.addView(mRightImageView, rightImageParams);

            rightParams.addRule(RelativeLayout.LEFT_OF, R.id.horizontal_right_image_id);
            rightParams.addRule(RelativeLayout.CENTER_VERTICAL);
            rightParams.setMargins(mRightIconMarginLeft, 0, 0, 0);
            layout.addView(mRightView, rightParams);
        } else {
            //添加到父布局左侧
            rightParams.addRule(RelativeLayout.ALIGN_PARENT_END);
            rightParams.addRule(RelativeLayout.CENTER_VERTICAL);
            layout.addView(mRightView, rightParams);
        }

        rootLayout.addView(layout, layoutParams);
        addView(rootLayout, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
    }
}
