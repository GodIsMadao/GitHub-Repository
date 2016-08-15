package com.jzfy.jfchangesystem.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;
import android.util.Log;

import com.android.volley.toolbox.ImageLoader.ImageCache;
import com.nostra13.universalimageloader.cache.disc.DiskCache;

public class BitmapCache implements ImageCache {

	private static final String TAG = "MemoryCache";
	private LruCache<String, Bitmap> cache;
	private int max = 10 * 1024 * 1024;

	public BitmapCache() {
		cache = new LruCache<String, Bitmap>(max) {
			@Override
			protected int sizeOf(String key, Bitmap bitmap) {
				return bitmap.getRowBytes() * bitmap.getHeight();
			}
		};
		// 这个取单个应用最大使用内存的1/8
//		int maxSize = (int) Runtime.getRuntime().maxMemory() / 8;
//		cache = new LruCache<String, Bitmap>(maxSize) {
//			@Override
//			protected int sizeOf(String key, Bitmap value) {
//				// 这个方法一定要重写，不然缓存没有效果
//				return value.getHeight() * value.getRowBytes();
//			}
//		};

	}

	@Override
	public Bitmap getBitmap(String url) {
		Log.i(TAG, "get cache " + url);
		return cache.get(url);
	}

	@Override
	public void putBitmap(String url, Bitmap bitmap) {
		Log.i(TAG, "get cache: " + url);
		if (bitmap != null) {
			cache.put(url, bitmap);
		}
	}
}
