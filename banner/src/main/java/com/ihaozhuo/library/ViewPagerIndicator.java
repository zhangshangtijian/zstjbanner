package com.ihaozhuo.library;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.ihaozhuo.library.FrescoUtils.ImageLoadUtils;


public class ViewPagerIndicator extends View {

    private float cx;
    private float cy;
    private float selectedRadius;
    private float unselectedRadius;
    private Paint selectedPaint;
    private Paint unselectedPaint;
    private float INDEX;
    private int number = 0;
    private float strokeWidth;

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
        invalidate();
    }

    public ViewPagerIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray array = context.obtainStyledAttributes(attrs,
                R.styleable.orgIndicator);
        int selectedColor = array.getColor(
                R.styleable.orgIndicator_selectedColor, Color.GRAY);
        int unselectedColor = array.getColor(
                R.styleable.orgIndicator_unselectedColor, Color.RED);

        float strokeWidth = array.getDimension(
                R.styleable.orgIndicator_strokeWidth, 0);
        selectedRadius = array.getDimension(
                R.styleable.orgIndicator_selectedRadius, 16);
        unselectedRadius = array.getDimension(
                R.styleable.orgIndicator_unselectedRadius, 12);
        array.recycle();
        selectedPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        selectedPaint.setColor(selectedColor);
        unselectedPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        unselectedPaint.setColor(unselectedColor);
        unselectedPaint.setStyle(Paint.Style.STROKE);
        unselectedPaint.setStrokeWidth(strokeWidth);
    }

    public ViewPagerIndicator(Context context) {
        super(context);
//        int selectedColor = Color.WHITE;
        int selectedColor = getContext().getResources().getColor(R.color.androidColorB);
        int unselectedColor = Color.WHITE;
        strokeWidth = ImageLoadUtils.dp2px(getContext(), (float) 1);
        selectedRadius = ImageLoadUtils.dp2px(getContext(), 4);
        unselectedRadius = ImageLoadUtils.dp2px(getContext(), 3);
        selectedPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        selectedPaint.setColor(selectedColor);
        unselectedPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        unselectedPaint.setColor(unselectedColor);
        unselectedPaint.setStyle(Paint.Style.STROKE);
        unselectedPaint.setStrokeWidth(strokeWidth);
    }

    public void setSelectedRadius(float radius) {
        selectedRadius = ImageLoadUtils.dp2px(getContext(), radius);
    }

    public void setUnselectedRadius(float radius) {
        unselectedRadius = ImageLoadUtils.dp2px(getContext(), radius);
    }

    public void setUnselectedStrokeWidth(float radius) {
        strokeWidth = ImageLoadUtils.dp2px(getContext(), radius);
        unselectedPaint.setStrokeWidth(strokeWidth);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        cx = (float) (getWidth() / 2 - (number - 1) * 1.5 * selectedRadius);
        cy = getHeight() / 2;
        for (int i = 0; i < number; i++) {
            canvas.drawCircle(cx + 3 * selectedRadius * i, cy, unselectedRadius, unselectedPaint);
//			canvas.drawCircle(cx + 3 * radius * i, cy, radius, paint2);
        }
        canvas.drawCircle(cx + INDEX, cy, selectedRadius, selectedPaint);
    }

    public void move(int position, float Offset) {
        if (number != 0) {
            position %= number;
            if (position == (number - 1) && Offset != 0) {
                return;
            }
            INDEX = (position + Offset) * 3 * selectedRadius;
            invalidate();
        }

    }

    public void move(int position) {
        if (number != 0) {
            position %= number;
            INDEX = (position) * 3 * selectedRadius;
            invalidate();
        }
    }

}
