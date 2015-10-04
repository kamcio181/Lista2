package com.domowe.apki.lista2;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

/**
 * Created by k.kaszubski on 10/2/15.
 */

class CustomTextView extends TextView {
    private Paint paint;
    private boolean strikeEnabled = false;
    private static final int OFFSET = 3;

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
            switch (getContext().getSharedPreferences(Constants.PREFERENCES_NAME, Context.MODE_PRIVATE).getInt(Constants.TILE_SIZE_KEY,Constants.TILE_MEDIUM)){
                case Constants.TILE_SMALL:
                    canvas.drawLine(getPaddingLeft() - OFFSET, getHeight() / 2, getPaint().measureText(getText().toString()) + getPaddingLeft() + OFFSET, getHeight() / 2, paint);
                    break;
                case Constants.TILE_MEDIUM:
                    switch (getLineCount()){
                        case 1:
                            canvas.drawLine(getPaddingLeft() - OFFSET, getHeight() / 2, getPaint().measureText(getText().toString()) + getPaddingLeft() + OFFSET, getHeight() / 2, paint);
                            break;
                        case 2:
                        default:
                            for(int i = 1; i <= 2; i++)
                                canvas.drawLine(getPaddingLeft() - OFFSET, i*getLineHeight(), getPaint().measureText(getText().toString()) + getPaddingLeft() + OFFSET, i*getLineHeight(), paint);
                            break;
                            }
                    break;
                case Constants.TILE_BIG:
                    switch (getLineCount()){
                        case 1:
                            canvas.drawLine(getPaddingLeft() - OFFSET, getHeight() / 2, getPaint().measureText(getText().toString()) + getPaddingLeft() + OFFSET, getHeight() / 2, paint);
                            break;
                        case 2:
                            for(int i = 1; i <= 2; i++)
                                canvas.drawLine(getPaddingLeft() - OFFSET, i*getLineHeight() + getLineHeight()/2, getPaint().measureText(getText().toString()) + getPaddingLeft() + OFFSET, i*getLineHeight() + getLineHeight()/2, paint);
                            break;
                        case 3:
                        default:
                            for(int i = 1; i <= 3; i++)
                                canvas.drawLine(getPaddingLeft() - OFFSET, i*getLineHeight(), getPaint().measureText(getText().toString()) + getPaddingLeft() + OFFSET, i*getLineHeight(), paint);
                            break;
                    }
                    break;
            }
        }
    }
}