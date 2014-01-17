package com.chinesepod.decks.view;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

public class TouchImageView extends ImageView {

    private Matrix mMatrix = new Matrix();

    private Matrix mSavedMatrix = new Matrix();

    private static final int NONE = 0; 
    private static final int DRAG = 1; 
    private static final int ZOOM = 2; 

    private int mMode = NONE;

    private PointF mStartPoint = new PointF();

    
    private float mOldDistance;

    
    private PointF mMidPoint = new PointF();

    public TouchImageView(Context context) {
        super(context);
        init(context);
    }

    public TouchImageView(Context context, AttributeSet attrs,
            int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public TouchImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    
    private void init(Context context) {
        
        setClickable(true);

        
        setScaleType(ScaleType.MATRIX);

        
        setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction() & MotionEvent.ACTION_MASK) {

                
                case MotionEvent.ACTION_DOWN:
                    mMode = DRAG;
                    mStartPoint.set(event.getX(), event.getY());
                    mSavedMatrix.set(mMatrix);
                    break;

                
                
                case MotionEvent.ACTION_MOVE:
                    if(mMode == DRAG) {
                        mMatrix.set(mSavedMatrix);
                        float x = event.getX() - mStartPoint.x;
                        float y = event.getY() - mStartPoint.y;
                        mMatrix.postTranslate(x, y);
                    } else if (mMode == ZOOM) {
                        float newDist = culcDistance(event);
                        float scale = newDist / mOldDistance;
                        mMatrix.set(mSavedMatrix);
                        mMatrix.postScale(scale, scale,
                                          mMidPoint.x, mMidPoint.y);
                    }
                    break;

                
                case MotionEvent.ACTION_UP:
                    mMode = NONE;
                    break;

                
                case MotionEvent.ACTION_POINTER_DOWN:
                    mMode = ZOOM;
                    mOldDistance = culcDistance(event);
                    culcMidPoint(mMidPoint, event);
                    break;

                
                case MotionEvent.ACTION_POINTER_UP:
                    mMode = NONE;
                    break;
                default:
                    break;
                }

                
                
                
                setImageMatrix(mMatrix);
                return true;
            }
        });
    }

    
    private float culcDistance(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return FloatMath.sqrt(x * x + y * y);
    }

    
    private void culcMidPoint(PointF midPoint, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        midPoint.set(x / 2, y / 2);
    }
}
