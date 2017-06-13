package com.example.smsmanager.utils;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.smsmanager.R;

import java.io.File;

/**
 * Created by admin on 2017/3/12.
 * 支持网络图片加载、圆角图片、圆形图片、手机本地图片、项目图片、gif图片等等
 */

public class GlideImgManager {
    public static void loadImage(Context context, String url, int erroImg, int emptyImg, ImageView iv) {
        //原生 API
        Glide.with(context).load(url).placeholder(emptyImg).error(erroImg).into(iv);
    }

    public static void loadImage(Context context, String url, ImageView iv) {
        //原生 API
        Glide.with(context).load(url).crossFade().placeholder(R.drawable.empty_photo).error(R.drawable.empty_photo).into(iv);
    }

    public static void loadGifImage(Context context, String url, ImageView iv) {
        Glide.with(context).load(url).asGif().diskCacheStrategy(DiskCacheStrategy.SOURCE).placeholder(R.drawable.empty_photo).error(R.drawable.empty_photo).into(iv);
    }


    public static void loadImage(Context context, final File file, final ImageView imageView) {
        Glide.with(context)
                .load(file)
                .into(imageView);


    }

    public static void loadImage(Context context, final int resourceId, final ImageView imageView) {
        Glide.with(context)
                .load(resourceId)
                .into(imageView);
    }
}
//使用实例
//    private void initView() {
//        //加载网络图片（普通）
//        GlideImgManager.loadImage(this, imageArr[0], imageview1);
//
//        //加载网络图片（圆角）
//        GlideImgManager.loadRoundCornerImage(this, imageArr[1], imageview2);
//
//        //加载网络图片（圆形）
//        GlideImgManager.loadCircleImage(this, imageArr[2], imageview3);
//
//        //加载项目中的图片
//        GlideImgManager.loadImage(this, R.mipmap.ic_launcher, imageview4);
//
//        //加载网络图片（GIF）
//        String gifUrl = "http://ww4.sinaimg.cn/mw690/bcc93f49gw1f6r1nce77jg207x07sx6q.gif";
//        GlideImgManager.loadGifImage(this, gifUrl, imageview5);
//    }
//
//    //加载进度监听
//    private void loadListener() {
//        Glide.with(this)
//                .load(imageArr[0])
//                .into(new GlideDrawableImageViewTarget(imageview6) {
//                    @Override
//                    public void onResourceReady(GlideDrawable drawable, GlideAnimation anim) {
//                        super.onResourceReady(drawable, anim);
//                        progressBar.setVisibility(View.GONE);
//                    }
//
//                    @Override
//                    public void onStart() {
//                        super.onStart();
//                        progressBar.setVisibility(View.VISIBLE);
//                    }
//                });
//    }
