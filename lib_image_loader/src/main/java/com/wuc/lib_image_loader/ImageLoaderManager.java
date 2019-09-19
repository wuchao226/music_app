package com.wuc.lib_image_loader;

import android.app.Notification;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RemoteViews;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.target.NotificationTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

import static com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions.withCrossFade;

/**
 * @author: wuchao
 * @date: 2019-09-19 11:49
 * @desciption: 图处加载类，外界唯一调用类,直持为view,notifcation,appwidget加载图片
 */
public class ImageLoaderManager {
    private ImageLoaderManager() {
    }

    public static ImageLoaderManager getInstance() {
        return Holder.INSTANCE;
    }

    /**
     * 为 ImageView 加载图片
     *
     * @param imageView imageView
     * @param url       url
     */
    public void displayImageForView(ImageView imageView, String url) {
        Glide.with(imageView.getContext())
                .asBitmap()
                .load(url)
                .apply(initCommonRequestOptions())
                .transition(withCrossFade())
                .into(imageView);
    }

    private RequestOptions initCommonRequestOptions() {
        return new RequestOptions()
                .placeholder(R.mipmap.b4y)
                .error(R.mipmap.b4y)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .skipMemoryCache(false)
                .priority(Priority.NORMAL);
    }

    /**
     * 为 ViewGroup 设置背景图
     *
     * @param viewGroup viewGroup
     * @param url       url
     */
    public void displayImageForViewGroup(final ViewGroup viewGroup, String url) {
        displayImageForViewGroup(viewGroup, url, false);
    }

    /**
     * 为 ViewGroup 设置背景图并模糊处理
     *
     * @param viewGroup viewGroup
     * @param url       url
     * @param isBlur    isBlur 是否进行模糊处理
     */
    public void displayImageForViewGroup(final ViewGroup viewGroup, String url, final boolean isBlur) {
        Glide.with(viewGroup.getContext())
                .asBitmap()
                .load(url)
                .apply(initCommonRequestOptions())
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        Disposable subscribe = Observable.just(resource)
                                .map(new Function<Bitmap, Drawable>() {
                                    @Override
                                    public Drawable apply(Bitmap bitmap) throws Exception {
                                        Drawable drawable;
                                        if (isBlur) {
                                            //将 bitmap 进行模糊处理并转为 Drawable
                                            drawable = new BitmapDrawable(
                                                    Utils.doBlur(bitmap, 100, true));
                                        } else {
                                            //将 bitmap 转为 Drawable
                                            drawable = new BitmapDrawable(bitmap);
                                        }
                                        return drawable;
                                    }
                                })
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Consumer<Drawable>() {
                                    @Override
                                    public void accept(Drawable drawable) throws Exception {
                                        viewGroup.setBackground(drawable);
                                    }
                                });
                    }
                });
    }

    /**
     * 为 ImageView 加载圆形图片
     *
     * @param imageView imageView
     * @param url       url
     */
    public void displayImageForCircle(final ImageView imageView, String url) {
        Glide.with(imageView.getContext())
                .asBitmap()
                .load(url)
                .apply(initCommonRequestOptions())
                .into(new BitmapImageViewTarget(imageView) {
                    //将 ImageView 包装成 Target
                    @Override
                    protected void setResource(Bitmap resource) {
                        RoundedBitmapDrawable circleBitmapDrawable =
                                RoundedBitmapDrawableFactory.create(imageView.getResources(), resource);
                        circleBitmapDrawable.setCircular(true);
                        imageView.setImageDrawable(circleBitmapDrawable);
                    }
                });
    }

    /**
     * 为notification加载图
     *
     * @param context        context
     * @param rv             RemoteViews
     * @param id             Notification 中view 的id
     * @param notification   notification
     * @param notificationId notificationId
     * @param url            url
     */
    public void displayImageForNotification(Context context, RemoteViews rv, int id,
                                            Notification notification, int notificationId, String url) {
        this.displayImageForTarget(context,
                initNotificationTarget(context, id, rv, notification, notificationId), url);
    }

    /**
     * 为非 view 加载图片
     */
    private void displayImageForTarget(Context context, Target target, String url) {
        Glide.with(context)
                .asBitmap()
                .load(url)
                .apply(initCommonRequestOptions())
                .transition(withCrossFade())
                .fitCenter()
                .into(target);
    }

    /**
     * 初始化Notification Target
     */
    private Target initNotificationTarget(Context context, int id, RemoteViews rv, Notification notification, int notificationId) {
        return new NotificationTarget(context, id, rv, notification, notificationId);
    }

    private static class Holder {
        private static final ImageLoaderManager INSTANCE = new ImageLoaderManager();
    }
}
