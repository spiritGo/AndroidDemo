package com.example.spirit.androiddemo.views;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class ScaleDragImageView extends android.support.v7.widget.AppCompatImageView implements
        View.OnTouchListener {

    private float startDis;
    private float endDis;
    private PointF startPoint = new PointF();
    final private int MODE_DRAG = 0;
    final private int MODE_SCALE = 1;
    private int currentMode = MODE_DRAG;
    private Matrix currentMatrix = new Matrix();
    private float mWidth;
    final private int MIN_WIDTH = 10;
    private float[] value = new float[9];

    public ScaleDragImageView(Context context) {
        this(context, null);
    }

    public ScaleDragImageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScaleDragImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOnTouchListener(this);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        currentMatrix.getValues(value);
        //图片宽度为屏幕宽度除缩放倍数
        mWidth = getWidth() / value[Matrix.MSCALE_X];
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                currentMode = MODE_DRAG;
                startPoint.set(event.getX(), event.getY());
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                currentMode = MODE_SCALE;
                startDis = distance(event);
                break;
            case MotionEvent.ACTION_MOVE:
                if (currentMode == MODE_DRAG) {
                    currentMatrix.set(getImageMatrix());
                    float dx = event.getX() - startPoint.x;
                    float dy = event.getY() - startPoint.y;
                    currentMatrix.postTranslate(dx, dy);
                    setImageMatrix(currentMatrix);
                    startPoint.set(event.getX(), event.getY());
                    //System.out.println(dx + ", " + dy);
                } else if (currentMode == MODE_SCALE) {
                    if (event.getPointerCount() >= 2) {
                        endDis = distance(event);
                        if (endDis > 10f) {
                            currentMatrix.set(getImageMatrix());
                            float scale = endDis / startDis;

                            if (mWidth >= MIN_WIDTH) {
                                mWidth *= scale;
                                startDis = endDis;
                                currentMatrix.postScale(scale, scale, getWidth() / 2, getHeight()
                                        / 2);
                                setImageMatrix(currentMatrix);
                            }

                            if (scale >= 1) {
                                mWidth *= scale;
                                startDis = endDis;
                                currentMatrix.postScale(scale, scale, getWidth() / 2, getHeight()
                                        / 2);
                                setImageMatrix(currentMatrix);
                            }
                        }
                    }
                }
                break;

        }
        return true;
    }

    private float distance(MotionEvent event) {
        float dx = event.getX(1) - event.getX(0);
        float dy = event.getY(1) - event.getY(0);
        return (float) Math.sqrt(dx * dx + dy * dy);
    }
}
