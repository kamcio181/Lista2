package com.domowe.apki.lista2;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class MainActivity extends AppCompatActivity implements IndexFragment.ListListener {
    private static final String FRAGMENT_LIST_VIEW = "list view";

    private Menu menu;

    private TextView title, subtitle;
    private Toolbar toolbar;
    private RelativeLayout toolbarLayout;
    private Fragment mainFragment;
    private boolean removeNote;

    private String listName;
    private int listId;

    private ArrayList<String> lists;
    private ArrayList<String> articles = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbarLayout = (RelativeLayout) findViewById(R.id.toolbarLayout);
        toolbar = (Toolbar) toolbarLayout.findViewById(R.id.toolbar);
        title = (TextView) toolbarLayout.findViewById(R.id.title);
        subtitle = (TextView) toolbarLayout.findViewById(R.id.subtitle);

        setSupportActionBar(toolbar);
        setToolbarClickListener();

        mainFragment = new IndexFragment();

        getSupportFragmentManager().beginTransaction()
                .add(R.id.container, mainFragment, FRAGMENT_LIST_VIEW)
                .commit();

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        Log.v("start", "menu");
        removeNote = false;
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.container);
        if(currentFragment instanceof IndexFragment){
            menu.findItem(R.id.action_add).setVisible(true);
            setTitle("Twoje notatki:");
        }
        else if(currentFragment instanceof PrivateListFragment){
            menu.findItem(R.id.action_add).setVisible(true);
            menu.findItem(R.id.action_delete).setVisible(true);
        }
        else if(currentFragment instanceof NoteFragment){
            menu.findItem(R.id.action_delete).setVisible(true);
        }
        else {
            menu.findItem(R.id.action_add).setVisible(true);
            menu.findItem(R.id.action_reload).setVisible(true);
            menu.findItem(R.id.action_save).setVisible(true);
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        this.menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.container);
        switch (id){
            case android.R.id.home:
                setTitle("klik");
                break;

            case R.id.action_add:
                if(currentFragment instanceof IndexFragment) {
                    ListTypeDialog().show();
                } else if(currentFragment instanceof PrivateListFragment){
                    AddItemDialog().show();
                }
                break;
            case R.id.action_delete:
                deleteDialog().show();
                break;
            case R.id.action_reload:
                break;
            case R.id.action_save:
                break;
            case R.id.action_settings:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private Dialog ListTypeDialog(){
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_list_type);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        final EditText name = (EditText) dialog.findViewById(R.id.name);
        final Button list = (Button) dialog.findViewById(R.id.button);
        Button note = (Button) dialog.findViewById(R.id.button2);
        list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleListCreating(name.getText().toString().trim(), true);
                dialog.dismiss();
            }
        });
        note.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleListCreating(name.getText().toString().trim(), false);
                dialog.dismiss();
            }
        });
        return dialog;
    }

    private Dialog AddItemDialog(){
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.add_item_dialog2);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        final AutoCompleteTextView name = (AutoCompleteTextView) dialog.findViewById(R.id.name);
        final EditText quantity = (EditText) dialog.findViewById(R.id.ilosc);
        final Button cancel = (Button) dialog.findViewById(R.id.anuluj);
        Button add = (Button) dialog.findViewById(R.id.dodaj);
        final ImageView pin = (ImageView) dialog.findViewById(R.id.imageView);
        final boolean[] pinned = new boolean[]{false};

        if(articles.size()==0)
            articles = Utils.getDefaultList(this);

        name.setAdapter(new ArrayAdapter(MainActivity.this,android.R.layout.simple_spinner_dropdown_item,articles));
        name.setThreshold(1);

        name.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                quantity.requestFocus();
            }
        });

        Utils.showKeyboard(this, name);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "Anulowano", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!Utils.isEmpty(MainActivity.this, name)){
                    Toast.makeText(MainActivity.this, "Dodano", Toast.LENGTH_SHORT).show();
                    handleItemAdding(name.getText().toString().trim(), Utils.getQuantityValue(quantity));
                    name.setText("");
                    name.requestFocus();
                    quantity.setText("1");
                    if(!pinned[0])
                        dialog.dismiss();
                }
            }
        });
        pin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pinned[0] = !pinned[0];
                if(pinned[0])
                    pin.setImageResource(R.drawable.ic_pin_white_24dp);
                else
                    pin.setImageResource(R.drawable.ic_pin_off_white_24dp);
            }
        });
        return dialog;
    }

    private Dialog deleteDialog(){
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.delete_dialog);
        Button no = (Button) dialog.findViewById(R.id.nie);
        Button yes = (Button) dialog.findViewById(R.id.tak);
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeNote = true;
                lists.remove(listId);
                dialog.dismiss();
                onBackPressed();
            }
        });
        return dialog;
    }

    private void handleListCreating(String name, boolean isList){
        if(lists == null)
            lists = ((IndexFragment)mainFragment).getListNames();

        name = Utils.getUniqueListName(lists, name);

        String type = isList ? "lista" : "notatka";

        ((IndexFragment)mainFragment).getProvider().addItem(name, (new SimpleDateFormat("yyyy-MM-dd HH-mm-ss")).format(new Date()), type);
        listName = name;
        listId = 1;

        lists.add(name);

        Fragment fragment = isList ? PrivateListFragment.newInstance(name, listId) : NoteFragment.newInstance(name,listId);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        fragmentTransaction.commit();

        setTitle(name);
        Log.v("start", "start");
    }

    private void handleItemAdding(String name, String quantity){
        MyDraggableWithSectionItemAdapter adapter = ((PrivateListFragment)getSupportFragmentManager().findFragmentById(R.id.container)).getMyItemAdapter();
        ListDataProvider provider = (ListDataProvider) adapter.getProvider();
        provider.addItem(Utils.replaceSemiColons(name), quantity);
        adapter.notifyItemInserted(1);

        if(!articles.contains(name))
            articles.add(name);
    }

    private void setToolbarClickListener(){
        try {
            Field titleField = Toolbar.class.getDeclaredField("mTitleTextView");
            titleField.setAccessible(true);
            TextView barTitleView = (TextView) titleField.get(toolbar);
            barTitleView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.container);
                    if(currentFragment instanceof NoteFragment || currentFragment instanceof PrivateListFragment)
                        renameDialog(currentFragment).show();
                }
            });
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private Dialog renameDialog(final Fragment currentFragment){
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.change_name_dialog);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        Button cancel = (Button) dialog.findViewById(R.id.anuluj);
        Button confirm = (Button) dialog.findViewById(R.id.zatwierdz);
        final EditText newName = (EditText) dialog.findViewById(R.id.name);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "Anulowano", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!Utils.isEmpty(MainActivity.this, newName)) {
                    handleNameChange(currentFragment, newName.getText().toString().trim());
                    dialog.dismiss();
                }
            }
        });
        return dialog;
    }

    private void handleNameChange(Fragment currentFragment ,String newName){
        if(!newName.equals(listName)){
            File file = Utils.getFileFromName(this, listName);

            newName = Utils.getUniqueListName(lists, newName);

            file.renameTo(Utils.getFileFromName(this,newName));

            setTitle(newName);

            Utils.setFragmentFile(currentFragment, newName);

            listName = newName;
            //zmiana w pliku
            new SaveReadFile(Utils.getFileFromName(this,Constants.LIST_ITEMS_FILE)).renameItemInList(listId, newName);
        }
    }

    @Override
    public void itemClicked(String name, String date, String type, int id) {
        setTitle(name);

        listName = name;
        listId = id;

        if(date.equals("")){
            Toast.makeText(this, "Jeszcze nie gotowe", Toast.LENGTH_SHORT).show();
        }
        else {//TODO wspolna lista
            Fragment fragment = new Fragment();
            if (type.equals("notatka")) {
                fragment = NoteFragment.newInstance(name, id);
            } else if (type.equals("lista")) {
                fragment = PrivateListFragment.newInstance(name, id);
            }
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.container, fragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            fragmentTransaction.commit();
        }
    }

    @Override
    public void onBackPressed() {
        Log.v("start", "back");
        super.onBackPressed();
        invalidateOptionsMenu();
    }

    public boolean isRemoveNote() {
        return removeNote;
    }

    @Override
    protected void onStop() {
        super.onStop();

        new SaveReadFile(Utils.getFileFromName(this, Constants.ARTICLE_LIST_FILE)).writeListToFile(articles);
    }
}
