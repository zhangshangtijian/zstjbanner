package com.ihaozhuo.library.commen;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.WindowManager;

public class BannerPager extends ViewPager {
    int width = 0;
    int height = 0;

    public BannerPager(Context context) {
        super(context);
        WindowManager wm = (WindowManager) getContext().getSystemService(
                Context.WINDOW_SERVICE);
        width = wm.getDefaultDisplay().getWidth();
        double proportion = (((double) 9) / 16);
        height = (int) (width * proportion);
    }

    @SuppressWarnings("deprecation")
    public BannerPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        WindowManager wm = (WindowManager) getContext().getSystemService(
                Context.WINDOW_SERVICE);
        width = wm.getDefaultDisplay().getWidth();
        double proportion = (((double) 9) / 16);
        height = (int) (width * proportion);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(width, height);
    }
}
