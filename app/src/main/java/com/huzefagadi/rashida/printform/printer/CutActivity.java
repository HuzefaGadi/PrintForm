package com.huzefagadi.rashida.printform.printer;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.epson.eposprint.BatteryStatusChangeEventListener;
import com.epson.eposprint.Builder;
import com.epson.eposprint.EposException;
import com.epson.eposprint.Print;
import com.epson.eposprint.StatusChangeEventListener;
import com.huzefagadi.rashida.printform.MainActivity;
import com.huzefagadi.rashida.printform.R;
import com.huzefagadi.rashida.printform.StartActivity;
import com.huzefagadi.rashida.printform.utility.Constants;

public class CutActivity extends Activity implements OnClickListener, StatusChangeEventListener, BatteryStatusChangeEventListener {

    static final int SEND_TIMEOUT = 10 * 1000;

    boolean exitActivity = false;
    StartActivity global;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor edit;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cut);
        global = (StartActivity)getApplicationContext();

        sharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCES,MODE_PRIVATE);
        edit = sharedPreferences.edit();
        //check close port
        Print printer = global.getPrinter();
        if(printer == null){
            finish();
            return ;
        }
        else {
            printer.setStatusChangeEventCallback(this);
            printer.setBatteryStatusChangeEventCallback(this);
        }
    
        //init type list
        Spinner spinner = (Spinner)findViewById(R.id.spinner_type);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter.add(getString(R.string.cuttype_nofeed));
        adapter.add(getString(R.string.cuttype_feed));
        spinner.setAdapter(adapter);
        spinner.setSelection(1);

        //Registration ClickListener
        Button button = (Button)findViewById(R.id.button_print);
        button.setOnClickListener(this);
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        if(!exitActivity){
            global.closePrinter();
        }
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            setResult(0, null);
            exitActivity = true;
        }
        
        return super.onKeyDown(keyCode, event);
    }
    
    @Override
    public void onClick(View v) {
        cut();
    }
    
    private void cut(){
        Builder builder = null;
        String method = "";
        try{
            //create builder
            Intent intent = getIntent();
            method = "Builder";
            builder = new Builder(
                    intent.getStringExtra("printername"), intent.getIntExtra("language", 0), getApplicationContext());

            //add command
            method = "addCut";
            builder.addCut(getBuilderType());

            //send builder data
            int[] status = new int[1];
            int[] battery = new int[1];
            try{
                Print printer = global.getPrinter();
                printer.sendData(builder, SEND_TIMEOUT, status, battery);
                ShowMsg.showStatus(EposException.SUCCESS, status[0], battery[0], this);
            }catch(EposException e){
                ShowMsg.showStatus(e.getErrorStatus(), e.getPrinterStatus(), e.getBatteryStatus(), this);
            }
        }catch(Exception e){
            ShowMsg.showException(e, method, this);
        }
        
        //remove builder
        if(builder != null){
            try{
                builder.clearCommandBuffer();
                builder = null;
            }catch(Exception e){
                builder = null;
            }
        }
    }
    
    private int getBuilderType() {
        Spinner spinner = (Spinner)findViewById(R.id.spinner_type);
        switch(spinner.getSelectedItemPosition()){
        case 1:
            return Builder.CUT_FEED;
        case 0:
        default:
            return Builder.CUT_NO_FEED;
        }
    }
    
	@Override
	public void onStatusChangeEvent(final String deviceName, final int status) {
		runOnUiThread(new Runnable() {
			@Override
			public synchronized void run() {
				ShowMsg.showStatusChangeEvent(deviceName, status, CutActivity.this);
			}
		});
	}

	@Override
	public void onBatteryStatusChangeEvent(final String deviceName, final int battery) {
		runOnUiThread(new Runnable() {
			@Override
			public synchronized void run() {
				ShowMsg.showBatteryStatusChangeEvent(deviceName, battery, CutActivity.this);
			}
		});
	}
}
