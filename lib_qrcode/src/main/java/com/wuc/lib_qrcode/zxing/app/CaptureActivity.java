/*
 * Copyright (C) 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.wuc.lib_qrcode.zxing.app;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.DecodeHintType;
import com.google.zxing.Result;
import com.google.zxing.ResultMetadataType;
import com.google.zxing.ResultPoint;
import com.google.zxing.client.result.ResultParser;
import com.wuc.lib_base.router.RouterPath;
import com.wuc.lib_common_ui.base.BaseActivity;
import com.wuc.lib_qrcode.R;
import com.wuc.lib_qrcode.zxing.camera.CameraManager;
import com.wuc.lib_qrcode.zxing.decode.AmbientLightManager;
import com.wuc.lib_qrcode.zxing.decode.BeepManager;
import com.wuc.lib_qrcode.zxing.decode.CaptureActivityHandler;
import com.wuc.lib_qrcode.zxing.decode.DecodeFormatManager;
import com.wuc.lib_qrcode.zxing.decode.FinishListener;
import com.wuc.lib_qrcode.zxing.decode.InactivityTimer;
import com.wuc.lib_qrcode.zxing.decode.IntentSource;
import com.wuc.lib_qrcode.zxing.decode.Intents;
import com.wuc.lib_qrcode.zxing.encode.QRCodeDecoder;
import com.wuc.lib_qrcode.zxing.util.Util;
import com.wuc.lib_qrcode.zxing.view.ViewfinderView;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Map;

/**
 * @author: wuchao
 * @date: 2018/12/10 16:56
 * @desciption: 扫描界面
 */
@Route(path = RouterPath.QrCode.PATH_CAPTURE_ZXING_ACTIVITY)
public final class CaptureActivity extends BaseActivity implements SurfaceHolder.Callback {

    private static final String TAG = CaptureActivity.class.getSimpleName();

    private static final long INTENT_RESULT_DURATION = 1500L;
    private static final long BULK_MODE_SCAN_DELAY_MS = 1000L;

    private static final String[] ZXING_URLS = {"http://zxing.appspot.com/scan", "zxing://scan/"};
    private static final String RETURN_URL_PARAM = "ret";

