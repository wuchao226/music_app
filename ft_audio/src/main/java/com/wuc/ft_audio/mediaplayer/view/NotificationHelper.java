package com.wuc.ft_audio.mediaplayer.view;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.widget.RemoteViews;

import com.wuc.ft_audio.R;
import com.wuc.ft_audio.app.AudioHelper;
import com.wuc.ft_audio.mediaplayer.core.AudioController;
import com.wuc.ft_audio.mediaplayer.core.MusicService;
import com.wuc.ft_audio.mediaplayer.db.GreenDaoHelper;
import com.wuc.ft_audio.mediaplayer.model.AudioBean;
import com.wuc.lib_image_loader.ImageLoaderManager;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

/**
 * @author: wuchao
 * @date: 2019-10-10 11:38
 * @desciption: 音乐 Notification 帮助类，完成 Notification 的创建，功能封装，Notification 逻辑处理等。
 */
public class NotificationHelper {

    public static final String CHANNEL_ID = "channel_id_audio";
    public static final String CHANNEL_NAME = "channel_name_audio";
    public static final int NOTIFICATION_ID = 0x111;
    /**
     * 最终的Notification显示类
     */
    private Notification mNotification;
    /**
     * 大布局
     */
    private RemoteViews mRemoteViews;
    /**
     * 小布局
     */
    private RemoteViews mSmallRemoteViews;

    private NotificationManagerCompat mNotificationManager;
    private NotificationHelperListener mListener;
    private String packageName;
    /**
     * 当前要播的歌曲Bean
     */
    private AudioBean mAudioBean;

    private NotificationHelper() {
    }

    public static NotificationHelper getInstance() {
        return Holder.INSTANCE;
    }

    public void init(NotificationHelperListener listener) {
//        mNotificationManager = (NotificationManagerCompat) AudioHelper.getContext()
//                .getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager = NotificationManagerCompat.from(AudioHelper.getContext());
        packageName = AudioHelper.getContext().getPackageName();
        mAudioBean = AudioController.getInstance().getNowPlaying();
        initNotification();
        mListener = listener;
        if (mListener != null) {
            mListener.onNotificationInit();
        }
    }

