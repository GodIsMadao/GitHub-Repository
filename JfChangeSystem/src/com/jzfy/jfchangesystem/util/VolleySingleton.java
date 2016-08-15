package com.jzfy.jfchangesystem.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

public class VolleySingleton {
	private static VolleySingleton volleySingleton;
	private RequestQueue mRequestQueue;
	private ImageLoader mImageLoader;
	private Context mContext;
	private static final String TAG = "MemoryCache";

	public VolleySingleton(Context context) {
		this.mContext = context;
		mRequestQueue = getRequestQueue();
		mImageLoader = new ImageLoader(mRequestQueue,
				new ImageLoader.ImageCache() {
					private final LruCache<String, Bitmap> cache = new LruCache<String, Bitmap>(
							20){
						@Override
						protected int sizeOf(String key, Bitmap bitmap) {
							return bitmap.getRowBytes() * bitmap.getHeight();
						}
						
					};

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
//					@Override
//					public Bitmap getBitmap(String url) {
//						return cache.get(url);
//					}
//
//					@Override
//					public void putBitmap(String url, Bitmap bitmap) {
//						cache.put(url, bitmap);
//					}
				});
	}

	public static synchronized VolleySingleton getVolleySingleton(
			Context context) {
		if (volleySingleton == null) {
			volleySingleton = new VolleySingleton(context);
		}
		return volleySingleton;
	}

	public RequestQueue getRequestQueue() {
		if (mRequestQueue == null) {
			mRequestQueue = Volley.newRequestQueue(mContext
					.getApplicationContext());
		}
		return mRequestQueue;
	}

	public <T> void addToRequestQueue(Request<T> req) {
		getRequestQueue().add(req);
	}

	public ImageLoader getImageLoader() {
		return mImageLoader;
	}
}
