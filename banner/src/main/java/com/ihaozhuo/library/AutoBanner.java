package com.ihaozhuo.library;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.WindowManager;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import android.widget.Scroller;

import com.ihaozhuo.library.FrescoUtils.FrescoPagerAdapter;
import com.ihaozhuo.library.FrescoUtils.ImageLoadUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * testCompile 'junit:junit:4.12'
 * compile 'com.android.support:appcompat-v7:23.4.0'
 * compile 'com.android.support:support-v4:23.4.0'
 * compile 'io.reactivex:rxandroid:1.1.0'
 * compile 'io.reactivex:rxjava:1.1.0'
 * compile 'com.facebook.fresco:fresco:0.13.0'
 * Created by haozhuo on 2016/11/22.
 */
public class AutoBanner extends FrameLayout {
    private int SpeedSCROLL = 800;// 滑动速度
    private static final int MP = FrameLayout.LayoutParams.MATCH_PARENT;
    private static final int WC = FrameLayout.LayoutParams.WRAP_CONTENT;
    int width = 0;
    int height = 0;

    private List<String> mImgStr;
    private int mPagerCount = 1;
    //自动播放时间
    private int mAutoPalyTime = 4000;
    private ViewPager mViewPager;
    private ViewPagerIndicator mIndicator;
    private FrescoPagerAdapter mPagerAdapter;

    private Subscription subscribeAuto;

    public AutoBanner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public AutoBanner(Context context, AttributeSet attrs) {
        super(context, attrs);
        WindowManager wm = (WindowManager) getContext().getSystemService(
                Context.WINDOW_SERVICE);
        width = wm.getDefaultDisplay().getWidth();
        double proportion = (((double) 9) / 16);
        height = (int) (width * proportion);
        initView(context);
    }

    /**
     * 设置自动轮播时间间隔
     *
     * @param mAutoPalyTime
     */
    public void setAutoPalyTime(int mAutoPalyTime) {
        this.mAutoPalyTime = mAutoPalyTime;
    }

    /**
     * 设置轮播图点击事件
     *
     * @param clickListener
     */
    public void setOnBannerClickListener(OnClickListener clickListener) {
        mPagerAdapter.setOnBannerClickListener(clickListener);
    }

    /**
     * 设置图片资源
     *
     * @param imgRes
     */
    public void setImgRes(ArrayList<String> imgRes) {
        if (imgRes == null || imgRes.size() == 0) return;
        mPagerCount = imgRes.size();
        mPagerAdapter.refresh(imgRes);
        mViewPager.setAdapter(mPagerAdapter);
        if (mPagerCount > 1) {
            mViewPager.setCurrentItem(mPagerCount * Constants.PAGERCOUNT / 2, false);
            startAutoPlay();
        }
        mIndicator.setNumber(mPagerCount);
    }

    /**
     * 添加 Indicator 高度默认24dp
     */
    public void addIndicator() {
        mIndicator = new ViewPagerIndicator(getContext());
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(MP, ImageLoadUtils.dp2px(getContext(), 24));
        //此处相当于布局文件中的Android:layout_gravity属性
        lp.gravity = Gravity.BOTTOM;
        mIndicator.setLayoutParams(lp);
        addView(mIndicator);
    }

    /**
     * 添加 Indicator 高度默认24dp
     *
     * @param indicator
     */
    public void bindIndicator(ViewPagerIndicator indicator) {
        mIndicator = indicator;
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(MP, ImageLoadUtils.dp2px(getContext(), 24));
        //此处相当于布局文件中的Android:layout_gravity属性
        lp.gravity = Gravity.BOTTOM;
        mIndicator.setLayoutParams(lp);
        addView(mIndicator);
    }

    /**
     * 添加 Indicator 自定义相对位置、大小
     *
     * @param indicator
     * @param params
     */
    public void bindIndicator(ViewPagerIndicator indicator, FrameLayout.LayoutParams params) {
        mIndicator = indicator;
        mIndicator.setLayoutParams(params);
        addView(mIndicator);
    }


    private void initView(Context context) {
        mViewPager = new ViewPager(context);
        addView(mViewPager, new FrameLayout.LayoutParams(MP, MP));
        mPagerAdapter = new FrescoPagerAdapter(context);
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (mIndicator != null) {
                    mIndicator.move(position % mPagerCount, positionOffset);
                }
            }

            @Override
            public void onPageSelected(int position) {
                startAutoPlay();
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                switch (state) {
                    case ViewPager.SCROLL_STATE_DRAGGING:
                        stopAutoPlay();
                        break;
                    case ViewPager.SCROLL_STATE_IDLE:
                        break;
                    case ViewPager.SCROLL_STATE_SETTLING:
                        break;
                }
            }
        });
        setViewPagerScroller(mViewPager);
    }

    public void startAutoPlay() {
        if (subscribeAuto == null || subscribeAuto.isUnsubscribed()) {
            subscribeAuto = Observable.interval(mAutoPalyTime, mAutoPalyTime, TimeUnit.MILLISECONDS)//延时 ，每间隔，时间单位
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<Long>() {
                        @Override
                        public void call(Long aLong) {
                            int currentIndex = mViewPager.getCurrentItem();
                            if (++currentIndex == mPagerAdapter.getCount()) {
                                mViewPager.setCurrentItem(0);
                            } else {
                                mViewPager.setCurrentItem(currentIndex, true);
                            }
                        }
                    });
        }
    }

    public void stopAutoPlay() {
        if (subscribeAuto != null && !subscribeAuto.isUnsubscribed()) {
            subscribeAuto.unsubscribe();
        }
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(width, height);
    }


    private void setViewPagerScroller(ViewPager pager) {
        try {
            //自定义滑动速度
            Field mScrollerField = ViewPager.class.getDeclaredField("mScroller");
            mScrollerField.setAccessible(true);
            mScrollerField.set(pager, new ViewPagerScroller(pager.getContext()));
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }


    /**
     * 自定义Scroller，用于调节ViewPager滑动速度
     */
    public class ViewPagerScroller extends Scroller {

        public ViewPagerScroller(Context context) {
            super(context);
        }

        public ViewPagerScroller(Context context, Interpolator interpolator) {
            super(context, interpolator);
        }

        public ViewPagerScroller(Context context, Interpolator interpolator, boolean flywheel) {
            super(context, interpolator, flywheel);
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy, int duration) {
            super.startScroll(startX, startY, dx, dy, SpeedSCROLL);
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy) {
            super.startScroll(startX, startY, dx, dy, SpeedSCROLL);
        }
    }
}
