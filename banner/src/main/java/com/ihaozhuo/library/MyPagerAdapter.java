package com.ihaozhuo.library;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.ihaozhuo.library.commen.Constants;
import com.ihaozhuo.library.commen.CustomImagerView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangzhongyao on 2016/11/24.
 */
public class MyPagerAdapter extends PagerAdapter {
    private List<CustomImagerView> mCacheViews = new ArrayList<>();
    private List<Integer> mImgRes;
    private Context mContext;
    private View.OnClickListener mOnClickListener;

    public MyPagerAdapter(Context context) {
        mImgRes = new ArrayList();
        mImgRes.add(R.drawable.zbanner);
        mContext = context;
    }

    public void refresh(ArrayList<Integer> imgRes) {
        mImgRes.clear();
        mImgRes.addAll(imgRes);
        notifyDataSetChanged();
    }

    public void setOnBannerClickListener(View.OnClickListener onClickListener) {
        mOnClickListener = onClickListener;
    }

    @Override
    public int getCount() {
        return mImgRes.size() < 2 ? 1 : Constants.PAGERCOUNT * mImgRes.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public View instantiateItem(ViewGroup container, int position) {
        final int index = position % mImgRes.size();
        CustomImagerView iv;
        if (mCacheViews.size() > 0) {
            iv = mCacheViews.remove(0);
        } else {
            iv = new CustomImagerView(mContext);
            iv.setLayoutParams(new ViewPager.LayoutParams());
            iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }
        iv.setImageResource(mImgRes.get(index));
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnClickListener == null) {
                    Toast.makeText(mContext, "click :" + (index + 1), Toast.LENGTH_SHORT).show();
                } else {
                    mOnClickListener.onClick(v);
                }
            }
        });
        Log.e("CustomImagerView", iv.toString());
        container.addView(iv);
        return iv;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
        mCacheViews.add((CustomImagerView) object);
    }
}
