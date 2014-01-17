package com.chinesepod.decks.view;

import android.content.Context;
import android.graphics.Canvas;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.widget.Button;

public class VerticalButton extends Button {
	public VerticalButton(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(heightMeasureSpec, widthMeasureSpec);
		setMeasuredDimension(getMeasuredHeight(), getMeasuredWidth());
	}

	@Override
	protected void onDraw(Canvas canvas) {
		TextPaint textPaint = getPaint();
		textPaint.setColor(getCurrentTextColor());
		textPaint.drawableState = getDrawableState();

		canvas.save();

		canvas.translate(0, getHeight());
		canvas.rotate(-90);

		canvas.translate(getCompoundPaddingLeft(), getExtendedPaddingTop());

		getLayout().draw(canvas);
		canvas.restore();
	}
}