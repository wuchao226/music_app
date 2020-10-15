package com.wuc.ft_audio.mediaplayer.view.adapter;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import com.wuc.ft_audio.R;
import com.wuc.ft_audio.mediaplayer.core.AudioController;
import com.wuc.ft_audio.mediaplayer.model.AudioBean;
import com.wuc.lib_image_loader.ImageLoaderManager;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

/**
 * @author: wuchao
 * @date: 2019-10-12 17:46
 * @desciption:
 */
public class MusicPagerAdapter extends PagerAdapter {

    private Context mContext;
    /*
     * data
     */
    private ArrayList<AudioBean> mAudioBeans;
    private SparseArray<ObjectAnimator> mAnims = new SparseArray<>();
    private MusicCallback mCallback;

    public MusicPagerAdapter(Context context, ArrayList<AudioBean> audioBeans, MusicCallback callback) {
        mContext = context;
        mAudioBeans = audioBeans;
        mCallback = callback;
    }

    public ObjectAnimator getAnim(int pos) {
        return mAnims.get(pos);
    }

    @Override
    public int getCount() {
        return mAudioBeans != null ? mAudioBeans.size() : 0;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View rootView = LayoutInflater.from(mContext).inflate(R.layout.indictor_item_view, null);
        ImageView imageView = rootView.findViewById(R.id.circle_view);
        container.addView(rootView);
        ImageLoaderManager.getInstance().displayImageForCircle(imageView, mAudioBeans.get(position).albumPic);
        //只在无动化时创建// 将动画缓存起来
        mAnims.put(position, createAnim(rootView));
        return rootView;
    }

    /**
     * 创建旋转动画
     */
    private ObjectAnimator createAnim(View view) {
        view.setRotation(0);
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, View.ROTATION.getName(), 0, 360);
        animator.setDuration(10000);
        animator.setInterpolator(new LinearInterpolator());
        animator.setRepeatCount(ValueAnimator.INFINITE);
        if (AudioController.getInstance().isStartState()) {
            animator.start();
        }
        return animator;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        super.destroyItem(container, position, object);
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    /**
     * 与IndictorView回调,暂时没用到
     */
    public interface MusicCallback {
        void onPlayStatus();

        void onPauseStatus();
    }
}
