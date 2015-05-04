package me.tatocaster.ibsuoid.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;

/**
 * Created by tatocaster on 2015-05-05.
 */
public class TranscriptListAdapter extends BaseAdapter {

    ArrayList<String> entries = null;

    @Override
    public int getCount() {
        if(entries == null) {
            return 0;
        } else {
            return entries.size();
        }
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            Context context = parent.getContext();
//            convertView = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
        }
        if(entries == null) {
            return convertView;
        }
//        TextView title_text = (TextView) convertView.findViewById(R.id.title_text);
//        title_text.setText(entries.get(position));

        return convertView;
    }

    public void loadData(ArrayList<String> entries) {
        this.entries = entries;
        notifyDataSetChanged();
    }

}