    /**
     * 创建Notification,
     */
    private void initNotification() {
        if (mNotification == null) {
            //首先创建布局
            initRemoteViews();
            //再构建Notification
            Intent intent = new Intent(AudioHelper.getContext(), MusicPlayerActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(AudioHelper.getContext(), 0, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            //适配安卓8.0的消息渠道, 此处必须兼容android O设备，否则系统版本在O以上可能不展示通知栏
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel =
                        new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
                //闪光灯
                channel.enableLights(false);
                //是否允许震动
                channel.enableVibration(false);
                mNotificationManager.createNotificationChannel(channel);
            }
            NotificationCompat.Builder builder =
                    new NotificationCompat.Builder(AudioHelper.getContext(), CHANNEL_ID)
                            // 设置通知栏被点击时的事件
                            .setContentIntent(pendingIntent)
                            // 设置通知栏的小图标,必需设置，否则crash
                            .setSmallIcon(R.mipmap.ic_launcher)
                            // 设置优先级，低优先级可能被隐藏
                            .setPriority(NotificationCompat.PRIORITY_HIGH)
                            //大布局
                            .setCustomBigContentView(mRemoteViews)
                            //正常布局，两个布局可以切换
                            .setContent(mSmallRemoteViews);
            mNotification = builder.build();
            //显示Notification的加载状态
            showLoadStatus(mAudioBean);
        }
    }

    /**
     * 创建Notification的布局,默认布局为Loading状态
     */
    private void initRemoteViews() {
        int layoutId = R.layout.notification_big_layout;
        mRemoteViews = new RemoteViews(packageName, layoutId);
        mRemoteViews.setTextViewText(R.id.title_view, mAudioBean.name);
        mRemoteViews.setTextViewText(R.id.tip_view, mAudioBean.album);


        int smallLayoutId = R.layout.notification_small_layout;
        mSmallRemoteViews = new RemoteViews(packageName, smallLayoutId);
        mSmallRemoteViews.setTextViewText(R.id.title_view, mAudioBean.name);
        mSmallRemoteViews.setTextViewText(R.id.tip_view, mAudioBean.album);

        //点击播放按钮广播
        Intent playIntent = new Intent(MusicService.NotificationReceiver.ACTION_STATUS_BAR);
        playIntent.putExtra(MusicService.NotificationReceiver.EXTRA, MusicService.NotificationReceiver.EXTRA_PLAY);
        PendingIntent playPendingIntent =
                PendingIntent.getBroadcast(AudioHelper.getContext(), 1, playIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);
        mRemoteViews.setOnClickPendingIntent(R.id.play_view, playPendingIntent);
        mRemoteViews.setImageViewResource(R.id.play_view, R.mipmap.note_btn_play_white);
        mSmallRemoteViews.setOnClickPendingIntent(R.id.play_view, playPendingIntent);
        mSmallRemoteViews.setImageViewResource(R.id.play_view, R.mipmap.note_btn_play_white);

        //点击上一首按钮广播
        Intent previousIntent = new Intent(MusicService.NotificationReceiver.ACTION_STATUS_BAR);
        previousIntent.putExtra(MusicService.NotificationReceiver.EXTRA,
                MusicService.NotificationReceiver.EXTRA_PRE);
        PendingIntent previousPendingIntent =
                PendingIntent.getBroadcast(AudioHelper.getContext(), 2, previousIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);
        mRemoteViews.setOnClickPendingIntent(R.id.previous_view, previousPendingIntent);
        mRemoteViews.setImageViewResource(R.id.previous_view, R.mipmap.note_btn_pre_white);

        //点击下一首按钮广播
        Intent nextIntent = new Intent(MusicService.NotificationReceiver.ACTION_STATUS_BAR);
        nextIntent.putExtra(MusicService.NotificationReceiver.EXTRA,
                MusicService.NotificationReceiver.EXTRA_NEXT);
        PendingIntent nextPendingIntent =
                PendingIntent.getBroadcast(AudioHelper.getContext(), 3, nextIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);
        mRemoteViews.setOnClickPendingIntent(R.id.next_view, nextPendingIntent);
        mRemoteViews.setImageViewResource(R.id.next_view, R.mipmap.note_btn_next_white);
        mSmallRemoteViews.setOnClickPendingIntent(R.id.next_view, nextPendingIntent);
        mSmallRemoteViews.setImageViewResource(R.id.next_view, R.mipmap.note_btn_next_white);

        //点击收藏按钮广播
        Intent favouriteIntent = new Intent(MusicService.NotificationReceiver.ACTION_STATUS_BAR);
        favouriteIntent.putExtra(MusicService.NotificationReceiver.EXTRA,
                MusicService.NotificationReceiver.EXTRA_FAV);
        PendingIntent favouritePendingIntent =
                PendingIntent.getBroadcast(AudioHelper.getContext(), 4, favouriteIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);
        mRemoteViews.setOnClickPendingIntent(R.id.favourite_view, favouritePendingIntent);
    }

    /**
     * 显示Notification的加载状态
     */
    public void showLoadStatus(AudioBean bean) {
        //防止空指针crash
        mAudioBean = bean;
        if (mRemoteViews != null) {
            mRemoteViews.setImageViewResource(R.id.play_view, R.mipmap.note_btn_pause_white);
            mRemoteViews.setTextViewText(R.id.title_view, mAudioBean.name);
            mRemoteViews.setTextViewText(R.id.tip_view, mAudioBean.album);
            //为Notification中的ImageView加载图片
            ImageLoaderManager.getInstance()
                    .displayImageForNotification(AudioHelper.getContext(), mRemoteViews, R.id.image_view,
                            mNotification, NOTIFICATION_ID, mAudioBean.albumPic);
            //更新收藏view
            if (null != GreenDaoHelper.selectFavourite(mAudioBean)) {
                mRemoteViews.setImageViewResource(R.id.favourite_view, R.mipmap.note_btn_loved);
            } else {
                mRemoteViews.setImageViewResource(R.id.favourite_view, R.mipmap.note_btn_love_white);
            }

            //小布局也要更新
            mSmallRemoteViews.setImageViewResource(R.id.play_view, R.mipmap.note_btn_pause_white);
            mSmallRemoteViews.setTextViewText(R.id.title_view, mAudioBean.name);
            mSmallRemoteViews.setTextViewText(R.id.tip_view, mAudioBean.album);
            ImageLoaderManager.getInstance()
                    .displayImageForNotification(AudioHelper.getContext(), mSmallRemoteViews, R.id.image_view,
                            mNotification, NOTIFICATION_ID, mAudioBean.albumPic);
            mNotificationManager.notify(NOTIFICATION_ID, mNotification);
        }
    }

    /**
     * 更新播放状态
     */
    public void showPlayStatus() {
        if (mRemoteViews != null) {
            mRemoteViews.setImageViewResource(R.id.play_view, R.mipmap.note_btn_pause_white);
            mSmallRemoteViews.setImageViewResource(R.id.play_view, R.mipmap.note_btn_pause_white);
            mNotificationManager.notify(NOTIFICATION_ID, mNotification);
        }
    }

    /**
     * 更新暂停状态
     */
    public void showPauseStatus() {
        if (mRemoteViews != null) {
            mRemoteViews.setImageViewResource(R.id.play_view, R.mipmap.note_btn_play_white);
            mSmallRemoteViews.setImageViewResource(R.id.play_view, R.mipmap.note_btn_play_white);
            mNotificationManager.notify(NOTIFICATION_ID, mNotification);
        }
    }

    /**
     * 更新收藏状态
     */
    public void changeFavouriteStatus(boolean isFavourite) {
        if (mRemoteViews != null) {
            mRemoteViews.setImageViewResource(R.id.favourite_view,
                    isFavourite ? R.mipmap.note_btn_loved : R.mipmap.note_btn_love_white);
            mNotificationManager.notify(NOTIFICATION_ID, mNotification);
        }
    }

    public Notification getNotification() {
        return mNotification;
    }

    /**
     * 与音乐service的回调通信
     */
    public interface NotificationHelperListener {
        /**
         * 回调，Notification与Service绑定
         */
        void onNotificationInit();
    }

    private static class Holder {
        private static final NotificationHelper INSTANCE = new NotificationHelper();
    }
}
