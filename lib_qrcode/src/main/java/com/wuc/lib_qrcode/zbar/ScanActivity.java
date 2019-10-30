package com.wuc.lib_qrcode.zbar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.wuc.lib_base.router.RouterPath;
import com.wuc.lib_common_ui.base.BaseActivity;
import com.wuc.lib_qrcode.R;
import com.wuc.lib_qrcode.zxing.app.QrCodeActivity;

import androidx.annotation.Nullable;
import me.dm7.barcodescanner.zbar.Result;
import me.dm7.barcodescanner.zbar.ZBarScannerView;

/**
 * @author: wuchao
 * @date: 2019-10-29 17:33
 * @desciption:
 */
@Route(path = RouterPath.QrCode.PATH_CAPTURE_ZBAR_ACTIVITY)
public class ScanActivity extends BaseActivity implements View.OnClickListener,
        ZBarScannerView.ResultHandler {

    public static String SCAN_RESULT = "SCAN_RESULT";
    private ScanView mScanView;
    private FrameLayout mContentFrame;
    private Button mButtonBack;
    private Button mPhotoBtn;
    private Button mFlashBtn;
    private Button mQrcodeBtn;
    private int REQUEST_CODE = 3;
    private boolean isFlash = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("scan", "onCreate");
        setContentView(R.layout.activity_zbar_capture_layout);
        initView();
    }

    private void initView() {
        mContentFrame = (FrameLayout) findViewById(R.id.content_frame);
        mButtonBack = (Button) findViewById(R.id.button_back);
        mButtonBack.setOnClickListener(this);
        mPhotoBtn = (Button) findViewById(R.id.photo_btn);
        mPhotoBtn.setOnClickListener(this);
        mFlashBtn = (Button) findViewById(R.id.flash_btn);
        mFlashBtn.setOnClickListener(this);
        mQrcodeBtn = (Button) findViewById(R.id.qrcode_btn);
        mQrcodeBtn.setOnClickListener(this);

        if (mScanView == null) {
            mScanView = new ScanView(this);
        }
        mScanView.setAutoFocus(true);
        mScanView.setResultHandler(this);
        // this paramter will make your HUAWEI phone works great!
        mScanView.setAspectTolerance(0.5f);
        mContentFrame.addView(mScanView);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mScanView != null) {
            mScanView.stopCameraPreview();
            mScanView.stopCamera();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mScanView != null) {
            mScanView.startCamera();
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.button_back) {
            finish();
        } else if (id == R.id.photo_btn) {
            // 打开手机中的相册
            // "android.intent.action.GET_CONTENT"
            Intent innerIntent = new Intent(Intent.ACTION_GET_CONTENT);
            innerIntent.setType("image/*");
            Intent wrapperIntent = Intent.createChooser(innerIntent, "选择二维码图片");
            startActivityForResult(wrapperIntent, REQUEST_CODE);
        } else if (id == R.id.flash_btn) {
            if (!isFlash) {
                mScanView.setFlash(true);
            } else {
                mScanView.setFlash(false);
            }
            isFlash = !isFlash;
        } else if (id == R.id.qrcode_btn) {
            // 跳转到生成二维码页面
            Intent intent = new Intent(ScanActivity.this, QrCodeActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void handleResult(Result rawResult) {
        Log.d("scan", "content" + rawResult.getContents());
        Log.d("scan", "format" + rawResult.getBarcodeFormat());
        Intent intent = new Intent();
        intent.putExtra(SCAN_RESULT, rawResult.getContents());
        setResult(Activity.RESULT_OK, intent);
        finish();
    }
}
