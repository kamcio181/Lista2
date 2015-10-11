package com.domowe.apki.lista2;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

public class SettingsActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, View.OnClickListener, NumberPicker.OnValueChangeListener{
    private Dialog dialog;
    private SharedPreferences preferences;
    private NumberPicker numberPicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbarLayout).findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        ListView listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.settings)));
        listView.setOnItemClickListener(this);

        preferences = getSharedPreferences(Constants.PREFERENCES_NAME, MODE_PRIVATE);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        switch (i){
            case 0:
                dialog = tileSizeDialog();
                break;

            case 1:
                dialog = noteTextSizeDialog();
                break;

            case 2:
                dialog = null;
                startActivity(new Intent(this, ItemsManagementActivity.class));
                break;

            default:
                break;
        }
        if(dialog!=null)
            dialog.show();
    }
    private Dialog tileSizeDialog(){
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_tile_size);
        Button small = (Button) dialog.findViewById(R.id.button3);
        Button medium = (Button) dialog.findViewById(R.id.button4);
        Button big = (Button) dialog.findViewById(R.id.button5);
        small.setOnClickListener(this);
        medium.setOnClickListener(this);
        big.setOnClickListener(this);
        return dialog;
    }

    private Dialog noteTextSizeDialog(){
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_text_size);
        Button cancel = (Button) dialog.findViewById(R.id.cancel);
        Button confirm = (Button) dialog.findViewById(R.id.confirm);
        TextView textView = (TextView) dialog.findViewById(R.id.textView3);
        textView.setTextSize(preferences.getInt(Constants.NOTE_TEXT_SIZE_KEY,18));
        numberPicker = (NumberPicker) dialog.findViewById(R.id.numberPicker);
        numberPicker.setMinValue(1);
        numberPicker.setMaxValue(40);
        numberPicker.setValue(preferences.getInt(Constants.NOTE_TEXT_SIZE_KEY, 18));
        numberPicker.setOnValueChangedListener(this);
        cancel.setOnClickListener(this);
        confirm.setOnClickListener(this);
        return dialog;
    }


    @Override
    public void onClick(View view) {
        dialog.dismiss();
        switch (view.getId()){
            case R.id.button3:
                preferences.edit().putInt(Constants.TILE_SIZE_KEY, Constants.TILE_SMALL).apply();
                break;
            case R.id.button4:
                preferences.edit().putInt(Constants.TILE_SIZE_KEY, Constants.TILE_MEDIUM).apply();
                break;
            case R.id.button5:
                preferences.edit().putInt(Constants.TILE_SIZE_KEY, Constants.TILE_BIG).apply();
                break;
            case R.id.cancel:
                Toast.makeText(this, R.string.cancelled,Toast.LENGTH_SHORT).show();
                break;
            case R.id.confirm:
                preferences.edit().putInt(Constants.NOTE_TEXT_SIZE_KEY, numberPicker.getValue()).apply();
                break;
            default:
                break;
        }
    }

    @Override
    public void onValueChange(NumberPicker numberPicker, int i, int i1) {
        TextView textView = (TextView) dialog.findViewById(R.id.textView3);
        textView.setTextSize(i1);
    }
}
