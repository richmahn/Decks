package com.chinesepod.decks.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.HorizontalScrollView;

public class ArrowedHorizontalScrollView extends HorizontalScrollView {

	private View mRightArrow;

	public ArrowedHorizontalScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ArrowedHorizontalScrollView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public void setRightArrowView(View rightArrow) {
		mRightArrow = rightArrow;

	}

	@Override
	protected void onScrollChanged(int x, int t, int oldl, int oldt) {
		super.onScrollChanged(x, t, oldl, oldt);
		int maxX = getChildAt(0).getMeasuredWidth() - ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getWidth();
		// Log.i("Scrolling", "X from [" + oldl + "] to [" + x + "]. Max x: " + maxX);

		if (mRightArrow != null) {
			if (x >= maxX) {
				Animation fadeOutAnimation = AnimationUtils.loadAnimation(getContext(), android.R.anim.fade_out);
				// Now Set your animation
				if (mRightArrow.getVisibility() == View.VISIBLE) {
					mRightArrow.startAnimation(fadeOutAnimation);
					mRightArrow.setVisibility(View.GONE);
				}
			} else {
				Animation fadeInAnimation = AnimationUtils.loadAnimation(getContext(), android.R.anim.fade_in);
				if (mRightArrow.getVisibility() == View.GONE) {
					mRightArrow.startAnimation(fadeInAnimation);
					mRightArrow.setVisibility(View.VISIBLE);
				}
			}
		}
	}
}