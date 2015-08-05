package com.huzefagadi.rashida.printform;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.huzefagadi.rashida.printform.adapter.ItemAdapter;
import com.huzefagadi.rashida.printform.adapter.ServerAdapter;
import com.huzefagadi.rashida.printform.bean.ItemBean;
import com.huzefagadi.rashida.printform.bean.MenuBean;
import com.huzefagadi.rashida.printform.bean.ServerBean;
import com.huzefagadi.rashida.printform.utility.DatabaseHandler;

import java.util.ArrayList;
import java.util.List;


public class AddServerActivity extends Activity {


    Button addServer;
    Context context;
    DatabaseHandler db;
    ServerAdapter serverAdapter;
    ListView listView;
    ArrayList<ServerBean> listOfServer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_server);
        context = this;
        db=new DatabaseHandler(context);
        listView = (ListView) findViewById(R.id.addItemListView);

        serverAdapter = new ServerAdapter(this, R.layout.list_item);
        listOfServer = db.getAllServers();
        serverAdapter.setListData(listOfServer);
        listView.setAdapter(serverAdapter);


        addServer = (Button) findViewById(R.id.addItem);
        addServer.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                {
                    showDialog();
                }
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           int pos, long id) {
                // TODO Auto-generated method stub

                Log.v("long clicked", "pos: " + pos);
                showDialogForUpdate(pos);

                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    public void showDialog()
    {

        // custom dialog
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.custom_dialog_menu);
        dialog.setTitle("Enter your details");
        final EditText serverName = (EditText) dialog.findViewById(R.id.description);

        Button dialogOkButton = (Button) dialog.findViewById(R.id.dialogButtonOK);
        // if button is clicked, close the custom dialog
        dialogOkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String serverNameText = serverName.getText().toString();
                db.addServer(serverNameText);

                listOfServer=db.getAllServers();
                serverAdapter.setListData(listOfServer);
                serverAdapter.notifyDataSetChanged();
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

    public void showDialogForUpdate(final int position)
    {

        final ServerBean serverBean = listOfServer.get(position);
        int selectedPositionForMenu=0;
        // custom dialog
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.custom_dialog_menu);
        dialog.setTitle("Enter your details To Update");


        final EditText serverName = (EditText) dialog.findViewById(R.id.description);
        serverName.setText(serverBean.getServerName());


        Button dialogOkButton = (Button) dialog.findViewById(R.id.dialogButtonOK);
        // if button is clicked, close the custom dialog
        dialogOkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String serverNameText = serverName.getText().toString();
                db.updateServer(new ServerBean(listOfServer.get(position).getId(),serverNameText));
                listOfServer = db.getAllServers();
                serverAdapter.setListData(listOfServer);
                serverAdapter.notifyDataSetChanged();
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


        Button dialogDeleteButton = (Button) dialog.findViewById(R.id.dialogButtonDelete);
        // if button is clicked, close the custom dialog
        dialogDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.deleteServer(serverBean);
                listOfServer = db.getAllServers();
                serverAdapter.setListData(listOfServer);
                serverAdapter.notifyDataSetChanged();
                dialog.dismiss();
            }
        });
        dialog.show();
    }}
