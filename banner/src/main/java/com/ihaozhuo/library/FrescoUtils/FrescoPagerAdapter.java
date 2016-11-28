package com.ihaozhuo.library.FrescoUtils;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.ihaozhuo.library.Constants;
import com.ihaozhuo.library.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangzhongyao on 2016/11/24.
 */
public class FrescoPagerAdapter extends PagerAdapter {
    private List<Width16Heigth9DraweeView> mCacheViews = new ArrayList<>();
    private List<String> mImgRes;
    private Context mContext;
    private View.OnClickListener mOnClickListener;

    public FrescoPagerAdapter(Context context) {
        mImgRes = new ArrayList();
        mImgRes.add("res:///" + R.drawable.banner);
        mContext = context;
    }

    public void refresh(ArrayList<String> imgRes) {
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
        Width16Heigth9DraweeView iv;
        if (mCacheViews.size() > 0) {
            iv = mCacheViews.remove(0);
        } else {
            iv = new Width16Heigth9DraweeView(mContext);
            iv.setLayoutParams(new ViewPager.LayoutParams());
            iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }
        ImageLoadUtils.getInstance().display(mImgRes.get(index), iv);
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
        container.addView(iv);
        return iv;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
        mCacheViews.add((Width16Heigth9DraweeView) object);
    }
}
