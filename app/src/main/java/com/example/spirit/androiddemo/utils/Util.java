package com.example.spirit.androiddemo.utils;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Binder;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.spirit.androiddemo.MyApplication;
import com.example.spirit.androiddemo.R;
import com.example.spirit.androiddemo.modle.FileBean;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

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

    public static Point getWindowSize() {
        WindowManager wm = (WindowManager) getMContext().getSystemService(Context.WINDOW_SERVICE);
        if (wm != null) {
            int width = wm.getDefaultDisplay().getWidth();
            int height = wm.getDefaultDisplay().getHeight();
            Point point = new Point();
            point.x = width;
            point.y = height;
            return point;
        }
        return null;
    }

    public static String getVersionName() {
        PackageManager packageManager = getMContext().getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(getMContext().getPackageName
                    (), 0);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean lacksPermission(String permission) {
        return ContextCompat.checkSelfPermission(getMContext(), permission) == PackageManager
                .PERMISSION_DENIED;
    }

    public static boolean checkAlertWindowsPermission(Context context) {
        try {
            Object object = context.getSystemService(Context.APP_OPS_SERVICE);
            if (object == null) {
                return false;
            }
            Class localClass = object.getClass();
            Class[] arrayOfClass = new Class[3];
            arrayOfClass[0] = Integer.TYPE;
            arrayOfClass[1] = Integer.TYPE;
            arrayOfClass[2] = String.class;
            Method method = localClass.getMethod("checkOp", arrayOfClass);
            if (method == null) {
                return false;
            }
            Object[] arrayOfObject1 = new Object[3];
            arrayOfObject1[0] = 24;
            arrayOfObject1[1] = Binder.getCallingUid();
            arrayOfObject1[2] = context.getPackageName();
            int m = ((Integer) method.invoke(object, arrayOfObject1));
            return m == AppOpsManager.MODE_ALLOWED;
        } catch (Exception ex) {
            System.out.println(ex);
        }
        return false;
    }

    public static boolean isServiceRunning(String name) {
        ActivityManager activityManager = (ActivityManager) getMContext().getSystemService
                (Context.ACTIVITY_SERVICE);
        if (activityManager != null) {
            List<ActivityManager.RunningServiceInfo> runningTasks = activityManager
                    .getRunningServices(100);
            if (runningTasks != null) {
                for (ActivityManager.RunningServiceInfo info : runningTasks) {
                    if (info.service.getClassName().equals(name)) {
                        return true;
                    }
                }
            }
        }
        return false;

    }

    public static ArrayList<FileBean> fileScan(File file) {
        File[] files = file.listFiles();
        if (files == null) return null;
        ArrayList<FileBean> fileBeans = new ArrayList<>();
        ArrayList<FileBean> subFile = new ArrayList<>();
        for (File fileItem : files) {
            FileBean fileBean = new FileBean();
            fileBean.setFile(fileItem);
            if (fileItem.isDirectory()) {
                fileBean.setType(ConstanceField.DIRECTORY);
                fileBeans.add(fileBean);

            } else {
                fileBean.setType(ConstanceField.NOT_DIRECTORY);
                subFile.add(fileBean);

            }
        }
        fileBeans.addAll(subFile);
        return fileBeans;
    }

    //打开文件
    public static void openFile(File file) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(android.content.Intent.ACTION_VIEW);
        String type = getMIMEType(file);
        intent.setDataAndType(Uri.fromFile(file), type);
        getMContext().startActivity(intent);
    }

    //获取文件mimetype
    private static String getMIMEType(File file) {
        String type = "";
        String name = file.getName();
        //文件扩展名
        String end = name.substring(name.lastIndexOf(".") + 1, name.length()).toLowerCase();
        if (end.equals("m4a") || end.equals("mp3") || end.equals("wav")) {
            type = "audio";
        } else if (end.equals("mp4") || end.equals("3gp")) {
            type = "video";
        } else if (end.equals("jpg") || end.equals("png") || end.equals("jpeg") || end.equals("bmp") || end.equals("gif")) {
            type = "image";
        } else {
            //如果无法直接打开，跳出列表由用户选择
            type = "*";
        }
        type += "/*";
        return type;
    }


}
