package com.wuc.lib_video.videoplayer.core.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.wuc.lib_video.R;
import com.wuc.lib_video.videoplayer.constant.VideoConstant;
import com.wuc.lib_video.videoplayer.utils.Utils;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;

/**
 * @author: wuchao
 * @date: 2019-10-23 10:56
 * @desciption: 负责视频播放，暂停，事件触发
 */
public class CustomVideoView extends RelativeLayout implements View.OnClickListener,
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnBufferingUpdateListener,
        MediaPlayer.OnInfoListener, MediaPlayer.OnCompletionListener, TextureView.SurfaceTextureListener {

    private static final String TAG = "CustomVideoView";
    /**
     * 事件类型
     */
    private static final int TIME_MSG = 0x01;
    /**
     * 时间间隔，每隔1000毫秒发送一个TIME_MSG出来
     */
    private static final int TIME_INVAL = 100;
    /**
     * 播放器生命周期状态
     */
    private static final int STATE_ERROR = -1;
    /**
     * 闲置
     */
    private static final int STATE_IDLE = 0;
    /**
     * 播放
     */
    private static final int STATE_PLAYING = 1;
    /**
     * 暂停
     */
    private static final int STATE_PAUSING = 2;

    /**
     * 加载失败重试机制，失败3次为失败
     */
    private static final int LOAD_TOTAL_COUNT = 3;

    /**
     * UI
     */
    private ViewGroup mParentContainer;
    private RelativeLayout mPlayerView;
    private TextureView mVideoView;
    private Button mMiniPlayBtn;
    private AppCompatImageView mFullBtn;
    private AppCompatImageView mLoadingBar;
    private AppCompatImageView mFrameView;

    /**
     * 音量控制器
     */
    private AudioManager mAudioManager;
    /**
     * 显示帧数据的类
     */
    private Surface mVideoSurface;

    /**
     * 加载的视频地址
     */
    private String mUrl;
    private String mFrameURI;
    /**
     * 是否静音(小屏静音，大屏不禁)
     */
    private boolean isMute;
    /**
     * 屏幕宽度，9：16计算的高度
     */
    private int mScreenWidth, mDestinationHeight;

    /**
     * Status 状态保护
     */
    private boolean canPlay = true;
    /**
     * 播放器是否真正暂停(1、用户点击暂停，2、播放完毕暂停)
     */
    private boolean mIsRealPause;
    /**
     * 播放器是否播放完成
     */
    private boolean mIsComplete;
    /**
     * 播放次数
     */
    private int mCurrentCount;
    /**
     * 默认闲置状态
     */
    private int playerState = STATE_IDLE;

    /**
     * 播放核心类
     */
    private MediaPlayer mMediaPlayer;
    /**
     * 事件监听回调
     */
    private VideoPlayerListener mListener;
    /**
     * 监听屏幕是否锁屏
     */
    private ScreenEventReceiver mScreenReceiver;
    private FrameImageLoadListener mFrameLoadListener;
    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case TIME_MSG:
                    //还可以在这里更新progressbar
                    if (mListener != null) {
                        mListener.onBufferUpdate(getCurrentPosition());
                    }
                    sendEmptyMessageDelayed(TIME_MSG, TIME_INVAL);
                    break;
                default:
            }
        }
    };

    public CustomVideoView(Context context) {
        this(context, null);
    }

    public CustomVideoView(Context context, ViewGroup parentContainer) {
        super(context);
        mParentContainer = parentContainer;
        mAudioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
        initData();
        initView();
        registerBroadcastReceiver();
    }

    private void initData() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        if (windowManager != null) {
            windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        }
        mScreenWidth = displayMetrics.widthPixels;
        mDestinationHeight = (int) (mScreenWidth * VideoConstant.VIDEO_HEIGHT_PERCENT);
    }

    private void initView() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        mPlayerView = (RelativeLayout) inflater.inflate(R.layout.video_player_layout, this);
        mVideoView = mPlayerView.findViewById(R.id.player_video_textureView);
        mVideoView.setOnClickListener(this);
        //屏幕常亮
        mVideoView.setKeepScreenOn(true);
        //设置Texture监听
        mVideoView.setSurfaceTextureListener(this);
        //初始化小视图模式
        initSmallLayoutMode();
    }

    /**
     * 注册广播
     */
    private void registerBroadcastReceiver() {
        if (mScreenReceiver == null) {
            mScreenReceiver = new ScreenEventReceiver();
            IntentFilter filter = new IntentFilter();
            filter.addAction(Intent.ACTION_SCREEN_OFF);
            filter.addAction(Intent.ACTION_USER_PRESENT);
            getContext().registerReceiver(mScreenReceiver, filter);
        }
    }

    /**
     * 小模式状态
     */
    private void initSmallLayoutMode() {
        LayoutParams params = new LayoutParams(mScreenWidth, mDestinationHeight);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        mPlayerView.setLayoutParams(params);

        mMiniPlayBtn = mPlayerView.findViewById(R.id.small_play_btn);
        mFullBtn = mPlayerView.findViewById(R.id.full_view);
        mLoadingBar = mPlayerView.findViewById(R.id.loading_bar);
        mFrameView = mPlayerView.findViewById(R.id.framing_view);
        mMiniPlayBtn.setOnClickListener(this);
        mFullBtn.setOnClickListener(this);
    }

    private synchronized void checkMediaPlayer() {
        if (mMediaPlayer == null) {
            //每次都重新创建一个新的播放器
            mMediaPlayer = createMediaPlayer();
        }
    }

    private MediaPlayer createMediaPlayer() {
        mMediaPlayer = new MediaPlayer();
        //重置MediaPlayer对象，清空MediaPlayer中的数据
        mMediaPlayer.reset();
        //当装载流媒体完毕的时候回调。
        mMediaPlayer.setOnPreparedListener(this);
        //网络流媒体播放结束时回调
        mMediaPlayer.setOnCompletionListener(this);
        //网络流媒体的缓冲变化时回调
        mMediaPlayer.setOnBufferingUpdateListener(this);
        //发生错误时回调
        mMediaPlayer.setOnErrorListener(this);
        //指定流媒体的类型
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mMediaPlayer.setAudioAttributes(
                    new AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .build());
        }
        if (mVideoSurface != null && mVideoSurface.isValid()) {
            mMediaPlayer.setSurface(mVideoSurface);
        } else {
            stop();
        }
        return mMediaPlayer;
    }

    /**
     * 播放器是否真正暂停
     */
    public boolean isRealPause() {
        return mIsRealPause;
    }

    /**
     * 播放器是否播放完成
     */
    public boolean isComplete() {
        return mIsComplete;
    }

    /**
     * true is no voice
     *
     * @param mute mute
     */
    public void mute(boolean mute) {
        isMute = mute;
        if (mMediaPlayer != null && this.mAudioManager != null) {
            float volume = isMute ? 0.0f : 1.0f;
            mMediaPlayer.setVolume(volume, volume);
        }
    }

    /**
     * 获取当前播放器播放的位置(毫秒)
     *
     * @return 已经播放了的毫秒数
     */
    public int getCurrentPosition() {
        if (mMediaPlayer != null) {
            return mMediaPlayer.getCurrentPosition();
        }
        return 0;
    }

    /**
     * 获取文件的播放时长 (毫秒), 如果没有可用的时长, 就会返回 -1;
     */
    public int getDuration() {
        if (mMediaPlayer != null) {
            return mMediaPlayer.getDuration();
        }
        return 0;
    }

    /**
     * 全屏不显示暂停状态,后续可以整合，不必单独出一个方法
     */
    public void pauseForFullScreen() {
        if (playerState != STATE_PLAYING) {
            return;
        }
        setCurrentPlayState(STATE_PAUSING);
        if (isPlaying()) {
            mMediaPlayer.pause();
            if (!this.canPlay) {
                mMediaPlayer.seekTo(0);
            }
        }
        mHandler.removeCallbacksAndMessages(null);
    }

    /**
     * 当前播放器state
     *
     * @param state state
     */
    private void setCurrentPlayState(int state) {
        playerState = state;
    }

    /**
     * 是否正在播放
     */
    public boolean isPlaying() {
        return mMediaPlayer != null && mMediaPlayer.isPlaying();
    }

    /**
     * 暂停视频
     */
    public void pause() {
        if (playerState != STATE_PLAYING) {
            return;
        }
        setCurrentPlayState(STATE_PAUSING);
        if (isPlaying()) {
            mMediaPlayer.pause();
            if (!this.canPlay) {
                this.mMediaPlayer.seekTo(0);
            }
        }
        this.showPauseView(false);
        mHandler.removeCallbacksAndMessages(null);
    }

    /**
     * 是否显示全屏按钮
     */
    public void isShowFullBtn(boolean isShow) {
        mFullBtn.setImageResource(isShow ? R.drawable.video_mini : R.drawable.video_mini_null);
        mFullBtn.setVisibility(isShow ? VISIBLE : GONE);
    }

    /**
     * 加载视频url
     */
    public void load() {
        if (this.playerState != STATE_IDLE) {
            return;
        }
        showLoadingView();
        try {
            setCurrentPlayState(STATE_IDLE);
            checkMediaPlayer();
            mute(true);
            mMediaPlayer.setDataSource(this.mUrl);
            //开始异步加载
            mMediaPlayer.prepareAsync();
        } catch (Exception e) {
            //error后重新调用stop加载
            stop();
        }
    }

    /**
     * 停止状态
     */
    public void stop() {
        if (this.mMediaPlayer != null) {
            this.mMediaPlayer.reset();
            this.mMediaPlayer.setOnSeekCompleteListener(null);
            this.mMediaPlayer.stop();
            this.mMediaPlayer.release();
            this.mMediaPlayer = null;
        }
        mHandler.removeCallbacksAndMessages(null);
        setCurrentPlayState(STATE_IDLE);
        //满足重新加载的条件
        if (mCurrentCount < LOAD_TOTAL_COUNT) {
            mCurrentCount += 1;
            load();
        } else {
            //显示暂停状态
            showPauseView(false);
        }
    }

    /**
     * 在 MediaPlayer 通过 HTTP 下载缓冲视频流的时候回调, 用以改变视频缓冲状态
     *
     * @param percent 已经缓冲了的 或者 播放了的 媒体流百分比
     */
    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {

    }

    /**
     * 在播放器播放完成时回调
     */
    @Override
    public void onCompletion(MediaPlayer mp) {
        if (mListener != null) {
            mListener.onVideoLoadComplete();
        }
        playBack();
        setIsComplete(true);
        setIsRealPause(true);
    }

    /**
     * 播放完成回到初始状态
     */
    private void playBack() {
        setCurrentPlayState(STATE_PAUSING);
        mHandler.removeCallbacksAndMessages(null);
        if (mMediaPlayer != null) {
            mMediaPlayer.setOnSeekCompleteListener(null);
            mMediaPlayer.seekTo(0);
            mMediaPlayer.pause();
        }
        this.showPauseView(false);
    }

    public void setIsComplete(boolean complete) {
        mIsComplete = complete;
    }

    public void setIsRealPause(boolean realPause) {
        mIsRealPause = realPause;
    }

    /**
     * 显示暂停view
     */
    private void showPauseView(boolean show) {
        mFullBtn.setVisibility(show ? VISIBLE : GONE);
        mMiniPlayBtn.setVisibility(show ? GONE : VISIBLE);
        mLoadingBar.clearAnimation();
        mLoadingBar.setVisibility(GONE);
        if (!show) {
            mFrameView.setVisibility(View.VISIBLE);
            loadFrameImage();
        } else {
            mFrameView.setVisibility(View.GONE);
        }
    }

    /**
     * 异步加载定帧图
     */
    private void loadFrameImage() {
        if (mFrameLoadListener != null) {
            mFrameLoadListener.onStartFrameLoad(mFrameURI, new ImageLoaderListener() {
                @Override
                public void onLoadingComplete(Bitmap loadedImage) {
                    if (loadedImage != null) {
                        mFrameView.setScaleType(ImageView.ScaleType.FIT_XY);
                        mFrameView.setImageBitmap(loadedImage);
                    } else {
                        mFrameView.setScaleType(ImageView.ScaleType.CENTER);
                        mFrameView.setImageResource(R.drawable.video_img_error);
                    }
                }
            });
        }
    }

    /**
     * 设置视频url
     *
     * @param url url
     */
    public void setDataSource(String url) {
        this.mUrl = url;
    }

    /**
     * 设置帧图url
     *
     * @param url url
     */
    public void setFrameURI(String url) {
        mFrameURI = url;
    }

    /**
     * 跳到指定点播放
     */
    public void seekAndResume(int position) {
        if (mMediaPlayer != null) {
            showPauseView(true);
            //进入播放状态
            entryResumeState();
            mMediaPlayer.seekTo(position);
            mMediaPlayer.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
                @Override
                public void onSeekComplete(MediaPlayer mp) {
                    mMediaPlayer.start();
                    mHandler.sendEmptyMessage(TIME_MSG);
                }
            });
        }
    }

    /**
     * 进入播放状态时的状态更新
     */
    private void entryResumeState() {
        canPlay = true;
        setCurrentPlayState(STATE_PLAYING);
        setIsRealPause(false);
        setIsComplete(false);
    }

    /**
     * 销毁当前的自定义view
     */
    public void destroy() {
        if (mMediaPlayer != null) {
            mMediaPlayer.setOnSeekCompleteListener(null);
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        setCurrentPlayState(STATE_IDLE);
        mCurrentCount = 0;
        setIsComplete(false);
        setIsRealPause(false);
        unRegisterBroadcastReceiver();
        //release all message and runnable
        mHandler.removeCallbacksAndMessages(null);
        //除了播放和loading外其余任何状态都显示pause
        showPauseView(false);
    }

    /**
     * 取消广播注册
     */
    private void unRegisterBroadcastReceiver() {
        if (mScreenReceiver != null) {
            getContext().unregisterReceiver(mScreenReceiver);
        }
    }

    /**
     * 跳到指定点暂停视频
     */
    public void seekAndPause(int position) {
        if (playerState != STATE_PLAYING) {
            return;
        }
        showPauseView(false);
        setCurrentPlayState(STATE_PAUSING);
        if (isPlaying()) {
            mMediaPlayer.seekTo(position);
            mMediaPlayer.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
                @Override
                public void onSeekComplete(MediaPlayer mp) {
                    mMediaPlayer.pause();
                    mHandler.removeCallbacksAndMessages(null);
                }
            });
        }
    }

    /**
     * 是否隐藏Frame
     */
    public boolean isFrameHidden() {
        return mFrameView.getVisibility() != View.VISIBLE;
    }

    public void setVideoPlayerListener(VideoPlayerListener listener) {
        this.mListener = listener;
    }

    public void setFrameLoadListener(FrameImageLoadListener frameLoadListener) {
        this.mFrameLoadListener = frameLoadListener;
    }

    /**
     * 播放器产生异常时回调
     */
    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        this.playerState = STATE_ERROR;
        mMediaPlayer = mp;
        if (mMediaPlayer != null) {
            mMediaPlayer.reset();
        }
        if (mCurrentCount >= LOAD_TOTAL_COUNT) {
            showPauseView(false);
            if (mListener != null) {
                mListener.onVideoLoadFailed();
            }
        }
        //重新load
        this.stop();
        return true;
    }

    /**
     * 播放时出现信息或警告时回调
     *
     * @param mp
     * @param what
     * @param extra
     * @return 如果处理了信息就会返回 true, 没有处理返回false, 如果没有注册该监听, 就会忽略该信息;
     */
    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        return true;
    }

    /**
     * 播放器处于就绪状态
     */
    @Override
    public void onPrepared(MediaPlayer mp) {
        showPlayView();
        mMediaPlayer = mp;
        if (mMediaPlayer != null) {
            mMediaPlayer.setOnBufferingUpdateListener(this);
            mCurrentCount = 0;
            if (mListener != null) {
                mListener.onVideoLoadSuccess();
            }
            //满足自动播放条件，则直接播放
            if (Utils.canAutoPlay(getContext()) &&
                    Utils.getVisiblePercent(mParentContainer) > VideoConstant.VIDEO_SCREEN_PERCENT) {
                setCurrentPlayState(STATE_PAUSING);
                resume();
            } else {
                setCurrentPlayState(STATE_PLAYING);
                pause();
            }
        }
    }

    /**
     * 在view的显示发送变化时，回调此方法
     */
    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (visibility == VISIBLE && playerState == STATE_PAUSING) {
            if (isRealPause() || isComplete()) {
                pause();
            } else {
                decideCanPlay();
            }
        } else {
            pause();
        }
    }

    /**
     * 防止与父容器产出事件冲突
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return true;
    }

    /**
     * TextureView 处于就绪状态
     */
    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        mVideoSurface = new Surface(surface);
        checkMediaPlayer();
        mMediaPlayer.setSurface(mVideoSurface);
        load();
    }

    /**
     * 缓冲区大小更改时回调
     */
    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    /**
     * 即将被销毁时调用。
     *
     * @return 如果返回true，则调用此方法后，表面纹理中不会发生渲染。如果返回false，
     * 则客户端需要调用release()。
     */
    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

    @Override
    public void onClick(View v) {
        if (v == this.mMiniPlayBtn) {
            if (this.playerState == STATE_PAUSING) {
                if (Utils.getVisiblePercent(mParentContainer) > VideoConstant.VIDEO_SCREEN_PERCENT) {
                    resume();
                    if (mListener != null) {
                        this.mListener.onClickPlay();
                    }
                }
            } else {
                load();
            }
        } else if (v == this.mFullBtn) {
            if (mListener != null) {
                this.mListener.onClickFullScreenBtn();
            }
        } else if (v == mVideoView) {
            if (mListener != null) {
                this.mListener.onClickVideo();
            }
        }
    }

    /**
     * 显示播放view
     */
    private void showPlayView() {
        mLoadingBar.clearAnimation();
        mLoadingBar.setVisibility(GONE);
        mMiniPlayBtn.setVisibility(GONE);
        mFrameView.setVisibility(GONE);
    }

    /**
     * 显示loading  view
     */
    private void showLoadingView() {
        mFullBtn.setVisibility(GONE);
        mMiniPlayBtn.setVisibility(View.GONE);
        mFrameView.setVisibility(View.GONE);
        mLoadingBar.setVisibility(VISIBLE);
        AnimationDrawable anim = (AnimationDrawable) mLoadingBar.getBackground();
        anim.start();
        loadFrameImage();
    }

    /**
     * 决定能不能播放
     */
    private void decideCanPlay() {
        if (Utils.getVisiblePercent(mParentContainer) > VideoConstant.VIDEO_SCREEN_PERCENT) {
            //来回切换界面时，只有大于50，且满足自动播放条件才自动播放
            resume();
        } else {
            pause();
        }
    }

    /**
     * 恢复视频播放
     */
    public void resume() {
        if (playerState != STATE_PAUSING) {
            return;
        }
        if (!isPlaying()) {
            //进入播放状态
            entryResumeState();
            mMediaPlayer.setOnSeekCompleteListener(null);
            mMediaPlayer.start();
            mHandler.sendEmptyMessage(TIME_MSG);
            showPauseView(true);
        } else {
            showPauseView(false);
        }
    }

    /**
     * 供slot层来实现具体点击逻辑,具体逻辑还会变，
     * 如果对UI的点击没有具体监测的话可以不回调
     */
    public interface VideoPlayerListener {
        /**
         * 播放器播放到第几秒
         *
         * @param time 秒
         */
        void onBufferUpdate(int time);

        /**
         * 跳转到全屏的事件监听
         */
        void onClickFullScreenBtn();

        /**
         * 点击视频区域的事件
         */
        void onClickVideo();

        /**
         * 点击返回按钮的事件监听
         */
        void onClickBackBtn();

        /**
         * 点击播放按钮的事件监听
         */
        void onClickPlay();

        /**
         * 视频加载成功的事件监听
         */
        void onVideoLoadSuccess();

        /**
         * 视频加载失败的事件监听
         */
        void onVideoLoadFailed();

        /**
         * 视频播放完成的事件监听
         */
        void onVideoLoadComplete();
    }

    public interface FrameImageLoadListener {
        /**
         * 加载帧图
         *
         * @param url      url
         * @param listener listener
         */
        void onStartFrameLoad(String url, ImageLoaderListener listener);
    }

    public interface ImageLoaderListener {
        /**
         * 如果图片下载不成功，传null
         *
         * @param loadedImage image
         */
        void onLoadingComplete(Bitmap loadedImage);
    }

    /**
     * 监听锁屏事件的广播接收器
     */
    private class ScreenEventReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            //主动锁屏时 pause，主动解锁屏幕时 resume
            String action = intent.getAction();
            if (action != null) {
                switch (action) {
                    //识别用户进入home界面,进而启动想启动的相关服务,解锁时发出的intent.
                    case Intent.ACTION_USER_PRESENT:
                        if (playerState == STATE_PAUSING) {
                            //亮屏事件处理
                            if (isRealPause()) {
                                //手动点击暂停，回来后还暂停
                                pause();
                            } else {
                                decideCanPlay();
                            }
                        }
                        break;
                    case Intent.ACTION_SCREEN_OFF:
                        //灭屏事件处理
                        if (playerState == STATE_PLAYING) {
                            //stop();
                            pause();
                        }
                        break;
                    default:
                }
            }
        }
    }
}
