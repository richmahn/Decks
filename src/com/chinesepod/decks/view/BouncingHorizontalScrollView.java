package com.chinesepod.decks.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.widget.HorizontalScrollView;

public class BouncingHorizontalScrollView extends HorizontalScrollView
{
    private static final int MAX_X_OVERSCROLL_DISTANCE = 200;

    private Context mContext;
    private int mMaxXOverscrollDistance;

    public BouncingHorizontalScrollView(Context context) 
    {
        super(context);
        mContext = context;
        initBouncingHorizontalScrollView();
    }

    public BouncingHorizontalScrollView(Context context, AttributeSet attrs) 
    {
        super(context, attrs);
        mContext = context;
        initBouncingHorizontalScrollView();
    }

    public BouncingHorizontalScrollView(Context context, AttributeSet attrs, int defStyle) 
    {
        super(context, attrs, defStyle);
        mContext = context;
        initBouncingHorizontalScrollView();
    }

    private void initBouncingHorizontalScrollView()
    {
        //get the density of the screen and do some maths with it on the max overscroll distance
        //variable so that you get similar behaviors no matter what the screen size

        final DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
            final float density = metrics.density;

        mMaxXOverscrollDistance = (int) (density * MAX_X_OVERSCROLL_DISTANCE);
    }

    @Override
    protected boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, int scrollRangeX, int scrollRangeY, int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) 
    { 
        //This is where the magic happens, we have replaced the incoming maxOverScrollY with our own custom variable mMaxYOverscrollDistance; 
        return super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX, scrollRangeY, mMaxXOverscrollDistance, maxOverScrollY, isTouchEvent);  
    }

}
