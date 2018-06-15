package com.example.spirit.androiddemo.modle;

import android.graphics.Bitmap;

public class PicBean {
    private String path;
    private Bitmap bitmap;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    @Override
    public String toString() {
        return "PicBean{" +
                "path='" + path + '\'' +
                ", bitmap=" + bitmap +
                '}';
    }
}
