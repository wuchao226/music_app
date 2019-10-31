package com.wuc.lib_qrcode.zbar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;

import me.dm7.barcodescanner.core.ViewFinderView;

/**
 * @author: wuchao
 * @date: 2019-10-29 18:01
 * @desciption:
 */
public class ZbarViewFinderView extends ViewFinderView {

    public ZbarViewFinderView(Context context) {
        this(context, null);
    }

    public ZbarViewFinderView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setSquareViewFinder(true);
        setLaserEnabled(true);
        mLaserPaint.setColor(Color.YELLOW);
        mBorderPaint.setColor(Color.YELLOW);
    }

    @Override
    public void drawLaser(Canvas canvas) {
        super.drawLaser(canvas);
    }
}
