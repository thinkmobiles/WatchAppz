package com.watchappz.android.utils.image_loader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.widget.ImageView;

import com.watchappz.android.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by
 * mRogach on 12.10.2015.
 */

public final class ImageLoader {

    MemoryCache memoryCache = new MemoryCache();
    FileCache fileCache;
    private Map<ImageView, String> imageViews = Collections.synchronizedMap(new WeakHashMap<ImageView, String>());
    ExecutorService executorService;
    Handler handler = new Handler();
    private Context mContext;

    public ImageLoader(Context context) {
        mContext = context;
        fileCache = new FileCache(context);
        executorService = Executors.newFixedThreadPool(5);

    }

    final int stub_id = R.mipmap.ic_launcher;

    public void displayImage(String packageName, ImageView imageView) {
        imageViews.put(imageView, packageName);
        Bitmap bitmap = memoryCache.get(packageName);
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
        } else {
            queuePhoto(packageName, imageView);
            imageView.setImageResource(stub_id);
        }
    }

    private void queuePhoto(String packageName, ImageView imageView) {
        PhotoToLoad p = new PhotoToLoad(packageName, imageView);
        executorService.submit(new PhotosLoader(p));
    }

    private class PhotoToLoad {
        public String packageName;
        public ImageView imageView;

        public PhotoToLoad(String _packageName, ImageView i) {
            packageName = _packageName;
            imageView = i;
        }
    }

    class PhotosLoader implements Runnable {
        PhotoToLoad photoToLoad;

        PhotosLoader(PhotoToLoad photoToLoad) {
            this.photoToLoad = photoToLoad;
        }

        @Override
        public void run() {
            try {
                if (imageViewReused(photoToLoad))
                    return;
                Bitmap bmp = getBitmap(photoToLoad.packageName);
                memoryCache.put(photoToLoad.packageName, bmp);
                if (imageViewReused(photoToLoad))
                    return;
                BitmapDisplayer bd = new BitmapDisplayer(bmp, photoToLoad);
                handler.post(bd);

            } catch (Throwable th) {
                th.printStackTrace();
            }
        }
    }

    private Bitmap getBitmap(String packageName) {
        File f = fileCache.getFile(packageName);
        Bitmap bitmap = decodeFile(f);
        if (bitmap != null)
            return bitmap;
        try {
//            FileOutputStream fOut = new FileOutputStream(f);
            bitmap = ((BitmapDrawable) mContext.getPackageManager().getApplicationIcon(packageName)).getBitmap();
//            bitmap.compress(Bitmap.CompressFormat.PNG, 85, fOut);
//            fOut.flush();
//            fOut.close();
//            bitmap = decodeFile(f);
            return bitmap;
        } catch (Throwable ex) {
            ex.printStackTrace();
            if (ex instanceof OutOfMemoryError)
                memoryCache.clear();
            return null;
        }
    }

    private Bitmap decodeFile(File f) {

        try {
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            FileInputStream stream1 = new FileInputStream(f);
            BitmapFactory.decodeStream(stream1, null, o);
            stream1.close();
            final int REQUIRED_SIZE = 85;

            int width_tmp = o.outWidth, height_tmp = o.outHeight;
            int scale = 1;
            while (true) {
                if (width_tmp / 2 < REQUIRED_SIZE || height_tmp / 2 < REQUIRED_SIZE)
                    break;
                width_tmp /= 2;
                height_tmp /= 2;
                scale *= 2;
            }
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            FileInputStream stream2 = new FileInputStream(f);
            Bitmap bitmap = BitmapFactory.decodeStream(stream2, null, o2);
            stream2.close();
            return bitmap;

        } catch (FileNotFoundException ignored) {
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    boolean imageViewReused(PhotoToLoad photoToLoad) {

        String tag = imageViews.get(photoToLoad.imageView);
        return tag == null || !tag.equals(photoToLoad.packageName);
    }

    class BitmapDisplayer implements Runnable {
        Bitmap bitmap;
        PhotoToLoad photoToLoad;

        public BitmapDisplayer(Bitmap b, PhotoToLoad p) {
            bitmap = b;
            photoToLoad = p;
        }

        public void run() {
            if (imageViewReused(photoToLoad))
                return;
            if (bitmap != null)
                photoToLoad.imageView.setImageBitmap(bitmap);
            else
                photoToLoad.imageView.setImageResource(stub_id);
        }
    }

    public void clearCache() {
        memoryCache.clear();
        fileCache.clear();
    }
}
