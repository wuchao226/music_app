package com.wuc.lib_common_ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

/**
 * @author: wuchao
 * @date: 2019-09-26 09:40
 * @desciption: 扩散圆
 */
public class SpreadView extends View {
    /**
     * 中心圆半径
     */
    private int mCenterRadius = 100;
    /**
     * 圆心X
     */
    private float mCenterX;
    /**
     * 圆心Y
     */
    private float mCenterY;
    /**
     * 每次圆递增间距
     */
    private int mDistance = 5;
    /**
     * 扩散最大圆半径
     */
    private int mMaxRadius = 80;
    /**
     * 扩散延迟间隔，越大扩散越慢
     */
    private int mDelayMilliseconds = 33;
    /**
     * 中心圆paint
     */
    private Paint mCenterPaint;
    /**
     * 扩散圆paint
     */
    private Paint mSpreadPaint;
    /**
     * 扩散圆层级数，元素为扩散的距离
     */
    private List<Integer> mSpreadRadius = new ArrayList<>();
    /**
     * 对应每层圆的透明度
     */
    private List<Integer> mAlphas = new ArrayList<>();


    public SpreadView(Context context) {
        this(context, null);
    }

    public SpreadView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SpreadView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SpreadView,
                defStyleAttr, 0);
        mCenterRadius = typedArray.getInt(R.styleable.SpreadView_spread_center_radius, mCenterRadius);
        mMaxRadius = typedArray.getInt(R.styleable.SpreadView_spread_max_radius, mMaxRadius);
        int centerColor = typedArray.getColor(R.styleable.SpreadView_spread_center_color,
                ContextCompat.getColor(context, android.R.color.holo_red_dark));
        int spreadColor = typedArray.getColor(R.styleable.SpreadView_spread_color,
                ContextCompat.getColor(context, R.color.color_F71816));
        mDistance = typedArray.getInt(R.styleable.SpreadView_spread_distance, mDistance);
        typedArray.recycle();

        mCenterPaint = new Paint();
        mCenterPaint.setAntiAlias(true);
        mCenterPaint.setColor(centerColor);

        //最开始不透明且扩散距离为0
        mAlphas.add(255);
        mSpreadRadius.add(0);

        mSpreadPaint = new Paint();
        mSpreadPaint.setAntiAlias(true);
        mSpreadPaint.setColor(spreadColor);
        mSpreadPaint.setStyle(Paint.Style.STROKE);
        mSpreadPaint.setAlpha(255);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        //圆心位置
        mCenterX = w / 2;
        mCenterY = h / 2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //绘制当前所有的圆
        for (int i = 0; i < mSpreadRadius.size(); i++) {
            int alpha = mAlphas.get(i);
            //扩散的距离
            int width = mSpreadRadius.get(i);
            mSpreadPaint.setAlpha(alpha);
            //绘制扩散圆
            canvas.drawCircle(mCenterX, mCenterY, mCenterRadius + width, mSpreadPaint);
            //每次扩散圆半径递增，圆透明度递减
            if (alpha > 0 && width < 300) {
                alpha = alpha - mDistance > 0 ? alpha - mDistance : 0;
                mAlphas.add(alpha);
                mSpreadRadius.set(i, width + mDistance);
            }
        }
        //当最外层扩散圆半径达到最大半径时添加新扩散圆
        if (mSpreadRadius.get(mSpreadRadius.size() - 1) > mMaxRadius) {
            mSpreadRadius.add(0);
            mAlphas.add(255);
        }
        //超过8个扩散圆，删除最先绘制的圆，即最外层的圆
        if (mSpreadRadius.size() >= 8) {
            mAlphas.remove(0);
            mSpreadRadius.remove(0);
        }
        //绘制中心圆
        canvas.drawCircle(mCenterX, mCenterY, mCenterRadius, mCenterPaint);
        //延迟更新，达到扩散视觉差效果
        postInvalidateDelayed(mDelayMilliseconds);
    }
}
