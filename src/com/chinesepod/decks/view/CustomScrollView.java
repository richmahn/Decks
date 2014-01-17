package com.chinesepod.decks.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

public class CustomScrollView extends ScrollView {

	public CustomScrollView(Context p_context, AttributeSet p_attrs) {
		super(p_context, p_attrs);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent p_event) {
		onTouchEvent(p_event);
		return false;
	}

	@Override
	public boolean onTouchEvent(MotionEvent p_event) {

		if (p_event.getAction() == MotionEvent.ACTION_MOVE
				&& getParent() != null) {
			getParent().requestDisallowInterceptTouchEvent(true);
		}

		return super.onTouchEvent(p_event);
	}
}