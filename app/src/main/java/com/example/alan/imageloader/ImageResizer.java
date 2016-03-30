package com.example.alan.imageloader;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.FileDescriptor;

/**
 * Created by alan on 2016/3/15.
 */
public class ImageResizer {
    private  static final String TAG = "ImageResizer";

    public ImageResizer() {

    }

    /**
     * 从文件描述符解码bitmap。首先会检测图片大小和所请求的长宽之间的差别，
     * 得出一个适当的缩放比例后({@link #calculateInSampleSize(BitmapFactory.Options, int, int)})，
     * 将缩放后的bitmap返回。
     * @param fd FileDescriptor of the image.
     * @param reqWidth 0 if we donnot wanna resize the image.
     * @param reqHeight 0 if we donnot wanna resize the image.
     * @return Bitmap which has been resized.
     */
    public Bitmap decodeSampledBitmapFromFileDescriptor(FileDescriptor fd, int reqWidth,
                                                        int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFileDescriptor(fd, null, options);

        int size = calculateInSampleSize(options,  reqWidth, reqHeight);
        options.inSampleSize = size;
        Log.i(TAG, "Resize Image:" + size);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFileDescriptor(fd, null, options);
    }

    /**
     *
     * @param options
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        if (reqWidth == 0 || reqHeight == 0) {
            return 1;
        }

        final int height = options.outHeight;
        final int width = options.outWidth;
        Log.i(TAG, "Image witdh:" + width + " Image height:" + height
                + " reqWidth:" + reqWidth + "reqHeight:" + reqHeight);
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            while((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    /**
     *
     * @param res
     * @param resId
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    public Bitmap decodeSampledBitmapFromResource(Resources res, int resId, int reqWidth,
                                                  int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }
}

