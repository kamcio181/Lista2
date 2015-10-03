package com.domowe.apki.lista2;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by k.kaszubski on 10/2/15.
 */

class CustomTextView extends TextView {
    private Paint paint;
    private boolean strikeEnabled = false;

    public CustomTextView(Context context) {
        super(context);
        init();
    }

    public CustomTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public CustomTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setColor(this.getCurrentTextColor());
        paint.setStrokeWidth(getResources().getDisplayMetrics().density * 1);

    }

    public void setMeasure(){
        measure(0,0);
    }

    public void setStrikeEnabled(boolean enableStrike) {
        this.strikeEnabled = enableStrike;
    }

    public void setStrikeEnabled(boolean enableStrike, int strikeColor) {
        paint.setColor(strikeColor);
        this.strikeEnabled = enableStrike;
    }

    public void setStrikeColor(int strikeColor) {
        paint.setColor(strikeColor);
        this.invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        super.onDraw(canvas);
        if (strikeEnabled) {
            canvas.drawLine(getPaddingLeft(), getHeight() / 2, getMeasuredWidth() - getPaddingLeft(), getHeight() / 2, paint);
        }
    }
}
