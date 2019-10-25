package com.wuc.lib_common_ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.wuc.lib_common_ui.utils.StatusBarUtil;
import com.wuc.lib_image_loader.ImageLoaderManager;

import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.LinearLayoutCompat;

/**
 * @author: wuchao
 * @date: 2019-10-21 16:47
 * @desciption: 显示1~N 张图片的View
 */
public class MultiImageViewLayout extends LinearLayoutCompat {

    public static int MAX_WIDTH = 0;
    /**
     * 照片的 Url 列表
     */
    private List<String> mImagesList;
    /** 长度 单位为Pixel **/
    /**
     * 单张图最大允许宽高
     */
    //private int pxOneMaxWandH;
    /**
     * 多张图的宽高
     */
    private int pxMoreWandh = 0;
    /**
     * 图片间的间距
     */
    private int pxImagePadding = StatusBarUtil.dip2px(getContext(), 3);
    /**
     * 每行显示最大数
     */
    private int MAX_PER_ROW_COUNT = 3;

    private LayoutParams onePicPara;
    private LayoutParams morePara, moreParaColumnFirst;
    private LayoutParams rowPara;

    private OnItemClickListener mOnItemClickListener;

    public MultiImageViewLayout(Context context) {
        this(context, null);
    }

    public MultiImageViewLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MultiImageViewLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (MAX_WIDTH == 0) {
            int width = measureWidth(widthMeasureSpec);
            if (width > 0) {
                MAX_WIDTH = width;
                if (mImagesList != null && mImagesList.size() > 0) {
                    setList(mImagesList);
                }
            }
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    /**
     * Determines the width of this view
     *
     * @param measureSpec A measureSpec packed into an int
     * @return The width of the view, honoring constraints from measureSpec
     */
    private int measureWidth(int measureSpec) {
        int result = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.EXACTLY) {
            // We were told how big to be
            result = specSize;
        } else {
            // Measure the text
            // result = (int) mTextPaint.measureText(mText) + getPaddingLeft()
            // + getPaddingRight();
            if (specMode == MeasureSpec.AT_MOST) {
                // Respect AT_MOST value if that was what is called for by
                // measureSpec
                result = Math.min(result, specSize);
            }
        }
        return result;
    }

    public void setList(List<String> lists) {
        if (lists == null) {
            throw new IllegalArgumentException("imageList is null...");
        }
        mImagesList = lists;
        if (MAX_WIDTH > 0) {
            //解决右侧图片和内容对不齐问题
            pxMoreWandh = (MAX_WIDTH - pxImagePadding * 2) / 3;
            //pxOneMaxWandH = MAX_WIDTH * 2 / 3;
            initImageLayoutParams();
        }
        initView();
    }

    private void initImageLayoutParams() {
        int wrap = LayoutParams.WRAP_CONTENT;
        int match = LayoutParams.MATCH_PARENT;
        onePicPara = new LayoutParams(wrap, wrap);
        moreParaColumnFirst = new LayoutParams(pxMoreWandh, pxMoreWandh);
        morePara = new LayoutParams(pxMoreWandh, pxMoreWandh);
        morePara.setMargins(pxImagePadding, 0, 0, 0);
        rowPara = new LayoutParams(match, wrap);
    }

    /**
     * 根据imageView的数量初始化不同的View布局,还要为每一个View作点击效果
     */
    private void initView() {
        this.setOrientation(VERTICAL);
        this.removeAllViews();
        if (MAX_WIDTH == 0) {
            //为了触发onMeasure()来测量MultiImageView的最大宽度，MultiImageView的宽设置为match_parent
            addView(new View(getContext()));
            return;
        }
        if (mImagesList == null || mImagesList.size() == 0) {
            return;
        }
        if (mImagesList.size() == 1) {
            addView(createImageView(0, false));
        } else {
            int allCount = mImagesList.size();
            if (allCount == 4) {
                MAX_PER_ROW_COUNT = 2;
            } else {
                MAX_PER_ROW_COUNT = 3;
            }
            // 行数
            int rowCount = allCount / MAX_PER_ROW_COUNT + (allCount % MAX_PER_ROW_COUNT > 0 ? 1 : 0);
            for (int rowCursor = 0; rowCursor < rowCount; rowCursor++) {
                LinearLayoutCompat rowLayout = new LinearLayoutCompat(getContext());
                rowLayout.setOrientation(LinearLayoutCompat.HORIZONTAL);

                rowLayout.setLayoutParams(rowPara);
                if (rowCursor != 0) {
                    rowLayout.setPadding(0, pxImagePadding, 0, 0);
                }
                //每行的列数
                int columnCount = allCount % MAX_PER_ROW_COUNT == 0 ? MAX_PER_ROW_COUNT
                        : allCount % MAX_PER_ROW_COUNT;
                if (rowCursor != rowCount - 1) {
                    columnCount = MAX_PER_ROW_COUNT;
                }
                addView(rowLayout);

                // 行偏移
                int rowOffset = rowCursor * MAX_PER_ROW_COUNT;
                for (int columnCursor = 0; columnCursor < columnCount; columnCursor++) {
                    int position = columnCursor + rowOffset;
                    rowLayout.addView(createImageView(position, true));
                }
            }
        }
    }

    private View createImageView(int position, boolean isMultiImage) {
        String photoInfo = mImagesList.get(position);
        AppCompatImageView imageView = new AppCompatImageView(getContext());
        if (isMultiImage) {
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setLayoutParams(position % MAX_PER_ROW_COUNT == 0 ? moreParaColumnFirst : morePara);
        } else {
            imageView.setAdjustViewBounds(true);
            imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            //一张图，根据图的大小wrap
            imageView.setLayoutParams(onePicPara);
        }

        imageView.setId(photoInfo.hashCode());
        imageView.setOnClickListener(new ImageOnClickListener(position));
        ImageLoaderManager.getInstance().displayImageForView(imageView, photoInfo);
        return imageView;
    }

    /**
     *
     */
    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    private class ImageOnClickListener implements OnClickListener {

        private int position;

        public ImageOnClickListener(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View view) {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(view, position);
            }
        }
    }
}
