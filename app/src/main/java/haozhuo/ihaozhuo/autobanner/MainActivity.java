package haozhuo.ihaozhuo.autobanner;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.ihaozhuo.library.AutoBanner;
import com.ihaozhuo.library.FrescoUtils.ImageLoadUtils;
import com.ihaozhuo.library.ViewPagerIndicator;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ArrayList<String> DATAS = new ArrayList();
    private AutoBanner banner;
    ViewPagerIndicator mIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //fresco
        Fresco.initialize(this, ImageLoadUtils.getInstance()
                .CustomConfig(this));
        banner = (AutoBanner) findViewById(R.id.banner);
        initIndicator();
        banner.bindIndicator(mIndicator);
//        banner.addIndicator();
//        banner.setOnBannerClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(MainActivity.this, "艾欧尼亚", Toast.LENGTH_SHORT).show();
//            }
//        });
        DATAS.add("res:///" + R.drawable.banner1);
        DATAS.add("res:///" + R.drawable.banner2);
        DATAS.add("res:///" + R.drawable.banner3);
        DATAS.add("http://pic56.nipic.com/file/20141227/19674963_215052431000_2.jpg");
        banner.setImgRes(DATAS);
    }

    private void initIndicator() {
        mIndicator = new ViewPagerIndicator(this);
        mIndicator.setBackgroundColor(Color.parseColor("#66000000"));
    }

    public void start(View v) {
        banner.startAutoPlay();
    }

    public void stop(View v) {
        banner.stopAutoPlay();
    }

    public void unselectR(View v) {
        mIndicator.setUnselectedRadius(4);
    }

    public void selectR(View v) {
        mIndicator.setSelectedRadius(8);
    }

    public void strokeWidth(View v) {
        mIndicator.setUnselectedStrokeWidth(3);
    }

}
