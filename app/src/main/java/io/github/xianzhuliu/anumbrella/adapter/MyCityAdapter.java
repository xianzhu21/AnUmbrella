package io.github.xianzhuliu.anumbrella.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import io.github.xianzhuliu.anumbrella.R;
import io.github.xianzhuliu.anumbrella.model.MyCityBean;

/**
 * Created by LiuXianzhu on 27/10/2016.
 * Contact: liuxianzhu0221@gmail.com
 */

public class MyCityAdapter extends BaseAdapter {

    private List<MyCityBean> mList;
    private LayoutInflater mInflater;

    public MyCityAdapter(Context context, List<MyCityBean> list) {
        mList = list;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.city_item, null);
            holder = new ViewHolder();
            holder.myCityName = (TextView) convertView.findViewById(R.id.my_city_name);
            holder.myCityTmp = (TextView) convertView.findViewById(R.id.my_city_tmp);
            holder.imgMyCity = (ImageView) convertView.findViewById(R.id.img_my_city);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        MyCityBean bean = mList.get(position);
        holder.myCityName.setText(bean.getMyCityName());
        holder.myCityTmp.setText(bean.getMyCityTmp());
        holder.imgMyCity.setImageResource(bean.getMyCityImgResId());

        return convertView;
    }

    class ViewHolder {
        public TextView myCityName;
        public TextView myCityTmp;
        public ImageView imgMyCity;
    }
}
