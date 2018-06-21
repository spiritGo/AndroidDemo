package com.example.spirit.androiddemo.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.spirit.androiddemo.ImageShowActivity;
import com.example.spirit.androiddemo.MyApplication;
import com.example.spirit.androiddemo.R;
import com.example.spirit.androiddemo.utils.ConstanceField;
import com.example.spirit.androiddemo.utils.Util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;

public class CaptureFragment extends Fragment {
    private ImageView btnTakePicture;
    private SurfaceView svView;
    private Camera camera;
    private ImageView ivImage;
    private BufferedOutputStream bos;
    private File file = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = Util.inflateView(R.layout.fragment_cature);
        MyApplication.setName(getClass().getSimpleName());
        initView(view);
        initUI();
        return view;
    }

    private void initView(View view) {
        svView = view.findViewById(R.id.sv_view);
        btnTakePicture = view.findViewById(R.id.btn_takePicture);
        ivImage = view.findViewById(R.id.iv_image);
    }

    private void initUI() {
        SurfaceHolder holder = svView.getHolder();
        holder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                camera = getCamera();
                if (camera != null) {
                    try {
                        camera.setPreviewDisplay(holder);
                        camera.setDisplayOrientation(90);
                        camera.startPreview();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                autoFocus();
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                releaseCamera();
            }
        });

        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        camera = getCamera();
        if (camera != null) {
            try {
                camera.setPreviewDisplay(holder);
                camera.setDisplayOrientation(90);
                camera.startPreview();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        btnTakePicture.setColorFilter(0x55000000);
        btnTakePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePicture();
            }
        });

        ivImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                if (getActivity() != null) {
                    intent.setClass(getActivity(), ImageShowActivity.class);
                    intent.putExtra(ConstanceField.TOP_TITLE, getString(R.string.preview));
                    intent.putExtra(ConstanceField.PATH, file.getAbsolutePath());
                    startActivity(intent);
                }
            }
        });


        svView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                autoFocus();
            }
        });
    }

    private void scanner() {
        Intent intent = null;
        Uri uri = null;
        try {
            intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            uri = Uri.fromFile(file);
            intent.setData(uri);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (getActivity() != null) {
            getActivity().sendBroadcast(intent);
        }
        System.out.println(file.getName() + "uriuri:::" + uri);
    }

    private void autoFocus() {
        camera.autoFocus(new Camera.AutoFocusCallback() {
            @Override
            public void onAutoFocus(boolean success, Camera camera) {
                System.out.println(success);
                if (success) {
                    initCamera();
                    camera.cancelAutoFocus();
                }
            }
        });
    }

    private void initCamera() {
        Camera.Parameters parameters = camera.getParameters();
        parameters.setPictureFormat(PixelFormat.JPEG);
        //parameters.setPictureSize(surfaceView.getWidth(), surfaceView.getHeight());  //
        // 部分定制手机，无法正常识别该方法。
        //parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);//1连续对焦
        setDisplay(parameters, camera);
        camera.setParameters(parameters);
        camera.startPreview();
        camera.cancelAutoFocus();// 2如果要实现连续的自动对焦，这一句必须加上
    }

    private void setDisplay(Camera.Parameters parameters, Camera camera) {
        if (Integer.parseInt(Build.VERSION.SDK) >= 8) {
            setDisplayOrientation(camera, 90);
        } else {
            parameters.setRotation(90);
        }
    }

    private void setDisplayOrientation(Camera camera, int i) {
        Method downPolymorphic;
        try {
            downPolymorphic = camera.getClass().getMethod("setDisplayOrientation", int.class);
            if (downPolymorphic != null) {
                downPolymorphic.invoke(camera, i);
            }
        } catch (Exception e) {
            Log.e("Came_e", "图像出错");
        }
    }

    private Camera getCamera() {
        if (camera == null) {
            try {
                camera = Camera.open();
            } catch (Exception e) {
                return null;
            }
        }
        return camera;
    }

    private void releaseCamera() {
        if (camera != null) {
            camera.release();
            camera = null;
        }
    }

    private void takePicture() {
        camera.takePicture(null, null, new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                Bitmap bitmap0 = BitmapFactory.decodeByteArray(data, 0, data.length);
                Matrix matrix = new Matrix();
                matrix.setRotate(90);
                Bitmap bitmap = Bitmap.createBitmap(bitmap0, 0, 0, bitmap0.getWidth(),
                        bitmap0.getHeight(), matrix, true);
                ivImage.setImageBitmap(bitmap);
                String fileName = "Hey" + System.currentTimeMillis() + ".jpg";
                try {
                    if (getContext() != null) {
                        file = new File(Environment.getExternalStorageDirectory(), fileName);
                    }
                    bos = new BufferedOutputStream(new FileOutputStream(file));
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        bos.flush();
                        bos.close();
                        scanner();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}
