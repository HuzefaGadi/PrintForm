package com.huzefagadi.rashida.printform.printer;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.epson.eposprint.BatteryStatusChangeEventListener;
import com.epson.eposprint.Builder;
import com.epson.eposprint.EposException;
import com.epson.eposprint.Print;
import com.epson.eposprint.StatusChangeEventListener;
import com.huzefagadi.rashida.printform.MainActivity;
import com.huzefagadi.rashida.printform.R;
import com.huzefagadi.rashida.printform.StartActivity;
import com.huzefagadi.rashida.printform.utility.Constants;

public class TextActivity extends Activity implements OnClickListener, StatusChangeEventListener, BatteryStatusChangeEventListener {

    static final int SEND_TIMEOUT = 10 * 1000;
    static final int SIZEWIDTH_MAX = 8;
    static final int SIZEHEIGHT_MAX = 8;
    StartActivity global;

    boolean exitActivity = false;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor edit;
    TextView feedUnit, xPosition, lineSpace;
    ToggleButton toggleForUnderLine, toggleForBoldLine;


    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.text);
        sharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCES, MODE_PRIVATE);
        edit = sharedPreferences.edit();
        //init font list
        Spinner spinner = (Spinner) findViewById(R.id.spinner_font);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter.add(getString(R.string.font_a));
        adapter.add(getString(R.string.font_b));
        adapter.add(getString(R.string.font_c));
        adapter.add(getString(R.string.font_d));
        adapter.add(getString(R.string.font_e));
        spinner.setAdapter(adapter);
        int font = sharedPreferences.getInt(Constants.TEXT_FONT,0);
        spinner.setSelection(font);


        //init align list
        spinner = (Spinner) findViewById(R.id.spinner_align);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter.add(getString(R.string.align_left));
        adapter.add(getString(R.string.align_center));
        adapter.add(getString(R.string.align_right));
        spinner.setAdapter(adapter);
        int align = sharedPreferences.getInt(Constants.TEXT_ALIGN,0);
        spinner.setSelection(align);

        //init language list
        spinner = (Spinner) findViewById(R.id.spinner_language);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter.add(getString(R.string.language_ank));
        adapter.add(getString(R.string.language_japanese));
        adapter.add(getString(R.string.language_simplified_chinese));
        adapter.add(getString(R.string.language_traditional_chinese));
        adapter.add(getString(R.string.language_korean));
        adapter.add(getString(R.string.language_thai));
        adapter.add(getString(R.string.language_vietnamese));
        spinner.setAdapter(adapter);
        int language = sharedPreferences.getInt(Constants.TEXT_LANGUAGE,0);
        spinner.setSelection(language);


        //init size list
        spinner = (Spinner) findViewById(R.id.spinner_size_width);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        for (int i = 1; i <= SIZEWIDTH_MAX; i++) {
            adapter.add(String.format("%d", i));
        }
        spinner.setAdapter(adapter);
        int width = sharedPreferences.getInt(Constants.TEXT_SIZE_WIDTH, 1);
        spinner.setSelection(width - 1);

        spinner = (Spinner) findViewById(R.id.spinner_size_height);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        for (int i = 1; i <= SIZEHEIGHT_MAX; i++) {
            adapter.add(String.format("%d", i));
        }
        spinner.setAdapter(adapter);
        int height = sharedPreferences.getInt(Constants.TEXT_SIZE_HEIGHT, 1);
        spinner.setSelection(height - 1);


        //Registration ClickListener
        Button button = (Button) findViewById(R.id.button_print);
        button.setOnClickListener(this);
        feedUnit = (TextView) findViewById(R.id.editText_feedunit);
        xPosition = (TextView) findViewById(R.id.editText_xposition);
        toggleForUnderLine = (ToggleButton) findViewById(R.id.toggleButton_style_underline);
        toggleForBoldLine = (ToggleButton) findViewById(R.id.toggleButton_style_bold);

        int feed = sharedPreferences.getInt(Constants.TEXT_FEED_UNIT, 0);
        feedUnit.setText(feed + "");
        int xPos = sharedPreferences.getInt(Constants.TEXT_POSITION, 0);
        xPosition.setText(xPos + "");
        int underline = sharedPreferences.getInt(Constants.TEXT_STYLE_UNDERLINE, Builder.FALSE);
        if (underline == Builder.FALSE) {
            toggleForUnderLine.setChecked(false);
        } else {
            toggleForUnderLine.setChecked(true);
        }
        int bold = sharedPreferences.getInt(Constants.TEXT_STYLE_BOLD, Builder.FALSE);
        if (bold == Builder.FALSE) {
            toggleForBoldLine.setChecked(false);
        } else {
            toggleForBoldLine.setChecked(true);
        }
        lineSpace = (TextView) findViewById(R.id.editText_linespace);
        int lspace = sharedPreferences.getInt(Constants.TEXT_LINE_SPACE, 0);
        lineSpace.setText(lspace+"");
        //hide keyboard
        this.getWindow().setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }


    @Override
    public void onClick(View v) {
        saveTextSettings();
        finish();
    }

    private void saveTextSettings() {

        //create builder

        edit.putInt(Constants.TEXT_FONT, getBuilderFont());
        edit.putInt(Constants.TEXT_ALIGN, getBuilderAlign());
        edit.putInt(Constants.TEXT_LINE_SPACE, getBuilderLineSpace());
        edit.putInt(Constants.TEXT_LANGUAGE, getBuilderLanguage());
        edit.putInt(Constants.TEXT_SIZE_WIDTH, getBuilderSizeW());
        edit.putInt(Constants.TEXT_SIZE_HEIGHT, getBuilderSizeH());
        edit.putInt(Constants.TEXT_STYLE_UNDERLINE, getBuilderStyleUnderline());
        edit.putInt(Constants.TEXT_STYLE_BOLD, getBuilderStyleBold());
        edit.putInt(Constants.TEXT_POSITION, getBuilderXPosition());
        edit.putInt(Constants.TEXT_FEED_UNIT, getBuilderFeedUnit());
        edit.commit();

           /* //add command
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
            builder.addText(getBuilderText());

            method = "addFeedUnit";
            builder.addFeedUnit(getBuilderFeedUnit());*/


    }

    private int getBuilderFont() {
        Spinner spinner = (Spinner) findViewById(R.id.spinner_font);
        switch (spinner.getSelectedItemPosition()) {
            case 1:
                return Builder.FONT_B;
            case 2:
                return Builder.FONT_C;
            case 3:
                return Builder.FONT_D;
            case 4:
                return Builder.FONT_E;
            case 0:
            default:
                return Builder.FONT_A;
        }
    }

    private int getBuilderAlign() {
        Spinner spinner = (Spinner) findViewById(R.id.spinner_align);
        switch (spinner.getSelectedItemPosition()) {
            case 1:
                return Builder.ALIGN_CENTER;
            case 2:
                return Builder.ALIGN_RIGHT;
            case 0:
            default:
                return Builder.ALIGN_LEFT;
        }
    }

    private int getBuilderLineSpace() {

        try {
            return Integer.parseInt(lineSpace.getText().toString());
        } catch (Exception e) {
            return 0;
        }
    }

    private int getBuilderLanguage() {
        Spinner spinner = (Spinner) findViewById(R.id.spinner_language);
        switch (spinner.getSelectedItemPosition()) {
            case 1:
                return Builder.LANG_JA;
            case 2:
                return Builder.LANG_ZH_CN;
            case 3:
                return Builder.LANG_ZH_TW;
            case 4:
                return Builder.LANG_KO;
            case 5:
                return Builder.LANG_TH;
            case 6:
                return Builder.LANG_VI;
            case 0:
            default:
                return Builder.LANG_EN;
        }
    }

    private int getBuilderSizeW() {
        Spinner spinner = (Spinner) findViewById(R.id.spinner_size_width);
        return spinner.getSelectedItemPosition() + 1;
    }

    private int getBuilderSizeH() {
        Spinner spinner = (Spinner) findViewById(R.id.spinner_size_height);
        return spinner.getSelectedItemPosition() + 1;
    }

    private int getBuilderStyleBold() {

        if (toggleForBoldLine.isChecked()) {
            return Builder.TRUE;
        } else {
            return Builder.FALSE;
        }
    }

    private int getBuilderStyleUnderline() {

        if (toggleForUnderLine.isChecked()) {
            return Builder.TRUE;
        } else {
            return Builder.FALSE;
        }
    }

    private int getBuilderXPosition() {

        try {
            return Integer.parseInt(xPosition.getText().toString());
        } catch (Exception e) {
            return 0;
        }
    }


    private int getBuilderFeedUnit() {

        try {
            return Integer.parseInt(feedUnit.getText().toString());
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public void onStatusChangeEvent(final String deviceName, final int status) {
        runOnUiThread(new Runnable() {
            @Override
            public synchronized void run() {
                ShowMsg.showStatusChangeEvent(deviceName, status, TextActivity.this);
            }
        });
    }

    @Override
    public void onBatteryStatusChangeEvent(final String deviceName, final int battery) {
        runOnUiThread(new Runnable() {
            @Override
            public synchronized void run() {
                ShowMsg.showBatteryStatusChangeEvent(deviceName, battery, TextActivity.this);
            }
        });
    }
}
