package com.example.myapplication;
import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import java.util.List;

public class BannerLayout extends FrameLayout {

    private Context mContext;
    private ViewPager mViewPager;
    private ViewPagerAdapter mViewPagerAdapter;
    private LinearLayout mLinearLayout;

    private List<View> mLists;

    //指示器相关
    private View[] mViews;  //指示器集合
    private final int DEFAULR_POINT_SIZE = 20;  //默认指示器大小
    private int mPointSize = DEFAULR_POINT_SIZE; //用户传入指示器大小
    private int lastIndex = 0;  //上一个页面的下标
    private final int DEFAULT_BOTTOM = 20;
    private int mBottomMargin = DEFAULT_BOTTOM;   //指示器距离底部距离；

    //自动播放相关
    private Handler mHandler;
    private Runnable mRunnable;
    private final int DEFAULT_DURATION = 1000;  //默认自动播放间隔
    private int duration = DEFAULT_DURATION;   //播放间隔
    private boolean isOpenAuto = true;  //是否打开自动播放
    private boolean isStopScroll = true;   //手指是否停止滑动
    private boolean isSmoothScroll = false;


    public BannerLayout(Context context) {
        super(context);

        mContext = context;
        initView();
    }

    public BannerLayout( Context context,  AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView();
    }

    public BannerLayout( Context context,  AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initView();
    }

    public BannerLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        mContext = context;
        initView();
    }

    //初始化组件
    private void initView(){
        FrameLayout.LayoutParams layoutParams = null;

        mViewPager = new ViewPager(mContext);
        addView(mViewPager);
        layoutParams = (LayoutParams) mViewPager.getLayoutParams();
        layoutParams.width = LayoutParams.MATCH_PARENT;
        layoutParams.height = LayoutParams.MATCH_PARENT;

        mLinearLayout = new LinearLayout(mContext);
        addView(mLinearLayout);
        layoutParams = (LayoutParams) mLinearLayout.getLayoutParams();
        layoutParams.width = LayoutParams.MATCH_PARENT;
        layoutParams.height = LayoutParams.WRAP_CONTENT;
        layoutParams.gravity = Gravity.BOTTOM;
        mLinearLayout.setGravity(Gravity.CENTER_HORIZONTAL);

    }

    public BannerLayout setPointerSize(int size){

        if(size > 0)
            mPointSize = size;

        return this;
    }

    public void setDatas(List<View> views){

        if(views != null && !views.isEmpty()){

            FrameLayout.LayoutParams layoutParams = (LayoutParams) mLinearLayout.getLayoutParams();
            layoutParams.bottomMargin = mBottomMargin;
            mLists = views;
            mViews = new View[views.size()-2];

            View view = null;
            LinearLayout.LayoutParams layoutParam = null;

            for(int i = 0; i < views.size()-2; i++){
                 view = new View(mContext);
                 mLinearLayout.addView(view);
                 layoutParam = (LinearLayout.LayoutParams) view.getLayoutParams();
                 layoutParam.height = mPointSize;
                 layoutParam.width = mPointSize;
                 layoutParam.rightMargin = mPointSize;

                 mViews[i] = view;
                 if(i == 0){
                     view.setBackground(mContext.getDrawable(R.drawable.point_light_bg));
                 }else {
                     view.setBackground(mContext.getDrawable(R.drawable.point_grey_bg));
                 }
            }


            mViewPagerAdapter = new ViewPagerAdapter(mLists);
            mViewPager.setAdapter(mViewPagerAdapter);
            mViewPager.setCurrentItem(1, isSmoothScroll);
            initViewPagerPageChangeListener();

            if(isOpenAuto){
                mHandler = new Handler();
                mRunnable = new Runnable();
                mHandler.postDelayed(mRunnable, duration);
            }
        }

    }

    private void initViewPagerPageChangeListener(){

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {


            @Override
            public void onPageScrolled(int i, float v, int i1) {

                if(isOpenAuto && isStopScroll && v == 0) {

                    if (i == mLists.size() - 1) {
                        cancle();
                        lastIndex = mLists.size() - 2;
                        mViewPager.setCurrentItem(1, isSmoothScroll);
                        return;
                    }

                    if (i == 0) {
                        cancle();
                        lastIndex = 1;
                        mViewPager.setCurrentItem(mLists.size() - 2, isSmoothScroll);
                        return;
                    }
                }
            }

            @Override
            public void onPageSelected(int i) {

                if(i == 0){
                    mViews[0].setBackground(mContext.getDrawable(R.drawable.point_grey_bg));
                    mViews[mViews.length-1].setBackground(mContext.getDrawable(R.drawable.point_light_bg));
                }
                if(i == mLists.size()-1){
                    mViews[0].setBackground(mContext.getDrawable(R.drawable.point_light_bg));
                    mViews[mViews.length-1].setBackground(mContext.getDrawable(R.drawable.point_grey_bg));
                }
                else if(i > 0 && i < mLists.size()-1) {
                    mViews[i-1].setBackground(mContext.getDrawable(R.drawable.point_light_bg));
                    mViews[lastIndex-1].setBackground(mContext.getDrawable(R.drawable.point_grey_bg));
                }

                if(isOpenAuto && isStopScroll) {
                    mHandler.postDelayed(mRunnable, duration);
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

                switch (i){


                    case ViewPager.SCROLL_STATE_DRAGGING:
                        cancle();
                        lastIndex = mViewPager.getCurrentItem();
                        isStopScroll = false;
                        break;

                        case ViewPager.SCROLL_STATE_SETTLING:

                            isStopScroll = true;
                            break;

                    case ViewPager.SCROLL_STATE_IDLE:

                        isStopScroll = true;
                        if(mViewPager.getCurrentItem() == mLists.size()-1){
                            lastIndex = mLists.size()-2;
                            mViewPager.setCurrentItem(1, isSmoothScroll);
                            return;
                        }

                        if(mViewPager.getCurrentItem() == 0){
                            lastIndex = 1;
                            mViewPager.setCurrentItem(mLists.size()-2,isSmoothScroll);
                            return;
                        }

                        break;
                }
            }
        });
    }

    private void cancle(){
        if(isOpenAuto){
            mHandler.removeCallbacks(mRunnable);
        }
    }

    private class Runnable implements java.lang.Runnable{

        @Override
        public void run() {

            lastIndex = mViewPager.getCurrentItem();
            mViewPager.setCurrentItem((lastIndex + 1) % mLists.size(), false);
        }
    }

    private class changeRunnable implements java.lang.Runnable{

        private int i;
        public changeRunnable(int i ){
            this.i = i;
        }

        @Override
        public void run() {
            mViewPager.setCurrentItem(i, false);
        }
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////

    private class ViewPagerAdapter extends PagerAdapter{

        private List<View> mLists;

        public ViewPagerAdapter(List<View> lists){
            mLists = lists;
        }


        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {

            View view = mLists.get(position);
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {

            container.removeView(mLists.get(position));
        }



        @Override
        public int getCount() {

            return mLists.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
            return view == o;
        }
    }

}
