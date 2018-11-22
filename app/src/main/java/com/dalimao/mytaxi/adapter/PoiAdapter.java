package com.dalimao.mytaxi.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.dalimao.mytaxi.R;

import java.util.List;

public class PoiAdapter extends ArrayAdapter {
    List mData;
    private final LayoutInflater inflater;

    public PoiAdapter(Context context, List data) {
        super(context, R.layout.poi_list_item);
        this.mData = data;
        inflater = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {

        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.poi_list_item, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.tv.setText((String) getItem(position));
        return convertView;
    }

    public void notifyData(List<String> data) {
        mData.clear();
        mData.addAll(data);
        notifyDataSetChanged();
    }

    static class ViewHolder {

        public TextView tv;

        public ViewHolder(View convertView) {
            tv = convertView.findViewById(R.id.name);
        }
    }
}
