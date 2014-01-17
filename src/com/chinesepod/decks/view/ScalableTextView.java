package com.chinesepod.decks.view;

import android.content.Context;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.SimpleOnScaleGestureListener;
import android.widget.TextView;

public class ScalableTextView extends TextView {
    ScaleGestureDetector scaleGestureDetector;

    public ScalableTextView(Context context) {
    	super(context);
    	setFocusable(true);
    	scaleGestureDetector = new ScaleGestureDetector(context, new mySimpleOnScaleGestureListener());
    }

  @Override
  public boolean onTouchEvent(MotionEvent event) {
      // TODO Auto-generated method stub
      scaleGestureDetector.onTouchEvent(event);
      return true;
  }

  public class mySimpleOnScaleGestureListener extends SimpleOnScaleGestureListener {

      @Override
      public boolean onScale(ScaleGestureDetector detector) {
          // TODO Auto-generated method stub
          float size = ScalableTextView.this.getTextSize();
          Log.d("TextSizeStart", String.valueOf(size));

          float factor = detector.getScaleFactor();
          Log.d("Factor", String.valueOf(factor));


          float product = size*factor;
          Log.d("TextSize", String.valueOf(product));
          ScalableTextView.this.setTextSize(TypedValue.COMPLEX_UNIT_PX, product);

          size = ScalableTextView.this.getTextSize();
          Log.d("TextSizeEnd", String.valueOf(size));
          return true;
      }
  }
} 
