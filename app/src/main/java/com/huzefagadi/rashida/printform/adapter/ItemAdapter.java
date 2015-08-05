package com.huzefagadi.rashida.printform.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.huzefagadi.rashida.printform.R;
import com.huzefagadi.rashida.printform.bean.ItemBean;
import com.huzefagadi.rashida.printform.bean.MenuBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rashida on 06/07/15.
 */
public class ItemAdapter extends ArrayAdapter<ItemBean> {

    private List<ItemBean> itemList;



    static class ItemViewHolder {
        TextView menuDescription;
        TextView description;
    }


    public ItemAdapter(Context context, int textViewResourceId) {

        super(context, textViewResourceId);

        itemList = new ArrayList<ItemBean>();


    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();

    }

    ;

    @Override
    public void add(ItemBean object) {
        itemList.add(object);
        super.add(object);
    }

    @Override
    public int getCount() {
        return itemList.size();
    }

    @Override
    public ItemBean getItem(int index) {
        return itemList.get(index);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ItemViewHolder viewHolder;
        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.list_item, parent, false);
            viewHolder = new ItemViewHolder();
            viewHolder.menuDescription = (TextView) row.findViewById(R.id.menuDescription);
            viewHolder.description = (TextView) row.findViewById(R.id.description);
            row.setTag(viewHolder);
        } else {
            viewHolder = (ItemViewHolder) row.getTag();
        }
        MenuBean menuBean = itemList.get(position).getMenu();
        if(menuBean == null)
        {

            viewHolder.menuDescription.setText("");
            viewHolder.description.setText(itemList.get(position).getDescription());
        }
        else
        {
            viewHolder.menuDescription.setText(menuBean.getDescription());
            viewHolder.description.setText(itemList.get(position).getDescription());

        }

        System.out.print("Bulletin ids in Nofification Adapter at position: " + position);
        return row;
    }

    public Bitmap decodeToBitmap(byte[] decodedByte) {
        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
    }


    public void setListData(ArrayList<ItemBean> data) {

        itemList = data;
    }


}
