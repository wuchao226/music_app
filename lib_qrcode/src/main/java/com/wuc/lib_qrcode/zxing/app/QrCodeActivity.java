package com.wuc.lib_qrcode.zxing.app;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.wuc.lib_common_ui.base.BaseActivity;
import com.wuc.lib_common_ui.base.constant.Constant;
import com.wuc.lib_qrcode.R;
import com.wuc.lib_qrcode.zbar.ScanActivity;
import com.wuc.lib_qrcode.zxing.encode.CodeCreator;

import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;

/**
 * @author: wuchao
 * @date: 2018/12/12 14:06
 * @desciption: 二维码相关
 */
public class QrCodeActivity extends BaseActivity implements View.OnClickListener {

    private Button scanBtn;
    private AppCompatTextView result;
    private AppCompatEditText contentEt;
    private Button encodeBtn;
    private AppCompatImageView contentIv;
    private Toolbar toolbar;
    private int REQUEST_CODE_SCAN = 111;
    /**
     * 生成带logo的二维码
     */
    private Button encodeBtnWithLogo;
    private AppCompatImageView contentIvWithLogo;
    private String contentEtString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_code_layout);
        initView();
    }

    private void initView() {
        /*扫描按钮*/
        scanBtn = findViewById(R.id.scanBtn);
        scanBtn.setOnClickListener(this);
        /*扫描结果*/
        result = findViewById(R.id.result);

        /*要生成二维码的输入框*/
        contentEt = findViewById(R.id.contentEt);
        /*生成按钮*/
        encodeBtn = findViewById(R.id.encodeBtn);
        encodeBtn.setOnClickListener(this);
        /*生成的图片*/
        contentIv = findViewById(R.id.contentIv);
        encodeBtnWithLogo = findViewById(R.id.encodeBtnWithLogo);
        encodeBtnWithLogo.setOnClickListener(this);
        contentIvWithLogo = findViewById(R.id.contentIvWithLogo);

        toolbar = findViewById(R.id.toolbar);

        toolbar.setTitle("扫一扫");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onClick(View v) {
        Bitmap bitmap = null;
        int id = v.getId();
        if (id == R.id.scanBtn) {
            if (hasPermission(Constant.HARDWEAR_CAMERA_PERMISSION)) {
                Intent intent = new Intent(QrCodeActivity.this, ScanActivity.class);
                startActivityForResult(intent, REQUEST_CODE_SCAN);
            } else {
                requestPermission(Constant.HARDWEAR_CAMERA_CODE, Constant.HARDWEAR_CAMERA_PERMISSION);
            }
        } else if (id == R.id.encodeBtn) {
            contentEtString = contentEt.getText().toString().trim();
            if (TextUtils.isEmpty(contentEtString)) {
                Toast.makeText(this, "请输入要生成二维码图片的字符串", Toast.LENGTH_SHORT).show();
                return;
            }

            bitmap = CodeCreator.createQRCode(contentEtString, 400, 400, null);
            if (bitmap != null) {
                contentIv.setImageBitmap(bitmap);
            }
        } else if (id == R.id.encodeBtnWithLogo) {
            contentEtString = contentEt.getText().toString().trim();
            if (TextUtils.isEmpty(contentEtString)) {
                Toast.makeText(this, "请输入要生成二维码图片的字符串", Toast.LENGTH_SHORT).show();
                return;
            }
            Bitmap logo = BitmapFactory.decodeResource(getResources(), R.drawable.launcher_icon);
            bitmap = CodeCreator.createQRCode(contentEtString, 400, 400, logo);
            if (bitmap != null) {
                contentIvWithLogo.setImageBitmap(bitmap);
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 扫描二维码/条码回传
        if (requestCode == REQUEST_CODE_SCAN && resultCode == RESULT_OK) {
            if (data != null) {
                String content = data.getStringExtra(CaptureActivity.SCAN_RESULT);
                result.setText("扫描结果为：" + content);
            }
        }
    }
}
