package com.huzefagadi.rashida.printform;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.huzefagadi.rashida.printform.printer.EPOSPrintSampleActivity;

public class SettingsActivity extends Activity {

    Context context;
    Button addMenu,printerSetting,addItem,addServer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        context= this;
        addMenu = (Button) findViewById(R.id.addMenu);
        addItem = (Button) findViewById(R.id.addItem);
        printerSetting = (Button) findViewById(R.id.printerSettings);
        addServer = (Button) findViewById(R.id.addServer);

        addMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, AddMenuActivity.class);
                startActivity(intent);
            }
        });

        addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, AddItemsActivity.class);
                startActivity(intent);
            }
        });
        printerSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context , EPOSPrintSampleActivity.class);
                startActivity(intent);
            }
        });

        addServer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context , AddServerActivity.class);
                startActivity(intent);

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_settings, menu);
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
