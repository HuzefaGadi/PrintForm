package com.huzefagadi.rashida.printform;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
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
import com.huzefagadi.rashida.printform.bean.ItemBean;
import com.huzefagadi.rashida.printform.bean.MenuBean;
import com.huzefagadi.rashida.printform.utility.DatabaseHandler;

import java.util.ArrayList;
import java.util.List;


public class AddItemsActivity extends Activity {

    Button addMenu,addItem;
    Context context;
    DatabaseHandler db;
    ItemAdapter itemAdapter;
    ListView listView;
    ArrayList <ItemBean>listForItem;
    ArrayList<MenuBean> listForMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_items);
        context = this;

        db = new DatabaseHandler(context);

        db=new DatabaseHandler(context);
        listView = (ListView) findViewById(R.id.addItemListView);

        itemAdapter = new ItemAdapter(this, R.layout.list_item);
        listForItem = db.getAllItems();
        itemAdapter.setListData(listForItem);
        listView.setAdapter(itemAdapter);


        addItem = (Button) findViewById(R.id.addItem);
        addItem.setOnClickListener(new View.OnClickListener()
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
        dialog.setContentView(R.layout.custom_dialog_item);
        dialog.setTitle("Enter your details");
        listForMenu = db.getAllMenus();
        List newList = new ArrayList();
        final RadioGroup radioGroup = (RadioGroup) dialog.findViewById(R.id.radioGroup);
        final RadioButton menuItem = (RadioButton) dialog.findViewById(R.id.menuItem);
        final RadioButton item = (RadioButton) dialog.findViewById(R.id.item);

        for(MenuBean menuBean:listForMenu)
        {
            newList.add(menuBean.getDescription());
        }
        final Spinner spinner = (Spinner)dialog.findViewById(R.id.spinner);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(context,
                android.R.layout.simple_spinner_item, newList);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);

        final EditText description = (EditText) dialog.findViewById(R.id.description);



        Button dialogOkButton = (Button) dialog.findViewById(R.id.dialogButtonOK);
        // if button is clicked, close the custom dialog
        dialogOkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String descriptionText = description.getText().toString();
                if(radioGroup.getCheckedRadioButtonId() == R.id.menuItem)
                {
                    db.addItem(new ItemBean(0,listForMenu.get(spinner.getSelectedItemPosition()),descriptionText));
                }
                else
                {
                    db.addItem(new ItemBean(0,null,descriptionText));
                }

                listForItem=db.getAllItems();
                itemAdapter.setListData(listForItem);
                itemAdapter.notifyDataSetChanged();
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
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                if (checkedId == R.id.menuItem)
                {
                    spinner.setEnabled(true);

                } else if (checkedId == R.id.item)
                {
                    spinner.setEnabled(false);
                }
            }
        });
        dialog.show();
    }

    public void showDialogForUpdate(int position)
    {

        final ItemBean itemBean = listForItem.get(position);
        int selectedPositionForMenu=0;
        // custom dialog
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.custom_dialog_update_item);
        dialog.setTitle("Enter your details To Update");
        listForMenu = db.getAllMenus();
        List newList = new ArrayList();
        final RadioGroup radioGroup = (RadioGroup) dialog.findViewById(R.id.radioGroup);
        final RadioButton menuItem = (RadioButton) dialog.findViewById(R.id.menuItem);
        final RadioButton item = (RadioButton) dialog.findViewById(R.id.item);
        int count=0;
        for(MenuBean menuBean:listForMenu)
        {
            newList.add(menuBean.getDescription());
            if(listForItem.get(position).getMenu()!=null)
            {
                if(menuBean.getId() == listForItem.get(position).getMenu().getId())
                {
                    selectedPositionForMenu=count;
                }
            }

            count++;
        }
        final Spinner spinner = (Spinner)dialog.findViewById(R.id.spinner);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(context,
                android.R.layout.simple_spinner_item, newList);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);

        final EditText description = (EditText) dialog.findViewById(R.id.description);
        description.setText(itemBean.getDescription());
        if(listForItem.get(position).getMenu()!=null)
        {
            menuItem.setChecked(true);
            spinner.setSelection(selectedPositionForMenu);
            spinner.setEnabled(true);
        }
        else
        {
            item.setChecked(true);
            spinner.setEnabled(false);
            spinner.setSelected(false);
        }


        Button dialogOkButton = (Button) dialog.findViewById(R.id.dialogButtonOK);
        // if button is clicked, close the custom dialog
        dialogOkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String descriptionText = description.getText().toString();
                int result;
                if (radioGroup.getCheckedRadioButtonId() == R.id.menuItem) {
                    result = db.updateItem(new ItemBean(itemBean.getId(), listForMenu.get(spinner.getSelectedItemPosition()), descriptionText));
                } else {
                    result = db.updateItem(new ItemBean(itemBean.getId(), null, descriptionText));
                }
                System.out.print("result " + result);
                listForItem = db.getAllItems();
                itemAdapter.setListData(listForItem);
                itemAdapter.notifyDataSetChanged();
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
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                if (checkedId == R.id.menuItem) {
                    spinner.setEnabled(true);

                } else if (checkedId == R.id.item) {
                    spinner.setEnabled(false);
                }
            }
        });

        Button dialogDeleteButton = (Button) dialog.findViewById(R.id.dialogButtonDelete);
        // if button is clicked, close the custom dialog
        dialogDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.deleteItem(itemBean);

                listForItem = db.getAllItems();
                itemAdapter.setListData(listForItem);
                itemAdapter.notifyDataSetChanged();
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}
