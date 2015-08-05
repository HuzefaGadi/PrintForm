package com.huzefagadi.rashida.printform;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;


import com.huzefagadi.rashida.printform.adapter.MenuAdapter;
import com.huzefagadi.rashida.printform.bean.MenuBean;
import com.huzefagadi.rashida.printform.utility.DatabaseHandler;

import java.util.ArrayList;


public class AddMenuActivity extends Activity {

    ListView listView;
    Button addMenu;
    Context context;
    MenuAdapter menuAdapter;
    DatabaseHandler db;
    ArrayList list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_menu);
        context=this;
        listView = (ListView) findViewById(R.id.addMenuListView);
        addMenu = (Button) findViewById(R.id.addMenu);
        menuAdapter = new MenuAdapter(this, R.layout.list_menu);
        db=new DatabaseHandler(context);

        list = db.getAllMenus();
        menuAdapter.setListData(list);
        listView.setAdapter(menuAdapter);

        addMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // custom dialog
                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.custom_dialog_menu);
                dialog.setTitle("Enter your details");

                // set the custom dialog components - text, image and button
                TextView text = (TextView) dialog.findViewById(R.id.addText);
                final EditText description = (EditText) dialog.findViewById(R.id.description);



                Button dialogOkButton = (Button) dialog.findViewById(R.id.dialogButtonOK);
                // if button is clicked, close the custom dialog
                dialogOkButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String descriptionText = description.getText().toString();
                        db.addMenu(new MenuBean(0,descriptionText));
                        list=db.getAllMenus();
                        menuAdapter.setListData(list);
                        menuAdapter.notifyDataSetChanged();
                        dialog.dismiss();
                    }
                });

                Button dialogCancelButton = (Button) dialog.findViewById(R.id.dialogButtonCancel);
                // if button is clicked, close the custom dialog
                dialogCancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
