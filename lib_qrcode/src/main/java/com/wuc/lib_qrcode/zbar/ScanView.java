package com.wuc.lib_qrcode.zbar;

import android.content.Context;
import android.util.AttributeSet;

import me.dm7.barcodescanner.core.IViewFinder;
import me.dm7.barcodescanner.zbar.ZBarScannerView;

/**
 * @author: wuchao
 * @date: 2019-10-29 18:00
 * @desciption:
 */
public class ScanView extends ZBarScannerView {

    public ScanView(Context context) {
        this(context, null);
    }

    public ScanView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    @Override
    protected IViewFinder createViewFinderView(Context context) {
        return new ZbarViewFinderView(context);
    }
}
