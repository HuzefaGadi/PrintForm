package com.huzefagadi.rashida.printform;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.epson.eposprint.BatteryStatusChangeEventListener;
import com.epson.eposprint.Builder;
import com.epson.eposprint.EposException;
import com.epson.eposprint.Print;
import com.epson.eposprint.StatusChangeEventListener;
import com.huzefagadi.rashida.printform.bean.ItemBean;
import com.huzefagadi.rashida.printform.bean.MenuBean;
import com.huzefagadi.rashida.printform.bean.ServerBean;
import com.huzefagadi.rashida.printform.bean.TextBean;
import com.huzefagadi.rashida.printform.printer.ShowMsg;
import com.huzefagadi.rashida.printform.printer.TextActivity;
import com.huzefagadi.rashida.printform.utility.Constants;
import com.huzefagadi.rashida.printform.utility.DatabaseHandler;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainActivity extends Activity implements StatusChangeEventListener, BatteryStatusChangeEventListener {


    SimpleDateFormat dateFormatForPrint = new SimpleDateFormat("h:mm a  dd/MM/yyyy");

    Context context;
    DatabaseHandler db;
    List<MenuBean> listOfMenus;
    List<ItemBean> listOfItems;
    TableLayout tableLayout;
    Button addition, subtraction, print, customText, settings;
    LinearLayout addSelectedTextArea;
    Button clear;
    List<ItemBean> listOfTexts;
    ItemBean selectedItem;
    int startIdCustomText = 200000;
    Map<ItemBean, Integer> mapForCountOfTexts;
    List<TextBean> listOfCustomTexts;
    int selectedTextPosition = 0;
    static final int SEND_TIMEOUT = 10 * 1000;
    static final int SIZEWIDTH_MAX = 8;
    static final int SIZEHEIGHT_MAX = 8;
    StartActivity global;
    boolean exitActivity = false;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor edit;
    Spinner serverSpinner;
    List<ServerBean> listOfServers;
    List listServerNames;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        sharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCES, MODE_PRIVATE);
        edit = sharedPreferences.edit();
        db = new DatabaseHandler(context);
        tableLayout = (TableLayout) findViewById(R.id.tableLayout);
        addSelectedTextArea = (LinearLayout) findViewById(R.id.addButtons);
        clear = (Button) findViewById(R.id.clear);
        listOfCustomTexts = new ArrayList<TextBean>();
        listOfMenus = db.getAllMenus();
        listOfItems = db.getAllNonMenuItems();
        listOfTexts = new ArrayList<ItemBean>();
        mapForCountOfTexts = new HashMap<ItemBean, Integer>();
        addition = (Button) findViewById(R.id.addition);
        subtraction = (Button) findViewById(R.id.substract);
        print = (Button) findViewById(R.id.print);
        settings = (Button) findViewById(R.id.settings);
        customText = (Button) findViewById(R.id.addText);
        serverSpinner = (Spinner) findViewById(R.id.serverSpinner);


        listOfServers = db.getAllServers();
        listServerNames = new ArrayList();
        for (ServerBean server : listOfServers) {
            listServerNames.add(server.getServerName());
        }
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(context,
                android.R.layout.simple_spinner_item, listServerNames);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        serverSpinner.setAdapter(dataAdapter);

        serverSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                edit.putInt(Constants.SERVER_NAME, position);
                edit.commit();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        serverSpinner.setSelection(sharedPreferences.getInt(Constants.SERVER_NAME, 0));
        global = (StartActivity) getApplicationContext();
        addition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (selectedItem != null) {
                    Integer count = mapForCountOfTexts.get(selectedItem);
                    count++;
                    mapForCountOfTexts.put(selectedItem, count);

                    TextView textView = (TextView) addSelectedTextArea.findViewById(selectedItem.getId());
                    textView.setText(count + " x " + selectedItem.getDescription());
                }


            }
        });
        subtraction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (selectedItem != null) {
                    Integer count = mapForCountOfTexts.get(selectedItem);
                    count--;
                    mapForCountOfTexts.put(selectedItem, count);

                    if (count == 0) {
                        mapForCountOfTexts.remove(selectedItem);
                        listOfTexts.remove(selectedItem);
                        addSelectedTextArea.removeView(addSelectedTextArea.findViewById(selectedItem.getId()));
                        selectedItem = null;

                    } else {
                        TextView textView = (TextView) addSelectedTextArea.findViewById(selectedItem.getId());
                        textView.setText(count + " x " + selectedItem.getDescription());
                    }
                }
            }
        });
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearScreen();
            }
        });


        print.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StringBuilder sb = new StringBuilder();

                sb.append(dateFormatForPrint.format(new Date()));
                sb.append("\n");
                sb.append(serverSpinner.getSelectedItem());
                sb.append("\n");
                sb.append("_____________________");
                sb.append("\n");
                sb.append("\n");
                sb.append("\n");

                for (int i = 0; i < addSelectedTextArea.getChildCount(); i++) {
                    sb.append(((TextView) addSelectedTextArea.getChildAt(i)).getText());
                    sb.append("\n");
                }
                printText(sb.toString());
            }
        });

        customText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogForAddingText();
            }
        });

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SettingsActivity.class);
                startActivityForResult(intent, 1);
            }
        });

        populateTable();

    }

    public void clearScreen() {
        addSelectedTextArea.removeAllViews();
        listOfTexts.clear();
        mapForCountOfTexts.clear();
        listOfCustomTexts.clear();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            listOfMenus = db.getAllMenus();
            listOfItems = db.getAllNonMenuItems();
            tableLayout.removeAllViews();
            populateTable();
            global = (StartActivity) getApplicationContext();
            listOfServers = db.getAllServers();
            listServerNames = new ArrayList();
            for (ServerBean server : listOfServers) {
                listServerNames.add(server.getServerName());
            }
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(context,
                    android.R.layout.simple_spinner_item, listServerNames);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            serverSpinner.setAdapter(dataAdapter);
        }
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

    public void populateTable() {
        populateItems();
        populateMenus();
    }

    public void populateItems() {
        TableRow.LayoutParams param = new TableRow.LayoutParams(
                0,
                TableRow.LayoutParams.WRAP_CONTENT, 1.0f);
        param.setMargins(0, 0, 20, 0);


        for (int i = 0; i < listOfItems.size(); i += 4) {
            /*Create a new row to be added. */
            TableRow tr = new TableRow(this);
            tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
            tr.setPadding(0, 10, 0, 10);
            if (i >= listOfItems.size()) {
                //populateMenus(tr,0);

            } else {
                Button b1 = (Button) getLayoutInflater().inflate(R.layout.custom_layout, null);
                // Button b1 = new Button(this);
                b1.setLayoutParams(param);

                b1.setText(listOfItems.get(i).getDescription());

                //  b1.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
            /* Add Button to row. */
                final ItemBean item1 = listOfItems.get(i);

                b1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        addButtonsToPrintScreen(item1);
                    }
                });

                tr.addView(b1);
            }
            if (i + 1 >= listOfItems.size()) {
                // populateMenus(tr,1);

            } else {
                Button b2 = (Button) getLayoutInflater().inflate(R.layout.custom_layout, null);
                b2.setLayoutParams(param);
                b2.setText(listOfItems.get(i + 1).getDescription());
                //  b2.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
            /* Add Button to row. */

                final ItemBean item2 = listOfItems.get(i + 1);

                b2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        addButtonsToPrintScreen(item2);
                    }
                });

                tr.addView(b2);
            }

            if (i + 2 >= listOfItems.size()) {
                // populateMenus(tr,2);

            } else {
                Button b3 = (Button) getLayoutInflater().inflate(R.layout.custom_layout, null);
                b3.setLayoutParams(param);
                b3.setText(listOfItems.get(i + 2).getDescription());
                //b3.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
            /* Add Button to row. */
                final ItemBean item3 = listOfItems.get(i + 2);

                b3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        addButtonsToPrintScreen(item3);
                    }
                });

                tr.addView(b3);
            }

            if (i + 3 >= listOfItems.size()) {
                //populateMenus(tr,3);

            } else {
                 /* Create a Button to be the row-content. */
                Button b4 = (Button) getLayoutInflater().inflate(R.layout.custom_layout, null);
                b4.setLayoutParams(param);
                b4.setText(listOfItems.get(i + 3).getDescription());
                // b4.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
            /* Add Button to row. */
                final ItemBean item4 = listOfItems.get(i + 3);

                b4.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        addButtonsToPrintScreen(item4);
                    }
                });

                tr.addView(b4);
            }
            /* Add row to TableLayout. */
            //tr.setBackgroundResource(R.drawable.sf_gradient_03);
            tableLayout.addView(tr, new TableLayout.LayoutParams(TableLayout.LayoutParams.FILL_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));


        }
    }

    public void populateMenus() {
        TableRow.LayoutParams param = new TableRow.LayoutParams(
                0,
                TableRow.LayoutParams.WRAP_CONTENT, 1.0f);
        param.setMargins(0, 0, 20, 0);
       /* if(position!=4)
        {
            for(int i=0;i<4-position;i++)
            {
                if (i >= listOfMenus.size()) {

                } else {
                    Button b1 = new Button(this);
                    b1.setText(listOfMenus.get(i).getDescription());
                    b1.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
            *//* Add Button to row. *//*
                    final MenuBean menuBean = listOfMenus.get(i);
                    b1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showDialogForMenu(menuBean.getId());
                        }
                    });

                    tableRow.addView(b1);
                }
                if (i + 1 >= listOfMenus.size()) {

                } else {
                    Button b1 = new Button(this);
                    b1.setText(listOfMenus.get(i+1).getDescription());
                    b1.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
            *//* Add Button to row. *//*
                    final MenuBean menuBean = listOfMenus.get(i+1);
                    b1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showDialogForMenu(menuBean.getId());
                        }
                    });

                    tableRow.addView(b1);
                }
                if (i+2 >= listOfMenus.size()) {

                } else {
                    Button b1 = new Button(this);
                    b1.setText(listOfMenus.get(i+2).getDescription());
                    b1.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
            *//* Add Button to row. *//*
                    final MenuBean menuBean = listOfMenus.get(i+2);
                    b1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showDialogForMenu(menuBean.getId());
                        }
                    });

                    tableRow.addView(b1);
                }
                if (i+3 >= listOfMenus.size()) {

                } else {
                    Button b1 = new Button(this);
                    b1.setText(listOfMenus.get(i+3).getDescription());
                    b1.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
            *//* Add Button to row. *//*
                    final MenuBean menuBean = listOfMenus.get(i+3);
                    b1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showDialogForMenu(menuBean.getId());
                        }
                    });

                    tableRow.addView(b1);
                }

                tableLayout.addView(tableRow, new TableLayout.LayoutParams(TableLayout.LayoutParams.FILL_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
            }
        }*/

        for (int i = 0; i < listOfMenus.size(); i += 4) {
            /*Create a new row to be added. */
            TableRow tr = new TableRow(this);
            tr.setPadding(0, 10, 0, 10);
            tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
            {
                if (i >= listOfMenus.size()) {

                } else {
                    Button b1 = (Button) getLayoutInflater().inflate(R.layout.custom_layout, null);
                    b1.setLayoutParams(param);
                    b1.setText(listOfMenus.get(i).getDescription());
                    //  b1.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
            /* Add Button to row. */
                    final MenuBean menuBean = listOfMenus.get(i);
                    b1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showDialogForMenu(menuBean.getId());
                        }
                    });

                    tr.addView(b1);
                }
                if (i + 1 >= listOfMenus.size()) {

                } else {
                    Button b1 = (Button) getLayoutInflater().inflate(R.layout.custom_layout, null);
                    b1.setLayoutParams(param);
                    b1.setText(listOfMenus.get(i + 1).getDescription());
                    //  b1.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
            /* Add Button to row. */
                    final MenuBean menuBean = listOfMenus.get(i + 1);
                    b1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showDialogForMenu(menuBean.getId());
                        }
                    });

                    tr.addView(b1);
                }
                if (i + 2 >= listOfMenus.size()) {

                } else {
                    Button b1 = (Button) getLayoutInflater().inflate(R.layout.custom_layout, null);
                    b1.setLayoutParams(param);
                    b1.setText(listOfMenus.get(i + 2).getDescription());
                    // b1.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
            /* Add Button to row. */
                    final MenuBean menuBean = listOfMenus.get(i + 2);
                    b1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showDialogForMenu(menuBean.getId());
                        }
                    });

                    tr.addView(b1);
                }
                if (i + 3 >= listOfMenus.size()) {

                } else {
                    Button b1 = (Button) getLayoutInflater().inflate(R.layout.custom_layout, null);
                    b1.setLayoutParams(param);
                    b1.setText(listOfMenus.get(i + 3).getDescription());
                    // b1.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
            /* Add Button to row. */
                    final MenuBean menuBean = listOfMenus.get(i + 3);
                    b1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showDialogForMenu(menuBean.getId());
                        }
                    });

                    tr.addView(b1);
                }

                tableLayout.addView(tr, new TableLayout.LayoutParams(TableLayout.LayoutParams.FILL_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
            }
            /* Add row to TableLayout. */
            //tr.setBackgroundResource(R.drawable.sf_gradient_03);
            // tableLayout.addView(tr, new TableLayout.LayoutParams(TableLayout.LayoutParams.FILL_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));


        }
    }

    public void addButtonsToPrintScreen(final ItemBean itemBean) {
        boolean isItemAlreadyPresent = false;
        for (ItemBean item : listOfTexts) {
            if (item.getId() == itemBean.getId()) {
                isItemAlreadyPresent = true;
                break;
            }
        }
        if (isItemAlreadyPresent) {

            Toast.makeText(context, "Text is already there!!", Toast.LENGTH_SHORT).show();
        } else {
            listOfTexts.add(itemBean);
            mapForCountOfTexts.put(itemBean, 1);
            final TextView text = new TextView(context);
            text.setLayoutParams(new TableRow.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            text.setText(itemBean.getDescription());
            text.setTextSize(30.0f);
            text.setSingleLine();
            text.setGravity(Gravity.CENTER);
            text.setId(itemBean.getId());
            text.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    text.setTextColor(getResources().getColor(R.color.material_deep_teal_500));
                    text.setSelected(true);
                    selectedItem = itemBean;
                    for (ItemBean item : listOfTexts) {

                        if (item.getId() != v.getId()) {
                            TextView textToChange = (TextView) findViewById(item.getId());
                            textToChange.setSelected(false);
                            textToChange.setTextColor(getResources().getColor(R.color.abc_secondary_text_material_light));
                        }

                    }

                }
            });
            addSelectedTextArea.addView(text);
        }

    }

    public void addTextToPrintScreen(final TextBean textBean) {

        listOfCustomTexts.add(textBean);

        final TextView text = new TextView(context);
        text.setLayoutParams(new TableRow.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        text.setText(textBean.getDescription());
        text.setTextSize(30.0f);
        text.setSingleLine();
        text.setGravity(Gravity.CENTER);
        text.setId(textBean.getId());
        text.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                showDialogForUpdatingText(textBean);
            }
        });
        addSelectedTextArea.addView(text);


    }

    public void showDialogForMenu(int id) {

        // custom dialog
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.menu_custom_layout);
        dialog.setTitle("Select Items");
        List<ItemBean> listOfItemsWithMenu = db.getAllItemsForMenu(id);
        LinearLayout linearLayout = (LinearLayout) dialog.findViewById(R.id.relativeLayout);

        for (final ItemBean itemBean : listOfItemsWithMenu) {
            Button b1 = new Button(this);
            b1.setText(itemBean.getDescription());
            b1.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
            b1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addButtonsToPrintScreen(itemBean);
                    dialog.dismiss();
                }
            });
            linearLayout.addView(b1);
        }
        dialog.show();
    }

    private void showDialogForAddingText() {
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.custom_dialog_menu);
        dialog.setTitle("Enter Text");

        // set the custom dialog components - text, image and button
        TextView text = (TextView) dialog.findViewById(R.id.addText);
        final EditText description = (EditText) dialog.findViewById(R.id.description);


        Button dialogOkButton = (Button) dialog.findViewById(R.id.dialogButtonOK);
        // if button is clicked, close the custom dialog
        dialogOkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String descriptionText = description.getText().toString();
                if (listOfCustomTexts.size() == 0) {
                    listOfCustomTexts.add(new TextBean(startIdCustomText, descriptionText));
                } else {
                    listOfCustomTexts.add(new TextBean(listOfCustomTexts.get(listOfCustomTexts.size() - 1).getId() + 1, descriptionText));
                }
                addTextToPrintScreen(listOfCustomTexts.get(listOfCustomTexts.size() - 1));
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

    private void showDialogForUpdatingText(final TextBean textBean) {
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.custom_dialog_menu);
        dialog.setTitle("Enter Text");


        int count = 0;
        for (TextBean text : listOfCustomTexts) {

            if (text.getId() == textBean.getId()) {
                selectedTextPosition = count;
                break;
            }
            count++;
        }

        // set the custom dialog components - text, image and button
        TextView text = (TextView) dialog.findViewById(R.id.addText);
        final EditText description = (EditText) dialog.findViewById(R.id.description);

        description.setText(listOfCustomTexts.get(selectedTextPosition).getDescription());

        Button dialogOkButton = (Button) dialog.findViewById(R.id.dialogButtonOK);
        // if button is clicked, close the custom dialog
        dialogOkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String descriptionText = description.getText().toString();


                listOfCustomTexts.set(selectedTextPosition, new TextBean(textBean.getId(), descriptionText));
                ((TextView) findViewById(textBean.getId())).setText(descriptionText);
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
                listOfCustomTexts.remove(selectedTextPosition);
                addSelectedTextArea.removeView(findViewById(textBean.getId()));
                dialog.dismiss();
            }
        });

        dialog.show();
    }


    private void printText(String text) {

        global = (StartActivity) getApplicationContext();
        Print printer = global.getPrinter();
        String deviceName = sharedPreferences.getString(Constants.PRINTER_OPEN_DEVICE_NAME, "");
        int enabled = sharedPreferences.getInt(Constants.PRINTER_ENABLED, 0);
        int interval = sharedPreferences.getInt(Constants.PRINTER_INTERVAL, 0);
        if (printer == null && !deviceName.equals("")) {
            printer = new Print(getApplicationContext());

            try {
                printer.openPrinter(global.getConnectionType(), global.getOpenDeviceName(), enabled, interval);
                printer.setStatusChangeEventCallback(this);
                printer.setBatteryStatusChangeEventCallback(this);


            } catch (Exception e) {
                printer = null;
                ShowMsg.showException(e, "openPrinter", this);

            }

            global.setPrinter(printer);

        }
        if (printer == null) {
            Toast.makeText(getApplicationContext(), "Please setup Printer first", Toast.LENGTH_SHORT).show();

        } else {


            if (text.isEmpty()) {
                ShowMsg.showError(R.string.errmsg_notext, this);
                return;
            }

            Builder builder = null;
            String method = "";
            try {
                //create builder

                method = "Builder";
                builder = new Builder(global.getPrinterName(), global.getLanguage(), getApplicationContext());

                //add command
                method = "addTextFont";
                builder.addTextFont(getBuilderFont());

                method = "addTextAlign";
                builder.addTextAlign(getBuilderAlign());

                method = "addTextLineSpace";
                builder.addTextLineSpace(getBuilderLineSpace());

                method = "addTextLang";
                builder.addTextLang(getBuilderLanguage());

                method = "addTextSize";
                builder.addTextSize(getBuilderSizeW(), getBuilderSizeH());

                method = "addTextStyle";
                builder.addTextStyle(Builder.FALSE, getBuilderStyleUnderline(), getBuilderStyleBold(), Builder.COLOR_1);

                method = "addTextPosition";
                builder.addTextPosition(getBuilderXPosition());

                method = "addText";
                builder.addText(text);

                method = "addFeedLine";
                builder.addFeedLine(getBuilderFeedUnit());

                method = "addCut";
                builder.addCut(getBuilderType());

                //send builder data
                int[] status = new int[1];
                int[] battery = new int[1];
                try {

                    printer.sendData(builder, SEND_TIMEOUT, status, battery);
                    ShowMsg.showStatus(EposException.SUCCESS, status[0], battery[0], this);
                    stopPrinter();
                } catch (EposException e) {
                    e.printStackTrace();
                    System.out.println("error occured : " + e.getErrorStatus());
                    ShowMsg.showStatus(e.getErrorStatus(), e.getPrinterStatus(), e.getBatteryStatus(), this);
                    stopPrinter();
                }
            } catch (Exception e) {
                e.printStackTrace();

                ShowMsg.showException(e, method, this);
                stopPrinter();
            }

            //remove builder
            if (builder != null) {
                try {
                    builder.clearCommandBuffer();
                    builder = null;
                } catch (Exception e) {
                    builder = null;
                }
            }
        }
    }

    private int getBuilderFont() {

        int font = sharedPreferences.getInt(Constants.TEXT_FONT, Builder.FONT_A);
        return font;
    }

    private int getBuilderAlign() {

        int align = sharedPreferences.getInt(Constants.TEXT_ALIGN, Builder.ALIGN_LEFT);
        return align;

    }

    private int getBuilderLineSpace() {
        int lineSpace = sharedPreferences.getInt(Constants.TEXT_LINE_SPACE, 10);
        return lineSpace;

    }

    private int getBuilderLanguage() {
        int language = sharedPreferences.getInt(Constants.TEXT_LANGUAGE, Builder.LANG_EN);
        return language;


    }

    private int getBuilderSizeW() {
        int width = sharedPreferences.getInt(Constants.TEXT_SIZE_WIDTH, 2);
        return width;
    }

    private int getBuilderSizeH() {
        int height = sharedPreferences.getInt(Constants.TEXT_SIZE_HEIGHT, 2);
        return height;
    }

    private int getBuilderStyleBold() {
        int bold = sharedPreferences.getInt(Constants.TEXT_STYLE_BOLD, Builder.FALSE);
        return bold;


    }

    private int getBuilderStyleUnderline() {

        int underline = sharedPreferences.getInt(Constants.TEXT_STYLE_UNDERLINE, Builder.FALSE);
        return underline;

    }

    private int getBuilderXPosition() {
        int position = sharedPreferences.getInt(Constants.TEXT_POSITION, 0);
        return position;

    }

    private int getBuilderType() {


        return Builder.CUT_FEED;

    }


    private int getBuilderFeedUnit() {

        int feed = sharedPreferences.getInt(Constants.TEXT_FEED_UNIT, 30);
        return feed;
    }

    @Override
    public void onStatusChangeEvent(final String deviceName, final int status) {
        runOnUiThread(new Runnable() {
            @Override
            public synchronized void run() {
                try {
                    ShowMsg.showStatusChangeEvent(deviceName, status, context);

                    if (status == EposException.SUCCESS) {
                        clearScreen();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    stopPrinter();
                }


            }
        });
    }

    @Override
    public void onBatteryStatusChangeEvent(final String deviceName, final int battery) {
        runOnUiThread(new Runnable() {
            @Override
            public synchronized void run() {

                try {
                    ShowMsg.showBatteryStatusChangeEvent(deviceName, battery, context);
                } catch (Exception e) {
                    e.printStackTrace();
                    stopPrinter();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        stopPrinter();
        super.onDestroy();
    }

    public void stopPrinter() {
        Print printer = global.getPrinter();
        if (printer != null) {
            try {
                printer.closePrinter();
                printer = null;
            } catch (Exception e) {
                e.printStackTrace();
                printer = null;
            }
            global.setPrinter(printer);
        }
    }
}

