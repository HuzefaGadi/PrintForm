package com.huzefagadi.rashida.printform.printer;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.epson.eposprint.BatteryStatusChangeEventListener;
import com.epson.eposprint.Builder;
import com.epson.eposprint.EposException;
import com.epson.eposprint.Print;
import com.epson.eposprint.StatusChangeEventListener;

import com.huzefagadi.rashida.printform.R;
import com.huzefagadi.rashida.printform.StartActivity;
import com.huzefagadi.rashida.printform.utility.Constants;

public class EPOSPrintSampleActivity extends Activity implements OnClickListener, StatusChangeEventListener, BatteryStatusChangeEventListener {

    static Print printer = null;
    StartActivity global;
    SharedPreferences sharedPreferences;



    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        sharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCES,MODE_PRIVATE);
        global = (StartActivity)getApplicationContext();
        //Registration ClickListener
        int[] target = {
                R.id.button_discovery,
                R.id.button_open,
                R.id.button_close,
                R.id.button_text,
                R.id.button_getstatus,
                R.id.button_getname,
                R.id.button_logsettings,
        };
        for (int i = 0; i < target.length; i++) {
            Button button = (Button) findViewById(target[i]);
            button.setOnClickListener(this);
        }


        //update button state
        updateButtonState();
    }


    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.button_discovery:
                intent = new Intent(this, DiscoverPrinterActivity.class);
                break;
            case R.id.button_open:
                intent = new Intent(this, OpenActivity.class);
                break;
            case R.id.button_close:
                printer = global.getPrinter();
                if (printer != null) {
                    try {
                        printer.closePrinter();
                        printer = null;
                    } catch (Exception e) {
                        printer = null;
                    }
                }
                updateButtonState();
                break;
            case R.id.button_text:
                intent = new Intent(this, TextActivity.class);
                break;

            case R.id.button_getstatus:
                showPrinterStatus();
                break;
            case R.id.button_getname:
                intent = new Intent(this, GetNameActivity.class);
                break;
            case R.id.button_logsettings:
                intent = new Intent(this, LogSettingsActivity.class);
                break;
            default:
                break;
        }
        if (intent != null) {
            //show activity
            intent.putExtra("devtype", global.getConnectionType());
            intent.putExtra("ipaddress", global.getOpenDeviceName());
            intent.putExtra("printername", global.getPrinterName());
            intent.putExtra("language", global.getLanguage());
            startActivityForResult(intent, 0);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            if (resultCode == 1 || resultCode == 2) {
                global.setConnectionType(sharedPreferences.getInt(Constants.PRINTER_CONNECTION_TYPE,0));
                global.setOpenDeviceName(sharedPreferences.getString(Constants.PRINTER_OPEN_DEVICE_NAME, ""));
            }
            if (resultCode == 2) {
                global.setPrinterName(sharedPreferences.getString(Constants.PRINTER_NAME, ""));
               global.setLanguage(sharedPreferences.getInt(Constants.PRINTER_LANGUAGE,0));
            }
        }
        printer = global.getPrinter();
        if (printer != null) {
            printer.setStatusChangeEventCallback(this);
            printer.setBatteryStatusChangeEventCallback(this);
        }
        updateButtonState();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString("openDeviceName", global.getOpenDeviceName());
        outState.putInt("connectionType", global.getConnectionType());
        outState.putInt("language", global.getLanguage());
        outState.putString("printerName", global.getPrinterName());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        global.setOpenDeviceName(savedInstanceState.getString("openDeviceName"));
        global.setConnectionType(savedInstanceState.getInt("connectionType"));
        global.setLanguage(savedInstanceState.getInt("language"));
        global.setPrinterName(savedInstanceState.getString("printerName"));
    }

    //change button satate(Eable/Disable)
    private void updateButtonState() {
        int[] target1 = {
                R.id.button_open,
                R.id.button_getname

        };
        int[] target2 = {
                R.id.button_close,
                R.id.button_getstatus
        };
        int[] enableTarget = null;
        int[] disableTarget = null;
        printer = global.getPrinter();
        if (printer == null) {
            enableTarget = target1;
            disableTarget = target2;
        } else {
            enableTarget = target2;
            disableTarget = target1;
        }
        for (int i = 0; i < enableTarget.length; i++) {
            Button button = (Button) findViewById(enableTarget[i]);
            button.setEnabled(true);
        }
        for (int i = 0; i < disableTarget.length; i++) {
            Button button = (Button) findViewById(disableTarget[i]);
            button.setEnabled(false);
        }
    }

    private void showPrinterStatus() {
        printer = global.getPrinter();
        Builder builder = null;
        String method = "";
        try {
            //create builder
            method = "Builder";
            builder = new Builder(global.getPrinterName(), global.getLanguage(), getApplicationContext());

            //send builder data(empty builder data)
            int[] status = new int[1];
            int[] battery = new int[1];
            try {
                global.getPrinter().sendData(builder, 0, status, battery);
                ShowMsg.showStatus(EposException.SUCCESS, status[0], battery[0], this);
            } catch (EposException e) {
                ShowMsg.showStatus(e.getErrorStatus(), e.getPrinterStatus(), e.getBatteryStatus(), this);
            }
        } catch (Exception e) {
            ShowMsg.showException(e, method, this);
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

    @Override
    public void onStatusChangeEvent(final String deviceName, final int status) {
        runOnUiThread(new Runnable() {
            @Override
            public synchronized void run() {
                ShowMsg.showStatusChangeEvent(deviceName, status, EPOSPrintSampleActivity.this);
            }
        });
    }

    @Override
    public void onBatteryStatusChangeEvent(final String deviceName, final int battery) {
        runOnUiThread(new Runnable() {
            @Override
            public synchronized void run() {
                ShowMsg.showBatteryStatusChangeEvent(deviceName, battery, EPOSPrintSampleActivity.this);
            }
        });
    }
}