package com.ihaozhuo.library;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Scroller;
import android.widget.Toast;

import com.ihaozhuo.library.commen.CustomImagerView;
import com.ihaozhuo.library.commen.ViewPagerIndicator;

import java.lang.reflect.Field;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by zhangzhongyao on 2016/11/22.
 */
public class AutoBanner2 extends FrameLayout {
    private int SpeedSCROLL = 800;// 滑动速度
    private static final int MP = RelativeLayout.LayoutParams.MATCH_PARENT;
    private static final int WC = RelativeLayout.LayoutParams.WRAP_CONTENT;
    int width = 0;
    int height = 0;
    private int[] mImgRes;
    //自动播放时间
    private int mAutoPalyTime = 4000;
    private ViewPager mViewPager;
    private ViewPagerIndicator mIndicator;
    private MyPagerAdapter mPagerAdapter;
    //    private List<CustomImagerView> mCacheViews = new ArrayList<>();
    private Subscription subscribeAuto;
    private boolean isDrag;

    public AutoBanner2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public AutoBanner2(Context context, AttributeSet attrs) {
        super(context, attrs);
        WindowManager wm = (WindowManager) getContext().getSystemService(
                Context.WINDOW_SERVICE);
        width = wm.getDefaultDisplay().getWidth();
        double proportion = (((double) 9) / 16);
        height = (int) (width * proportion);
        initView(context);
    }

    public AutoBanner2(Context context) {
        super(context);
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
     * 设置图片资源
     *
     * @param imgRes
     */
    public void setImgRes(int[] imgRes) {
        if (imgRes == null) return;
        mImgRes = imgRes;
        mPagerAdapter.notifyDataSetChanged();
        mViewPager.setAdapter(mPagerAdapter);
        if (imgRes.length > 1) {
            mViewPager.setCurrentItem(1, false);
            startAutoPlay();
        }
        mIndicator.setNumber(mImgRes.length);
    }

    public void bindIndicator(ViewPagerIndicator indicator) {
        mIndicator = indicator;
    }

    private void initView(Context context) {
        mViewPager = new ViewPager(context);
        addView(mViewPager, new LayoutParams(MP, MP));
        mPagerAdapter = new MyPagerAdapter(context);
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            private int getIndex(int position) {
                final int index;
                if (position == 0) {
                    index = mImgRes.length - 1;
                } else if (position == (mImgRes.length + 1)) {
                    index = 0;
                } else {
                    index = position - 1;
                }
                return index;
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (mIndicator != null) {
                    mIndicator.move(getIndex(position), positionOffset);
                }
            }


            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {
                switch (state) {
                    case ViewPager.SCROLL_STATE_DRAGGING:
                        isDrag = true;
                        break;
                    case ViewPager.SCROLL_STATE_IDLE:
                        isDrag = false;
                        int current = mViewPager.getCurrentItem();
                        int lastReal = mViewPager.getAdapter().getCount() - 2;
                        if (current == 0) {
                            mViewPager.setCurrentItem(lastReal, false);
                        } else if (current == lastReal + 1) {
                            mViewPager.setCurrentItem(1, false);
                        }
                        break;
                    case ViewPager.SCROLL_STATE_SETTLING:
                        isDrag = false;
                        break;
                }
            }
        });
        setViewPagerScroller(mViewPager);
    }

    public void startAutoPlay() {
        if (subscribeAuto == null || subscribeAuto.isUnsubscribed()) {
            subscribeAuto = Observable.interval(mAutoPalyTime, mAutoPalyTime, TimeUnit.MILLISECONDS)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<Long>() {
                        @Override
                        public void call(Long aLong) {
                            if (isDrag) return;
                            int currentIndex = mViewPager.getCurrentItem();
                            if (++currentIndex == mPagerAdapter.getCount()) {
                                mViewPager.setCurrentItem(1, false);
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


    private class MyPagerAdapter extends PagerAdapter {

        private Context mContext;

        public MyPagerAdapter(Context context) {
            mContext = context;
        }

        @Override
        public int getCount() {
            return mImgRes == null || mImgRes.length < 2 ? 1 : mImgRes.length + 2;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public View instantiateItem(ViewGroup container, int position) {
            final int index;
            if (position == 0) {
                index = mImgRes.length - 1;
            } else if (position == (mImgRes.length + 1)) {
                index = 0;
            } else {
                index = position - 1;
            }

            CustomImagerView iv = new CustomImagerView(mContext);
            iv.setLayoutParams(new ViewPager.LayoutParams());
            iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
            iv.setImageResource(mImgRes[index]);
            iv.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(mContext, "click :" + (index + 1), Toast.LENGTH_SHORT).show();
                }
            });
            container.addView(iv);
            return iv;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
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
