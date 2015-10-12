package com.domowe.apki.lista2;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class ItemsManagementActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{
    private ArrayList<String> articles;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarLayout).findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        articles = Utils.getDefaultList(this);

        listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, articles));
        listView.setOnItemClickListener(this);

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        removeEditDialog(i).show();
    }

    private Dialog removeEditDialog(final int itemId){
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_delete);
        TextView textView = (TextView) dialog.findViewById(R.id.textView);
        Button edit = (Button) dialog.findViewById(R.id.nie);
        Button remove = (Button) dialog.findViewById(R.id.tak);
        textView.setText(R.string.choose_action);
        edit.setText(R.string.edit);
        remove.setText(R.string.delete);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                editItemDialog(itemId).show();
            }
        });
        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                removeItemDialog(itemId).show();
            }
        });
        return dialog;
    }

    private Dialog editItemDialog(final int itemId){
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_change_name);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        TextView textView = (TextView) dialog.findViewById(R.id.textView);
        final EditText editText = (EditText) dialog.findViewById(R.id.name);
        Button cancel = (Button) dialog.findViewById(R.id.anuluj);
        Button confirm = (Button) dialog.findViewById(R.id.zatwierdz);
        textView.setText(R.string.article_edit);
        editText.setHint(R.string.enter_article);
        editText.setText(articles.get(itemId));
        editText.setSelection(articles.get(itemId).length());
        cancel.setText(R.string.cancel);
        confirm.setText(R.string.confirm);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                articles.set(itemId, editText.getText().toString().trim());
                ((BaseAdapter) listView.getAdapter()).notifyDataSetChanged();
                dialog.dismiss();
            }
        });
        return dialog;
    }

    private Dialog removeItemDialog(final int itemId){
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_delete);
        TextView textView = (TextView) dialog.findViewById(R.id.textView);
        Button no = (Button) dialog.findViewById(R.id.nie);
        Button yes = (Button) dialog.findViewById(R.id.tak);
        textView.setText(R.string.delete_item_question);
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                articles.remove(itemId);
                ((BaseAdapter)listView.getAdapter()).notifyDataSetChanged();
                dialog.dismiss();
            }
        });
        return dialog;
    }

    @Override
    protected void onStop() {
        super.onStop();

        new SaveReadFile(Utils.getFileFromName(this, Constants.ARTICLE_LIST_FILE)).writeListToFile(articles);
    }
}
