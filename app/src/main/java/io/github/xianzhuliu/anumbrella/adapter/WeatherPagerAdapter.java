package io.github.xianzhuliu.anumbrella.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.List;

import io.github.xianzhuliu.anumbrella.activity.WeatherFragment;

/**
 * Created by LiuXianzhu on 08/11/2016.
 * Contact: liuxianzhu0221@gmail.com
 */

public class WeatherPagerAdapter extends FragmentStatePagerAdapter {
    private List<WeatherFragment> list;

    public WeatherPagerAdapter(FragmentManager fm, List<WeatherFragment> list) {
        super(fm);
        this.list = list;
    }

    @Override
    public Fragment getItem(int position) {
        return list.get(position);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
}
