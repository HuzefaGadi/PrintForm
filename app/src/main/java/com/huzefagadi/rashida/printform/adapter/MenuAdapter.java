package com.huzefagadi.rashida.printform.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.huzefagadi.rashida.printform.R;

import com.huzefagadi.rashida.printform.bean.MenuBean;


public class MenuAdapter extends ArrayAdapter<MenuBean> {

    private List<MenuBean> menuList;


    //Set <String> readNews;
    static class MenuViewHolder {
        TextView description;

    }


    public MenuAdapter(Context context, int textViewResourceId) {

        super(context, textViewResourceId);

        menuList = new ArrayList<MenuBean>();


    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();

    }

    ;

    @Override
    public void add(MenuBean object) {
        menuList.add(object);
        super.add(object);
    }

    @Override
    public int getCount() {
        return menuList.size();
    }

    @Override
    public MenuBean getItem(int index) {
        return menuList.get(index);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        MenuViewHolder viewHolder;
        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.list_menu, parent, false);
            viewHolder = new MenuViewHolder();
            viewHolder.description = (TextView) row.findViewById(R.id.description);

            row.setTag(viewHolder);
        } else {
            viewHolder = (MenuViewHolder) row.getTag();
        }


        viewHolder.description.setText(menuList.get(position).getDescription());


        System.out.print("Bulletin ids in Nofification Adapter at position: " + position);

	/*
     *
	 * you need to implement the image download logic here
	 */

        //	viewHolder.image.setImageBitmap(bm);

        return row;
    }

    public Bitmap decodeToBitmap(byte[] decodedByte) {
        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
    }


    public void setListData(ArrayList<MenuBean> data) {

        menuList = data;
    }


}
