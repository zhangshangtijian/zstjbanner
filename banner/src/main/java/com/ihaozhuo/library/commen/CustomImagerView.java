package com.ihaozhuo.library.commen;

import android.content.Context;
import android.util.AttributeSet;
import android.view.WindowManager;
import android.widget.ImageView;

/**
 * Created by zhangzhongyao on 2016/11/22.
 */
public class CustomImagerView extends ImageView {
    int width = 0;
    int height = 0;

    public CustomImagerView(Context context) {
        super(context);
        WindowManager wm = (WindowManager) getContext().getSystemService(
                Context.WINDOW_SERVICE);
        width = wm.getDefaultDisplay().getWidth();
        double proportion = (((double) 9) / 16);
        height = (int) (width * proportion);
    }

    public CustomImagerView(Context context, AttributeSet attrs) {
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
