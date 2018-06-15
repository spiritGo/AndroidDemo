package com.example.spirit.androiddemo.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.View;
import android.widget.Toast;

import com.example.spirit.androiddemo.MyApplication;
import com.example.spirit.androiddemo.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class Util {

    public static Context getMContext() {
        return MyApplication.getmContext();
    }

    public static View inflateView(int layoutId) {
        return View.inflate(getMContext(), layoutId, null);
    }

    public static void toast(String text) {
        Toast.makeText(getMContext(), text, Toast.LENGTH_SHORT).show();
    }

    public static Bitmap getBitMap(String path) {
        return BitmapFactory.decodeFile(path);
    }

    public static int dp2px(float dp) {
        float density = getMContext().getResources().getDisplayMetrics().density;
        return (int) (dp * density + 0.5);
    }

    public static Bitmap scaleBitmap(String path, int defSize) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        int outWidth = options.outWidth;
        int outHeight = options.outHeight;

        Bitmap bitmap = BitmapFactory.decodeFile(path);
        int destWidth = 0;
        int destHeight = 0;
        if (bitmap != null) {
            destWidth = bitmap.getWidth();
            destHeight = bitmap.getHeight();
        } else {
            destHeight = defSize;
            destWidth = defSize;
        }

        int inSampleSize = 2;
        if (outHeight > destHeight || outWidth > destHeight) {
            if (outWidth > outHeight) {
                inSampleSize = Math.round(outHeight / destHeight);
            } else {
                inSampleSize = Math.round(outWidth / destWidth);
            }
        }

        options = new BitmapFactory.Options();
        options.inSampleSize = inSampleSize;
        return BitmapFactory.decodeFile(path, options);
    }

    public static Bitmap compressBySize(int id, int targetWidth, int targetHeight) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(getMContext().getResources(), id, opts);

        int imgWidth = opts.outWidth;
        int imgHeight = opts.outHeight;
        // 分别计算图片宽度、高度与目标宽度、高度的比例；取大于等于该比例的最小整数；

        int widthRatio = (int) Math.ceil(imgWidth / (float) targetWidth);
        int heightRatio = (int) Math.ceil(imgHeight / (float) targetHeight);
        if (widthRatio > 1 || heightRatio > 1) {
            if (widthRatio > heightRatio) {
                opts.inSampleSize = widthRatio;
            } else {
                opts.inSampleSize = heightRatio;
            }
        }
        // 设置好缩放比例后，加载图片进内容；
        opts.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(getMContext().getResources(), id, opts);
    }

    public static StringBuilder getTextDuration(int duration) {
        int total = duration / 1000;
        int hour = total / 60 / 60;
        int min = total / 60 % 60;
        int s = total % 60;

        StringBuilder sb = new StringBuilder();
        if (hour != 0) {
            if (hour > 9) {
                sb.append(hour);
                sb.append(":");
            } else {
                sb.append("0").append(hour).append(":");
            }
        }

        if (min > 9) {
            sb.append(min).append(":");
        } else {
            sb.append("0").append(min).append(":");
        }

        if (s > 9) {
            sb.append(s);
        } else {
            sb.append("0").append(s);
        }

        return sb;
    }

    public static int getStatusBarHeight() {
        int resourceId = getMContext().getResources().getIdentifier("status_bar_height", "dimen",
                "android");
        int dimensionPixelSize = 0;
        if (resourceId > 0) {
            dimensionPixelSize = getMContext().getResources().getDimensionPixelSize(resourceId);
        }
        return dimensionPixelSize;
    }

    public static void createDialog(Context context, String msg) {
        new AlertDialog.Builder(context)
                .setTitle("提示：")
                .setIcon(R.mipmap.ic_launcher)
                .setMessage(msg)
                .setNegativeButton("确定", null)
                .show();
    }

    public static int sp2px(Context context, float spVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, spVal, context
                .getResources().getDisplayMetrics());
    }

    public static Drawable getScaleDrawble(int id, int width, int height) {
        return new BitmapDrawable(compressBySize(id, width, height));
    }

    private static byte[] readData(FileInputStream inStream) throws Exception {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        while ((len = inStream.read(buffer)) != -1) {
            outStream.write(buffer, 0, len);
        }
        outStream.close();
        inStream.close();
        return outStream.toByteArray();
    }

    public static String readFile(File file) {
        System.out.println(file.getName());
        try {
            FileInputStream fis = getMContext().openFileInput(file.getName());
            byte[] bytes = readData(fis);
            fis.close();
            return new String(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static void saveFile(String name, String content, int mode) {
        try {
            FileOutputStream fos = getMContext().openFileOutput(name, mode);
            fos.write(content.getBytes());
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}