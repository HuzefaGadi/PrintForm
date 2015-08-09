package com.huzefagadi.rashida.printform.printer;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.epson.eposprint.BatteryStatusChangeEventListener;
import com.epson.eposprint.Builder;
import com.epson.eposprint.Print;
import com.epson.eposprint.StatusChangeEventListener;
import com.huzefagadi.rashida.printform.MainActivity;
import com.huzefagadi.rashida.printform.R;
import com.huzefagadi.rashida.printform.StartActivity;
import com.huzefagadi.rashida.printform.utility.Constants;

public class OpenActivity extends Activity implements OnClickListener, StatusChangeEventListener, BatteryStatusChangeEventListener {

    /**
     * Called when the activity is first created.
     */
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor edit;
    StartActivity global;
    ToggleButton toggleStatusMonitor ;
    RadioGroup radioGroup;
    RadioButton epsonPrinter,wifiPrinter;
    EditText lineSpacing;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.open);
        global= (StartActivity)getApplicationContext();
        sharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCES, MODE_PRIVATE);
        edit = sharedPreferences.edit();
        lineSpacing = (EditText)findViewById(R.id.linespacing);
        toggleStatusMonitor = (ToggleButton) findViewById(R.id.toggleButton_statusmonitor);
        int lineSpacingNumber = sharedPreferences.getInt("LINE_SPACING_WIFI_PRINTER",8);

        lineSpacing.setText(lineSpacingNumber+"");


        int enabled = sharedPreferences.getInt(Constants.PRINTER_ENABLED,0);

        if(enabled == 0)
        {
            toggleStatusMonitor.setChecked(false);
        }
        else
        {
            toggleStatusMonitor.setChecked(true);
        }
        //init printer list
        Spinner spinner = (Spinner) findViewById(R.id.spinner_printer);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter.add(getString(R.string.printername_p20));
        adapter.add(getString(R.string.printername_p60));
        adapter.add(getString(R.string.printername_p60ii));
        adapter.add(getString(R.string.printername_p80));
        adapter.add(getString(R.string.printername_t20));
        adapter.add(getString(R.string.printername_t20ii));
        adapter.add(getString(R.string.printername_t70));
        adapter.add(getString(R.string.printername_t70ii));
        adapter.add(getString(R.string.printername_t81ii));
        adapter.add(getString(R.string.printername_t82));
        adapter.add(getString(R.string.printername_t82ii));
        adapter.add(getString(R.string.printername_t83ii));
        adapter.add(getString(R.string.printername_t88v));
        adapter.add(getString(R.string.printername_t90ii));
        adapter.add(getString(R.string.printername_u220));
        adapter.add(getString(R.string.printername_u330));
        spinner.setAdapter(adapter);

        String printerName = sharedPreferences.getString(Constants.PRINTER_NAME,"");
        for(int i=0;i<spinner.getChildCount();i++)
        {
            if(printerName.equals(spinner.getItemAtPosition(i)))
            {
                spinner.setSelection(i);
                break;
            }
        }

        //init language list
        spinner = (Spinner) findViewById(R.id.spinner_language);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter.add(getString(R.string.model_ank));
        adapter.add(getString(R.string.model_japanese));
        adapter.add(getString(R.string.model_chinese));
        adapter.add(getString(R.string.model_taiwan));
        adapter.add(getString(R.string.model_korean));
        adapter.add(getString(R.string.model_thai));
        adapter.add(getString(R.string.model_southasia));
        spinner.setAdapter(adapter);

        int language = sharedPreferences.getInt(Constants.PRINTER_LANGUAGE, 0);
        spinner.setSelection(language);

        //select default radiobutton
        RadioGroup radio = (RadioGroup) findViewById(R.id.radiogroup_devtype);

        switch (sharedPreferences.getInt(Constants.PRINTER_CONNECTION_TYPE,0)) {
            case Print.DEVTYPE_TCP:
                radio.check(R.id.radioButton_tcp);
                break;
            case Print.DEVTYPE_BLUETOOTH:
                radio.check(R.id.radioButton_bluetooth);
                break;
            case Print.DEVTYPE_USB:
                radio.check(R.id.radioButton_usb);
                break;
            default:
                radio.check(R.id.radioButton_tcp);
                break;
        }

        //init edit
        TextView textIp = (TextView) findViewById(R.id.editText_ip);

        TextView textInterval = (TextView) findViewById(R.id.editText_interval);
        textInterval.setText("1000");

        String ipAddress = sharedPreferences.getString(Constants.PRINTER_OPEN_DEVICE_NAME,"");
        textIp.setText(ipAddress);

        int interval = sharedPreferences.getInt(Constants.PRINTER_INTERVAL,1000);
        textInterval.setText(interval+"");

        //Registration ClickListener
        Button button = (Button) findViewById(R.id.button_save);
        button.setOnClickListener(this);
        int checkedId = sharedPreferences.getInt("SELECTED_PRINTER",1);
        radioGroup = (RadioGroup) findViewById(R.id.printerSelect);
        epsonPrinter = (RadioButton)findViewById(R.id.epsonPrinter);
        wifiPrinter = (RadioButton)findViewById(R.id.wifiPrinter);
        if(checkedId == 1)
        {
            radioGroup.check(R.id.epsonPrinter);
        }
        else
        {
            radioGroup.check(R.id.wifiPrinter);
        }

        //hide keyboard
        this.getWindow().setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    @Override
    public void onClick(View v) {
        savePrinter();
    }

    private void savePrinter() {
        //get open parameter

        int checkedId = radioGroup.getCheckedRadioButtonId();
        if (checkedId == R.id.epsonPrinter) {
            edit.putInt("SELECTED_PRINTER", 1);
        } else {
            edit.putInt("SELECTED_PRINTER", 2);
        }
        edit.putInt("LINE_SPACING_WIFI_PRINTER",Integer.parseInt(lineSpacing.getText().toString()));



        TextView textIp = (TextView) findViewById(R.id.editText_ip);
        if (textIp.getText().toString().isEmpty()) {
            ShowMsg.showError(R.string.errmsg_noaddress, this);
            return;
        }
        edit.putString(Constants.PRINTER_OPEN_DEVICE_NAME,textIp.getText().toString());
        int deviceType = Print.DEVTYPE_TCP;
        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radiogroup_devtype);
        switch (radioGroup.getCheckedRadioButtonId()) {
            case R.id.radioButton_tcp:
                deviceType = Print.DEVTYPE_TCP;
                break;
            case R.id.radioButton_bluetooth:
                deviceType = Print.DEVTYPE_BLUETOOTH;
                break;
            case R.id.radioButton_usb:
                deviceType = Print.DEVTYPE_USB;
                break;
            default:
                deviceType = Print.DEVTYPE_TCP;
                break;
        }
        edit.putInt(Constants.PRINTER_CONNECTION_TYPE, deviceType);

        int enabled=0;

        if (toggleStatusMonitor.isChecked()) {
            enabled = Print.TRUE;
        } else {
            enabled = Print.FALSE;
        }
        edit.putInt(Constants.PRINTER_ENABLED, enabled);


        int interval = 0;
        TextView textInterval = (TextView) findViewById(R.id.editText_interval);
        if (!(textInterval.getText().toString().isEmpty())) {
            interval = Integer.parseInt(textInterval.getText().toString());
        }
        edit.putInt(Constants.PRINTER_INTERVAL, interval);
        Spinner spinner = (Spinner) findViewById(R.id.spinner_printer);
        edit.putString(Constants.PRINTER_NAME, (String) spinner.getSelectedItem());


        //open
        /*Print printer = new Print(getApplicationContext());

        if (printer != null) {
            printer.setStatusChangeEventCallback(this);
            printer.setBatteryStatusChangeEventCallback(this);
        }



        try {
            printer.openPrinter(deviceType, textIp.getText().toString(), enabled, interval);
        } catch (Exception e) {
            printer = null;
            ShowMsg.showException(e, "openPrinter", this);
            return;
        }*/

        //return settings
        Intent intent = new Intent();
        intent.putExtra("devtype", deviceType);
        intent.putExtra("ipaddress", textIp.getText().toString());
        intent.putExtra("printername", (String) spinner.getSelectedItem());

        spinner = (Spinner) findViewById(R.id.spinner_language);
        switch (spinner.getSelectedItemPosition()) {
            case 1:
                intent.putExtra("language", Builder.MODEL_JAPANESE);
                break;
            case 2:
                intent.putExtra("language", Builder.MODEL_CHINESE);
                break;
            case 3:
                intent.putExtra("language", Builder.MODEL_TAIWAN);
                break;
            case 4:
                intent.putExtra("language", Builder.MODEL_KOREAN);
                break;
            case 5:
                intent.putExtra("language", Builder.MODEL_THAI);
                break;
            case 6:
                intent.putExtra("language", Builder.MODEL_SOUTHASIA);
                break;
            case 0:
            default:
                intent.putExtra("language", Builder.MODEL_ANK);
                break;
        }
        edit.putInt(Constants.PRINTER_LANGUAGE,spinner.getSelectedItemPosition());
        edit.commit();

        //return main activity
        //global.setPrinter(printer);
        setResult(2, intent);
        finish();
    }

    @Override
    public void onStatusChangeEvent(final String deviceName, final int status) {
        ;
    }

    @Override
    public void onBatteryStatusChangeEvent(final String deviceName, final int battery) {
        ;
    }
}

