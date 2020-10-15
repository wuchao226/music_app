package com.wuc.ft_home.view.friend.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import androidx.appcompat.widget.AppCompatImageView;
import com.wuc.ft_home.R;
import com.wuc.ft_home.model.friend.FriendBodyValue;
import com.wuc.lib_base.ft_audio.service.impl.AudioImpl;
import com.wuc.lib_common_ui.MultiImageViewLayout;
import com.wuc.lib_common_ui.recyclerview.MultiItemTypeAdapter;
import com.wuc.lib_common_ui.recyclerview.base.ItemViewDelegate;
import com.wuc.lib_common_ui.recyclerview.base.ViewHolder;
import com.wuc.lib_image_loader.ImageLoaderManager;
import com.wuc.lib_video.videoplayer.core.VideoManager;
import com.wuc.lib_video.videoplayer.core.VideoManagerInterface;
import com.wuc.lib_video.videoplayer.core.view.CustomVideoView;
import com.wuc.lib_video.videoplayer.module.VideoValue;
import java.util.List;

/**
 * @author: wuchao
 * @date: 2019-10-22 10:19
 * @desciption:
 */
public class FriendRecyclerAdapter extends MultiItemTypeAdapter {
  /**
   * 音乐类型
   */
  public static final int MUSIC_TYPE = 0x01;
  /**
   * 视频类型
   */
  public static final int VIDEO_TYPE = 0x02;

  private Context mContext;
  private VideoManager mVideoManager;

  public FriendRecyclerAdapter(Context context, List<FriendBodyValue> datas) {
    super(context, datas);
    mContext = context;
    addItemViewDelegate(MUSIC_TYPE, new MusicItemDelegate());
    addItemViewDelegate(VIDEO_TYPE, new VideoItemDelegate());
  }

  /**
   * 根据滑动距离来判断是否可以自动播放, 出现超过50%自动播放，离开超过50%,自动暂停
   */
  public void updateVideoInScrollView() {
    if (mVideoManager != null) {
      mVideoManager.updateVideoInScrollView();
    }
  }

  /**
   * 图片类型item
   */
  private class MusicItemDelegate implements ItemViewDelegate<FriendBodyValue> {
    @Override
    public int getItemViewLayoutId() {
      return R.layout.item_friend_list_picture_layout;
    }

    @Override
    public boolean isForViewType(FriendBodyValue item, int position) {
      return item.type == FriendRecyclerAdapter.MUSIC_TYPE;
    }

    @Override
    public void convert(ViewHolder holder, final FriendBodyValue recommandBodyValue, int position) {
      holder.setText(R.id.name_view, recommandBodyValue.name + " 分享单曲:");
      holder.setText(R.id.fansi_view, recommandBodyValue.fans + "粉丝");
      holder.setText(R.id.text_view, recommandBodyValue.text);
      holder.setText(R.id.zan_view, recommandBodyValue.zan);
      holder.setText(R.id.message_view, recommandBodyValue.msg);
      holder.setText(R.id.audio_name_view, recommandBodyValue.audioBean.name);
      holder.setText(R.id.audio_author_view, recommandBodyValue.audioBean.album);
      holder.setOnClickListener(R.id.album_layout, new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          //调用播放器装饰类
          AudioImpl.getInstance().addAudio((Activity) mContext, recommandBodyValue.audioBean);
        }
      });
      holder.setOnClickListener(R.id.guanzhu_view, new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          // if (!UserManager.getInstance().hasLogined()) {
          //     //goto login
          //     LoginActivity.start(mContext);
          // }
        }
      });
      AppCompatImageView avatar = holder.getView(R.id.photo_view);
      ImageLoaderManager.getInstance().displayImageForCircle(avatar, recommandBodyValue.avatr);
      AppCompatImageView albumPicView = holder.getView(R.id.album_view);
      ImageLoaderManager.getInstance()
          .displayImageForView(albumPicView, recommandBodyValue.audioBean.albumPic);

      MultiImageViewLayout imageViewLayout = holder.getView(R.id.image_layout);
      imageViewLayout.setList(recommandBodyValue.pics);
      imageViewLayout.setOnItemClickListener(new MultiImageViewLayout.OnItemClickListener() {
        @Override
        public void onItemClick(View view, int position) {

        }
      });
    }
  }

  /**
   * 视频类型item
   */
  private class VideoItemDelegate implements ItemViewDelegate<FriendBodyValue> {

    @Override
    public int getItemViewLayoutId() {
      return R.layout.item_friend_list_video_layout;
    }

    @Override
    public boolean isForViewType(FriendBodyValue item, int position) {
      return item.type == FriendRecyclerAdapter.VIDEO_TYPE;
    }

    @Override
    public void convert(ViewHolder holder, FriendBodyValue recommandBodyValue, int position) {
      RelativeLayout videoGroup = holder.getView(R.id.video_layout);
      VideoValue videoValue = new VideoValue();
      videoValue.resource = recommandBodyValue.videoUrl;
      videoValue.thumb = recommandBodyValue.avatr;
      mVideoManager = new VideoManager(videoGroup, videoValue, new CustomVideoView.FrameImageLoadListener() {
        @Override
        public void onStartFrameLoad(String url, CustomVideoView.ImageLoaderListener listener) {
          listener.onLoadingComplete(ImageLoaderManager.getInstance().getBitmapForUrl(mContext, url));
        }
      });
      mVideoManager.setListener(new VideoManagerInterface() {
        @Override
        public void onVideoSuccess() {

        }

        @Override
        public void onVideoFailed() {

        }

        @Override
        public void onVideoComplete() {

        }

        @Override
        public void onClickVideo(String url) {
                    /*Intent intent = new Intent(mContext, AdBrowserActivity.class);
                    intent.putExtra(AdBrowserActivity.KEY_URL, url);
                    mContext.startActivity(intent);*/
        }
      });
      holder.setText(R.id.fansi_view, recommandBodyValue.fans + "粉丝");
      holder.setText(R.id.name_view, recommandBodyValue.name + " 分享视频");
      holder.setText(R.id.text_view, recommandBodyValue.text);
      holder.setOnClickListener(R.id.guanzhu_view, new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          // if (!UserManager.getInstance().hasLogined()) {
          //     //goto login
          //     LoginActivity.start(mContext);
          // }
        }
      });
      ImageView avatar = holder.getView(R.id.photo_view);
      ImageLoaderManager.getInstance().displayImageForCircle(avatar, recommandBodyValue.avatr);
    }
  }
}
