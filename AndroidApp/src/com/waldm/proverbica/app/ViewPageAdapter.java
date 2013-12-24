package com.waldm.proverbica.app;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.common.collect.Lists;
import com.waldm.proverbica.R;
import com.waldm.proverbica.Saying;

public class ViewPageAdapter extends PagerAdapter {
    private final Context context;
    private List<Bitmap> images = Lists.newArrayList();
    private List<Saying> sayings = Lists.newArrayList();
    private int position;

    public ViewPageAdapter(Context context) {
        this.context = context;
    }

    public void addBitmap(Bitmap bitmap) {
        images.add(bitmap);
        notifyDataSetChanged();
    }

    public void addSaying(Saying saying) {
        sayings.add(saying);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((RelativeLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View v = LayoutInflater.from(context).inflate(R.layout.view_pager, null);

        ImageView imageView = (ImageView) v.findViewById(R.id.image);
        imageView.setImageBitmap(images.get(position));

        TextView textView = (TextView) v.findViewById(R.id.text_box);
        textView.setText(sayings.get(position).getText());

        ((ViewPager) container).addView(v, 0);
        return v;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((RelativeLayout) object);
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getCurrentSayingText() {
        return sayings.get(position).getText();
    }

    public String getCurrentSayingImageLocation() {
        return sayings.get(position).getImageLocation();
    }
}
