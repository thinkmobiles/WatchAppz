package com.watchappz.android.utils.image_loader;

import android.content.Context;

import java.io.File;

/**
 * Created by
 * mRogach on 12.10.2015.
 */

public final class FileCache {

    private File cacheDir;

    public FileCache(Context context) {

        if (android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED)) {
            cacheDir = new File(android.os.Environment.getExternalStorageDirectory(), "WatchAppz");
        } else {
            cacheDir = context.getCacheDir();
        }

        if (!cacheDir.exists()) {
            cacheDir.mkdirs();
        }
    }

    public File getFile(String url) {
        String filename = String.valueOf(url.hashCode());
        File f = new File(cacheDir, filename);
        return f;

    }

    public void clear() {
        File[] files = cacheDir.listFiles();
        if (files == null)
            return;
        for (File f : files)
            f.delete();
    }
}
