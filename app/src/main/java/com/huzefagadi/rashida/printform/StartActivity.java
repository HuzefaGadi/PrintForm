package com.huzefagadi.rashida.printform;

import android.app.Application;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.epson.eposprint.BatteryStatusChangeEventListener;
import com.epson.eposprint.Print;
import com.epson.eposprint.StatusChangeEventListener;
import com.huzefagadi.rashida.printform.printer.ShowMsg;
import com.huzefagadi.rashida.printform.utility.Constants;


public class StartActivity extends Application  {

    static Print printer = null;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor edit;

    public static void setPrinter(Print obj){
        printer = obj;
    }

    public static Print getPrinter(){
        return printer;
    }

    public static void closePrinter(){
        try{
            printer.closePrinter();
            printer = null;
        }catch(Exception e){
            printer = null;
        }
    }

    private String openDeviceName;
    private int connectionType;
    private int language;
    private  String printerName;

    @Override
    public void onCreate() {

        sharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCES, MODE_PRIVATE);
        edit=sharedPreferences.edit();



        openDeviceName = sharedPreferences.getString(Constants.PRINTER_OPEN_DEVICE_NAME, "192.168.192.168");
        connectionType = sharedPreferences.getInt(Constants.PRINTER_CONNECTION_TYPE, Print.DEVTYPE_TCP);
        printerName = sharedPreferences.getString(Constants.PRINTER_NAME, "TM-T88V");
        language = sharedPreferences.getInt(Constants.PRINTER_LANGUAGE, 0);




        super.onCreate();

    }

    public String getOpenDeviceName() {
        return openDeviceName;
    }

    public void setOpenDeviceName(String openDeviceName) {
        this.openDeviceName = openDeviceName;
        edit.putString(Constants.PRINTER_OPEN_DEVICE_NAME,openDeviceName);
        edit.commit();
    }

    public int getConnectionType() {
        return connectionType;
    }

    public void setConnectionType(int connectionType) {
        this.connectionType = connectionType;
        edit.putInt(Constants.PRINTER_CONNECTION_TYPE, connectionType);
        edit.commit();

    }

    public int getLanguage() {
        return language;
    }

    public void setLanguage(int language) {
        this.language = language;
        edit.putInt(Constants.PRINTER_LANGUAGE, language);
        edit.commit();
    }

    public String getPrinterName() {
        return printerName;
    }

    public void setPrinterName(String printerName) {
        this.printerName = printerName;
        edit.putString(Constants.PRINTER_NAME, printerName);
        edit.commit();
    }


}
