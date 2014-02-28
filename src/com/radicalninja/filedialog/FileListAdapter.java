package com.radicalninja.filedialog;

import android.content.Context;
import android.database.DataSetObserver;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;

import java.util.ArrayList;
import java.util.List;


public class FileListAdapter implements ListAdapter {

    private final Context mContext;
    private final List<IconItemView.IconItem> mValues;
    private ArrayList<DataSetObserver> observers = new ArrayList<DataSetObserver>();

    public FileListAdapter(Context context) {
        mContext = context;
        mValues = new ArrayList<IconItemView.IconItem>();
    }

    public FileListAdapter(Context context, List<IconItemView.IconItem> values) {
        mContext = context;
        mValues = values;
    }

    public void addItem(IconItemView.IconItem item) {
        mValues.add(item);
        notifyDataSetChanged();
    }

    public void addItems(List<IconItemView.IconItem> items) {
        mValues.addAll(items);
        notifyDataSetChanged();
    }

    public void removeItems(List<IconItemView.IconItem> items) {
        mValues.removeAll(items);
        notifyDataSetChanged();
    }

    public void removeItem(IconItemView.IconItem item) {
        mValues.remove(item);
        notifyDataSetChanged();
    }

    public void clearItems() {
        mValues.clear();
        notifyDataSetChanged();
    }

    public void setItems(List<IconItemView.IconItem> values) {
        mValues.clear();
        mValues.addAll(values);
        notifyDataSetChanged();
    }

    public void notifyDataSetChanged() {
        //for (DataSetObserver observer : observers) {
        //    observer.onChanged();
        //}
    }

    @Override
    public boolean areAllItemsEnabled() {
        return true;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver dataSetObserver) {
        observers.add(dataSetObserver);
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver dataSetObserver) {
        observers.remove(dataSetObserver);
    }

    @Override
    public int getCount() {
        return mValues.size();
    }

    @Override
    public IconItemView.IconItem getItem(int i) {
        return mValues.get(i);
    }

    @Override
    public long getItemId(int i) {
        return mValues.get(i).type.getValue();
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public int getItemViewType(int i) {
        return mValues.get(i).type.getValue();
    }

    @Override
    public int getViewTypeCount() {
        return IconItemView.IconItem.IconType.size;
    }

    @Override
    public boolean isEmpty() {
        return (mValues.size() == 0);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        IconItemView view = (IconItemView) convertView;
        if (view == null) {
            view = new IconItemView(mContext);
        }
        //IconItemView.IconItem icon = getItem(position);
        //Log.e("ListAdapter", String.format("Item Label: %s | Item Type: %d", icon.label, icon.type.getValue()));
        //view.bind(icon);
        view.bind(getItem(position));
        return view;
    }

    @Override
    public boolean isEnabled(int i) {
        //todo
        return true;
    }
}
