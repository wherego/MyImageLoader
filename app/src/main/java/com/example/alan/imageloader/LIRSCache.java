package com.example.alan.imageloader;

import android.graphics.Bitmap;
import android.util.Log;
import android.util.LruCache;

/**
 * Created by Alan on 16/3/29.
 */
public class LIRSCache {
    private static final String TAG = "LIRSCache";
    public LruCache<String, Bitmap> L1, L2;

    public LIRSCache() {
        int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        int cacheSize = maxMemory / 8;

        L1 = new LruCache<String, Bitmap>(cacheSize * 2) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getRowBytes() * value.getHeight() / 1024;
            }

            @Override
            protected void entryRemoved(boolean evicted, String key, Bitmap oldValue, Bitmap newValue) {
                //如果从L1中淘汰，则加入L2
                if (evicted) {
                    L2.put(key, oldValue);
                }
            }
        };

        L2 = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getRowBytes() * value.getHeight() / 1024;
            }
        };
    }

    public Bitmap put(String key, Bitmap value) {
        if (L2.get(key) == null) {
            if (L1.get(key) == null) {
                return L2.put(key, value);
            } else {
                return L1.put(key, value);
            }
        } else {
            L1.put(key, value);
            return L2.remove(key);
        }
    }

    public Bitmap get(String key) {
        Bitmap bitmap = null;
        if ((bitmap = L2.get(key)) == null) {
            bitmap = L1.get(key);
            if (bitmap != null) {
                Log.i(TAG, "LIRS L1 hit");
            }
            return bitmap;
        } else {
            L1.put(key, bitmap);
            Log.i(TAG, "LIRS L2 hit");
            return L2.remove(key);
        }
    }
}
