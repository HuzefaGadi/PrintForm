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
import com.huzefagadi.rashida.printform.bean.MenuBean;
import com.huzefagadi.rashida.printform.bean.ServerBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rashida on 13/07/15.
 */
public class ServerAdapter extends ArrayAdapter<ServerBean> {

    private List<ServerBean> serverList;


    //Set <String> readNews;
    static class ServerViewHolder {
        TextView serverName;

    }


    public ServerAdapter(Context context, int textViewResourceId) {

        super(context, textViewResourceId);

        serverList = new ArrayList<ServerBean>();


    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();

    }

    ;

    @Override
    public void add(ServerBean object) {
        serverList.add(object);
        super.add(object);
    }

    @Override
    public int getCount() {
        return serverList.size();
    }

    @Override
    public ServerBean getItem(int index) {
        return serverList.get(index);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ServerViewHolder viewHolder;
        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.list_menu, parent, false);
            viewHolder = new ServerViewHolder();
            viewHolder.serverName = (TextView) row.findViewById(R.id.description);

            row.setTag(viewHolder);
        } else {
            viewHolder = (ServerViewHolder) row.getTag();
        }


        viewHolder.serverName.setText(serverList.get(position).getServerName());


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


    public void setListData(ArrayList<ServerBean> data) {

        serverList = data;
    }


}
