package com.waldm.proverbica.app;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import com.waldm.proverbica.SayingDisplayer;
import com.waldm.proverbica.infrastructure.ImageSize;
import com.waldm.proverbica.infrastructure.SayingSource;
import com.waldm.proverbica.retriever.SayingRetriever;

public class CustomViewPager extends ViewPager {
    private static final String TAG = CustomViewPager.class.getSimpleName();
    private SayingRetriever sayingRetriever;
    private int maxPosition = -1;
    private SayingDisplayer sayingDisplayer;

    public CustomViewPager(Context context) {
        super(context);
    }

    public CustomViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onPageScrolled(int position, float offset, int offsetPixels) {
        super.onPageScrolled(position, offset, offsetPixels);

        if (position > maxPosition) {
            Log.d(TAG, "Loading new saying");
            sayingRetriever.loadSaying(SayingSource.EITHER, ImageSize.NORMAL);
            maxPosition = position;
        }

        ((ViewPageAdapter) getAdapter()).setPosition(position);
        sayingDisplayer.updateFavouritesButton();
    }

    @Override
    public void setOffscreenPageLimit(int limit) {
        // TODO Auto-generated method stub
        super.setOffscreenPageLimit(limit);
    }

    @Override
    public void setOnPageChangeListener(OnPageChangeListener listener) {
        // TODO Auto-generated method stub
        super.setOnPageChangeListener(listener);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        // Log.e(TAG, "onInterceptTouchEvent");
        // sayingRetriever.loadSaying(SayingSource.EITHER, ImageSize.NORMAL);
        return super.onInterceptTouchEvent(motionEvent);
    }

    public void setFields(SayingRetriever sayingRetriever, SayingDisplayer sayingDisplayer) {
        this.sayingRetriever = sayingRetriever;
        this.sayingDisplayer = sayingDisplayer;
    }

}