    private static final Collection<ResultMetadataType> DISPLAYABLE_METADATA_TYPES =
            EnumSet.of(ResultMetadataType.ISSUE_NUMBER,
                    ResultMetadataType.SUGGESTED_PRICE,
                    ResultMetadataType.ERROR_CORRECTION_LEVEL,
                    ResultMetadataType.POSSIBLE_COUNTRY);
    public static String SCAN_RESULT = "SCAN_RESULT";
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private CameraManager cameraManager;
    private CaptureActivityHandler handler;
    private Result savedResultToShow;
    private ViewfinderView viewfinderView;
    private Button mButtonBack;
    private Button createBtn;
    private Button photoBtn;
    private Button flashBtn;
    private Result lastResult;
    private boolean hasSurface;
    private IntentSource source;
    private String sourceUrl;
    private String returnUrlTemplate;
    private Collection<BarcodeFormat> decodeFormats;
    private Map<DecodeHintType, ?> decodeHints;
    private String characterSet;
    private InactivityTimer inactivityTimer;
    private BeepManager beepManager;
    private AmbientLightManager ambientLightManager;
    private boolean isFlash = false;
    private int REQUEST_CODE = 3;
    /**
     * 闪光灯点击事件
     */
    private View.OnClickListener click = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            int id = v.getId();
            if (id == R.id.button_back) {
                finish();
            } else if (id == R.id.flash_btn) {
                if (!isFlash) {
                    cameraManager.turnLightOn();
                } else {

                    cameraManager.turnLightOff();
                }
                isFlash = !isFlash;
            } else if (id == R.id.photo_btn) {
                // 打开手机中的相册
                // "android.intent.action.GET_CONTENT"
                Intent innerIntent = new Intent(Intent.ACTION_GET_CONTENT);
                innerIntent.setType("image/*");
                Intent wrapperIntent = Intent.createChooser(innerIntent, "选择二维码图片");
                startActivityForResult(wrapperIntent, REQUEST_CODE);
            } else if (id == R.id.qrcode_btn) {
                // 跳转到生成二维码页面
                Intent intent = new Intent(CaptureActivity.this, QrCodeActivity.class);
                startActivity(intent);
                finish();
            }

        }
    };

    public ViewfinderView getViewfinderView() {
        return viewfinderView;
    }

    public Handler getHandler() {
        return handler;
    }

    public CameraManager getCameraManager() {
        return cameraManager;
    }

    @Override
    public void onCreate(Bundle icicle) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(icicle);
        //window设置标志位，保证屏幕常亮不会黑屏
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_qrcode_capture_layout);

        Util.currentActivity = this;
        viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);

        mButtonBack = (Button) findViewById(R.id.button_back);
        mButtonBack.setOnClickListener(click);
        createBtn = (Button) findViewById(R.id.qrcode_btn);
        createBtn.setOnClickListener(click);
        photoBtn = (Button) findViewById(R.id.photo_btn);
        photoBtn.setOnClickListener(click);
        flashBtn = (Button) findViewById(R.id.flash_btn);
        flashBtn.setOnClickListener(click);

        surfaceView = (SurfaceView) findViewById(R.id.preview_view);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        hasSurface = false;
        handler = null;
        lastResult = null;
        inactivityTimer = new InactivityTimer(this);
        beepManager = new BeepManager(this);
        ambientLightManager = new AmbientLightManager(this);
        cameraManager = new CameraManager(getApplication());

        viewfinderView.setCameraManager(cameraManager);

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

    }

    @Override
    protected void onDestroy() {
        inactivityTimer.shutdown();
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                if (source == IntentSource.NATIVE_APP_INTENT) {
                    setResult(RESULT_CANCELED);
                    finish();
                    return true;
                }
                if ((source == IntentSource.NONE || source == IntentSource.ZXING_LINK) && lastResult != null) {
                    restartPreviewAfterDelay(0L);
                    return true;
                }
                break;
            case KeyEvent.KEYCODE_FOCUS:
            case KeyEvent.KEYCODE_CAMERA:
                // Handle these events so they don't launch the Camera app
                return true;
            // Use volume up/down to turn on light
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                cameraManager.setTorch(false);
                return true;
            case KeyEvent.KEYCODE_VOLUME_UP:
                cameraManager.setTorch(true);
                return true;
            default:
        }
        return super.onKeyDown(keyCode, event);
    }

    public void restartPreviewAfterDelay(long delayMS) {
        if (handler != null) {
            handler.sendEmptyMessageDelayed(R.id.restart_preview, delayMS);
        }
        resetStatusView();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            // 首先获取到此图片的Uri
            Uri uri = data.getData();
            String photoPath = getRealFilePath(this, uri);
            if (photoPath == null) {
                Toast.makeText(getApplicationContext(), "路径获取失败", Toast.LENGTH_SHORT).show();
            } else {
                //解析图片
                parsePhoto(photoPath);
            }
        }
    }

    @Override
    protected void onPause() {
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        inactivityTimer.onPause();
        ambientLightManager.stop();
        beepManager.close();
        cameraManager.closeDriver();
        //historyManager = null; // Keep for onActivityResult
        if (!hasSurface) {
            SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
            SurfaceHolder surfaceHolder = surfaceView.getHolder();
            surfaceHolder.removeCallback(this);
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();

        resetStatusView();
        if (hasSurface) {
            // The activity was paused but not stopped, so the surface still exists. Therefore
            // surfaceCreated() won't be called, so init the camera here.
            initCamera(surfaceHolder);
        } else {
            // Install the callback and wait for surfaceCreated() to init the camera.
            surfaceHolder.addCallback(this);
        }

        Intent intent = getIntent();
        String action = intent == null ? null : intent.getAction();
        String dataString = intent == null ? null : intent.getDataString();

        if (intent != null && action != null) {
            if (Intents.Scan.ACTION.equals(action)) {
                // Scan the formats the intent requested, and return the result to the calling activity.
                source = IntentSource.NATIVE_APP_INTENT;
                decodeFormats = DecodeFormatManager.parseDecodeFormats(intent);

                if (intent.hasExtra(Intents.Scan.WIDTH) && intent.hasExtra(Intents.Scan.HEIGHT)) {
                    int width = intent.getIntExtra(Intents.Scan.WIDTH, 0);
                    int height = intent.getIntExtra(Intents.Scan.HEIGHT, 0);
                    if (width > 0 && height > 0) {
                        cameraManager.setManualFramingRect(width, height);
                    }
                }

                if (intent.hasExtra(Intents.Scan.CAMERA_ID)) {
                    int cameraId = intent.getIntExtra(Intents.Scan.CAMERA_ID, -1);
                    if (cameraId >= 0) {
                        cameraManager.setManualCameraId(cameraId);
                    }
                }
            } else if (dataString != null &&
                    dataString.contains("http://www.google") &&
                    dataString.contains("/m/products/scan")) {

                // Scan only products and send the result to mobile Product Search.
                source = IntentSource.PRODUCT_SEARCH_LINK;
                sourceUrl = dataString;
                Uri inputUri = Uri.parse(sourceUrl);
                returnUrlTemplate = inputUri.getQueryParameter(RETURN_URL_PARAM);
                decodeFormats = DecodeFormatManager.PRODUCT_FORMATS;

            } else if (isZXingURL(dataString)) {
                source = IntentSource.ZXING_LINK;
                sourceUrl = dataString;
                Uri inputUri = Uri.parse(dataString);
                decodeFormats = DecodeFormatManager.parseDecodeFormats(inputUri);
            }
            characterSet = intent.getStringExtra(Intents.Scan.CHARACTER_SET);
        } else {
            source = IntentSource.NONE;
            decodeFormats = null;
            characterSet = null;
        }
        beepManager.updatePrefs();
        ambientLightManager.start(cameraManager);
        inactivityTimer.onResume();

    }

    private void resetStatusView() {
        viewfinderView.setVisibility(View.VISIBLE);
        lastResult = null;
    }

    private void initCamera(SurfaceHolder surfaceHolder) {
        if (surfaceHolder == null) {
            throw new IllegalStateException("No SurfaceHolder provided");
        }
        if (cameraManager.isOpen()) {
            Log.w(TAG, "initCamera() while already open -- late SurfaceView callback?");
            return;
        }
        try {
            cameraManager.openDriver(surfaceHolder);
            // Creating the handler starts the preview, which can also throw a RuntimeException.
            if (handler == null) {
                handler = new CaptureActivityHandler(this, decodeFormats, decodeHints, characterSet, cameraManager);
            }
            decodeOrStoreSavedBitmap(null, null);
        } catch (IOException ioe) {
            Log.w(TAG, ioe);
            displayFrameworkBugMessageAndExit();
        } catch (RuntimeException e) {
            // Barcode Scanner has seen crashes in the wild of this variety:
            // java.?lang.?RuntimeException: Fail to connect to camera service
            Log.w(TAG, "Unexpected error initializing camera", e);
            displayFrameworkBugMessageAndExit();
        }
    }

    private static boolean isZXingURL(String dataString) {
        if (dataString == null) {
            return false;
        }
        for (String url : ZXING_URLS) {
            if (dataString.startsWith(url)) {
                return true;
            }
        }
        return false;
    }

    private void decodeOrStoreSavedBitmap(Bitmap bitmap, Result result) {
        // Bitmap isn't used yet -- will be used soon
        if (handler == null) {
            savedResultToShow = result;
        } else {
            if (result != null) {
                savedResultToShow = result;
            }
            if (savedResultToShow != null) {
                Message message = Message.obtain(handler, R.id.decode_succeeded, savedResultToShow);
                handler.sendMessage(message);
            }
            savedResultToShow = null;
        }
    }

    private void displayFrameworkBugMessageAndExit() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.app_name));
        builder.setMessage(getString(R.string.msg_camera_framework_bug));
        builder.setPositiveButton(R.string.button_ok, new FinishListener(this));
        builder.setOnCancelListener(new FinishListener(this));
        builder.show();
    }

    /**
     * 获取选取的图片的绝对地址
     *
     * @param c
     * @param uri
     * @return
     */
    private String getRealFilePath(Context c, Uri uri) {
        String result;
        Cursor cursor = c.getContentResolver().query(uri,
                new String[]{MediaStore.Images.ImageColumns.DATA},
                null, null, null);
        if (cursor == null) {
            result = uri.getPath();
        } else {
            cursor.moveToFirst();
            int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(index);
            cursor.close();
        }
        return result;
    }

    /**
     * 启动线程解析二维码图片
     *
     * @param path
     */
    private void parsePhoto(String path) {
        //启动线程完成图片扫码
        new QrCodeAsyncTask(this, path).execute(path);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (holder == null) {
            Log.e(TAG, "*** WARNING *** surfaceCreated() gave us a null surface!");
        }
        if (!hasSurface) {
            hasSurface = true;
            initCamera(holder);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // do nothing
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;
    }

    /**
     * A valid barcode has been found, so give an indication of success and show the results.
     *
     * @param rawResult   The contents of the barcode.
     * @param scaleFactor amount by which thumbnail was scaled
     * @param barcode     A greyscale bitmap of the camera data which was decoded.
     */
    public void handleDecode(Result rawResult, Bitmap barcode, float scaleFactor) {
        inactivityTimer.onActivity();
        lastResult = rawResult;
        beepManager.playBeepSoundAndVibrate();
        if (barcode != null) {
            viewfinderView.drawResultBitmap(barcode);
            handleDecodeInternally(rawResult, barcode);
            drawResultPoints(barcode, scaleFactor, rawResult);
        }

        switch (source) {
            case NATIVE_APP_INTENT:
            case PRODUCT_SEARCH_LINK:
                handleDecodeExternally(rawResult, barcode);
                break;
            case ZXING_LINK:
                if (returnUrlTemplate == null) {
                    handleDecodeInternally(rawResult, barcode);
                } else {
                    handleDecodeExternally(rawResult, barcode);
                }
                break;
            case NONE:
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
                if (barcode != null && prefs.getBoolean(PreferencesActivity.KEY_BULK_MODE, false)) {
                    Toast.makeText(getApplicationContext(),
                            getResources().getString(R.string.msg_bulk_mode_scanned) + " (" + rawResult.getText() + ')',
                            Toast.LENGTH_SHORT).show();
                    // Wait a moment or else it will scan the same barcode continuously about 3 times
                    restartPreviewAfterDelay(BULK_MODE_SCAN_DELAY_MS);
                } else {
                    handleDecodeInternally(rawResult, barcode);
                }
                break;
            default:
        }
    }

    /**
     * Put up our own UI for how to handle the decoded contents.
     *
     * @param rawResult
     * @param barcode
     */
    private void handleDecodeInternally(Result rawResult, Bitmap barcode) {
        String result = ResultParser.parseResult(rawResult).getDisplayResult().toString().trim();
        handleResult(result);
    }

    /**
     * Superimpose a line for 1D or dots for 2D to highlight the key features of the barcode.
     *
     * @param barcode     A bitmap of the captured image.
     * @param scaleFactor amount by which thumbnail was scaled
     * @param rawResult   The decoded results which contains the points to draw.
     */
    private void drawResultPoints(Bitmap barcode, float scaleFactor, Result rawResult) {
        ResultPoint[] points = rawResult.getResultPoints();
        if (points != null && points.length > 0) {
            Canvas canvas = new Canvas(barcode);
            Paint paint = new Paint();
            paint.setColor(getResources().getColor(R.color.result_points));
            if (points.length == 2) {
                paint.setStrokeWidth(4.0f);
                drawLine(canvas, paint, points[0], points[1], scaleFactor);
            } else if (points.length == 4 &&
                    (rawResult.getBarcodeFormat() == BarcodeFormat.UPC_A ||
                            rawResult.getBarcodeFormat() == BarcodeFormat.EAN_13)) {
                // Hacky special case -- draw two lines, for the barcode and metadata
                drawLine(canvas, paint, points[0], points[1], scaleFactor);
                drawLine(canvas, paint, points[2], points[3], scaleFactor);
            } else {
                paint.setStrokeWidth(10.0f);
                for (ResultPoint point : points) {
                    if (point != null) {
                        canvas.drawPoint(scaleFactor * point.getX(), scaleFactor * point.getY(), paint);
                    }
                }
            }
        }
    }

    /**
     * Briefly show the contents of the barcode, then handle the result outside Barcode Scanner.
     * Barcode Scanner.
     */
    private void handleDecodeExternally(Result rawResult, Bitmap barcode) {

        if (barcode != null) {
            viewfinderView.drawResultBitmap(barcode);
        }

        switch (source) {
            case NATIVE_APP_INTENT:
                // Hand back whatever action they requested - this can be changed to Intents.Scan.ACTION when
                // the deprecated intent is retired.
                Intent intent = new Intent(getIntent().getAction());
                intent.addFlags(Intents.FLAG_NEW_DOC);
                intent.putExtra(Intents.Scan.RESULT, rawResult.toString());
                intent.putExtra(Intents.Scan.RESULT_FORMAT, rawResult.getBarcodeFormat().toString());
                byte[] rawBytes = rawResult.getRawBytes();
                if (rawBytes != null && rawBytes.length > 0) {
                    intent.putExtra(Intents.Scan.RESULT_BYTES, rawBytes);
                }
                Map<ResultMetadataType, ?> metadata = rawResult.getResultMetadata();
                if (metadata != null) {
                    if (metadata.containsKey(ResultMetadataType.UPC_EAN_EXTENSION)) {
                        intent.putExtra(Intents.Scan.RESULT_UPC_EAN_EXTENSION,
                                metadata.get(ResultMetadataType.UPC_EAN_EXTENSION).toString());
                    }
                    Number orientation = (Number) metadata.get(ResultMetadataType.ORIENTATION);
                    if (orientation != null) {
                        intent.putExtra(Intents.Scan.RESULT_ORIENTATION, orientation.intValue());
                    }
                    String ecLevel = (String) metadata.get(ResultMetadataType.ERROR_CORRECTION_LEVEL);
                    if (ecLevel != null) {
                        intent.putExtra(Intents.Scan.RESULT_ERROR_CORRECTION_LEVEL, ecLevel);
                    }
                    @SuppressWarnings("unchecked")
                    Iterable<byte[]> byteSegments = (Iterable<byte[]>) metadata.get(ResultMetadataType.BYTE_SEGMENTS);
                    if (byteSegments != null) {
                        int i = 0;
                        for (byte[] byteSegment : byteSegments) {
                            intent.putExtra(Intents.Scan.RESULT_BYTE_SEGMENTS_PREFIX + i, byteSegment);
                            i++;
                        }
                    }
                }
                sendReplyMessage(R.id.return_scan_result, intent, INTENT_RESULT_DURATION);
                break;

            case PRODUCT_SEARCH_LINK:
                // Reformulate the URL which triggered us into a query, so that the request goes to the same
                // TLD as the scan URL.
                sendReplyMessage(R.id.launch_product_query, null, INTENT_RESULT_DURATION);
                break;

            case ZXING_LINK:
                sendReplyMessage(R.id.launch_product_query, null, INTENT_RESULT_DURATION);
                break;
            default:
        }
    }

    private void handleResult(String result) {
        Intent intent = new Intent();
        intent.putExtra(SCAN_RESULT, result);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    private static void drawLine(Canvas canvas, Paint paint, ResultPoint a, ResultPoint b, float scaleFactor) {
        if (a != null && b != null) {
            canvas.drawLine(scaleFactor * a.getX(),
                    scaleFactor * a.getY(),
                    scaleFactor * b.getX(),
                    scaleFactor * b.getY(),
                    paint);
        }
    }

    private void sendReplyMessage(int id, Object arg, long delayMS) {
        if (handler != null) {
            Message message = Message.obtain(handler, id, arg);
            if (delayMS > 0L) {
                handler.sendMessageDelayed(message, delayMS);
            } else {
                handler.sendMessage(message);
            }
        }
    }

    public void drawViewfinder() {
        viewfinderView.drawViewfinder();
    }

    /**
     * 处理图片二维码解析的数据
     *
     * @param s
     */
    public void handleQrCode(String s) {
        if (null == s) {
            Toast.makeText(getApplicationContext(), "图片格式有误", Toast.LENGTH_SHORT).show();
        } else {
            // 识别出图片二维码/条码，内容为s
            //handleResult(s);
            Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * AsyncTask 静态内部类，防止内存泄漏
     */
    static class QrCodeAsyncTask extends AsyncTask<String, Integer, String> {
        private WeakReference<Activity> mWeakReference;
        private String path;

        public QrCodeAsyncTask(Activity activity, String path) {
            mWeakReference = new WeakReference<>(activity);
            this.path = path;
        }

        @Override
        protected String doInBackground(String... strings) {
            // 解析二维码/条码
            return QRCodeDecoder.syncDecodeQRCode(path);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //识别出图片二维码/条码，内容为s
            CaptureActivity activity = (CaptureActivity) mWeakReference.get();
            if (activity != null) {
                activity.handleQrCode(s);
            }
        }
    }

}
