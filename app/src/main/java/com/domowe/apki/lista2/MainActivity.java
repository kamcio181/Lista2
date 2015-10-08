package com.domowe.apki.lista2;

import android.app.Dialog;
import android.content.Intent;
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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class MainActivity extends AppCompatActivity implements IndexFragment.ListListener {
    private static final String FRAGMENT_LIST_VIEW = "list view";

    //private Menu menu;

    //private TextView title, subtitle;
    private Toolbar toolbar;
    private Fragment mainFragment;
    private boolean removeNote;
    private boolean sharedListUpdated = false;

    private String listName;
    private int listId;

    private ArrayList<String> lists;
    private ArrayList<String> articles = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RelativeLayout toolbarLayout = (RelativeLayout) findViewById(R.id.toolbarLayout);
        toolbar = (Toolbar) toolbarLayout.findViewById(R.id.toolbar);
        //title = (TextView) toolbarLayout.findViewById(R.id.title);
        //subtitle = (TextView) toolbarLayout.findViewById(R.id.subtitle);

        setSupportActionBar(toolbar);
        setToolbarClickListener();

        mainFragment = new IndexFragment();

        getSupportFragmentManager().beginTransaction()
                .add(R.id.container, mainFragment, FRAGMENT_LIST_VIEW)
                .commit();

    }

    public void setLists(ArrayList<String> lists) {
        this.lists = lists;
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
            menu.findItem(R.id.action_remove).setVisible(true);
            menu.findItem(R.id.action_delete).setVisible(true);
        }
        else if(currentFragment instanceof NoteFragment){
            menu.findItem(R.id.action_delete).setVisible(true);
        }
        else {
            menu.findItem(R.id.action_add).setVisible(true);
            menu.findItem(R.id.action_remove).setVisible(true);
            menu.findItem(R.id.action_reload).setVisible(true);
            menu.findItem(R.id.action_save).setVisible(true);
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        //this.menu = menu;
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
                    listTypeDialog().show();
                } else if(currentFragment instanceof PrivateListFragment){
                    addItemDialog(true).show();
                }else if(currentFragment instanceof SharedListFragment && sharedListUpdated){
                    addItemDialog(false).show();
                }else if(Utils.isOnline(this)){
                    //TODO pobierz liste we fragmencie
                    //addItemDialog(false).show();
                }else {
                    Toast.makeText(this, "Wlacz dostep do internetu aby uaktualnic wspolna liste", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.action_remove:
                //TODO sprawdzanie czy shared czy private
                removeDialog().show();
                break;
            case R.id.action_delete:
                deleteDialog().show();
                break;
            case R.id.action_reload:
                //TODO sprawdz czy jest dostep do neta i pobierz liste
                break;
            case R.id.action_save:
                //TODO sprawdz czy jest dostep do neta i pobierz liste
                //TODO porownaj nowa liste z data ze starej - jesli daty rozne sprawdz roznice i dodaj jako new i popros o sprawdze nie zmian
                //TODO jezeli nie bylo zmian zapisz liste
                break;
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private Dialog listTypeDialog(){
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

    private Dialog addItemDialog(final boolean isPrivate){
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_add_item);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        final AutoCompleteTextView name = (AutoCompleteTextView) dialog.findViewById(R.id.name);
        final EditText quantity = (EditText) dialog.findViewById(R.id.ilosc);
        final Button cancel = (Button) dialog.findViewById(R.id.anuluj);
        Button add = (Button) dialog.findViewById(R.id.dodaj);
        final ImageView pin = (ImageView) dialog.findViewById(R.id.imageView);
        final Spinner unit = (Spinner) dialog.findViewById(R.id.spinner);
        final String[] units = getResources().getStringArray(R.array.units);

        unit.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, units));
        final boolean[] pinned = new boolean[]{false};

        if(articles.size()==0)
            articles = Utils.getDefaultList(this);

        name.setAdapter(new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item,articles));
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

                    handleItemAdding(name.getText().toString().trim(), Utils.getQuantityValue(quantity) + " " + unit.getSelectedItem(), isPrivate);

                    Log.v(" main", Utils.getQuantityValue(quantity)+" "+unit.getSelectedItem());
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

    private Dialog removeDialog(){
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_delete);
        TextView textView = (TextView) dialog.findViewById(R.id.textView);
        Button no = (Button) dialog.findViewById(R.id.nie);
        Button yes = (Button) dialog.findViewById(R.id.tak);
        textView.setText("Usunąć kupione artykuły?");
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleItemsRemoving();
                dialog.dismiss();
            }
        });
        return dialog;
    }

    private Dialog deleteDialog(){
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_delete);
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

    private void handleItemAdding(String name, String quantity, boolean isPrivate){
        MyDraggableWithSectionItemAdapter adapter;
        if(isPrivate)
            adapter = ((PrivateListFragment) getSupportFragmentManager().findFragmentById(R.id.container)).getMyItemAdapter();
        else
            adapter = ((SharedListFragment) getSupportFragmentManager().findFragmentById(R.id.container)).getMyItemAdapter();

        ListDataProvider provider = (ListDataProvider) adapter.getProvider();
        provider.addItem(Utils.replaceSemiColons(name), quantity, false);
        adapter.notifyItemInserted(1);

        if(!articles.contains(name))
            articles.add(name);
    }

    private void handleItemsRemoving(){
        MyDraggableWithSectionItemAdapter adapter = ((PrivateListFragment)getSupportFragmentManager().findFragmentById(R.id.container)).getMyItemAdapter();
        ListDataProvider provider = (ListDataProvider) adapter.getProvider();
        provider.removeInactiveItems(adapter.getLast() + 2);
        adapter.notifyDataSetChanged();
        //adapter.notifyItemRangeRemoved(adapter.getLast()+2, provider.getCount());
    }

    private void setToolbarClickListener(){
        try {
            Field titleField = Toolbar.class.getDeclaredField("mTitleTextView");
            titleField.setAccessible(true);
            try{
                TextView barTitleView = (TextView) titleField.get(toolbar);
                barTitleView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.container);
                        if(currentFragment instanceof NoteFragment || currentFragment instanceof PrivateListFragment)
                            renameDialog(currentFragment).show();
                    }
                });
            } catch (IllegalAccessException f){
                f.printStackTrace();
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    private Dialog renameDialog(final Fragment currentFragment){
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_change_name);
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
        if(!newName.equals(listName) && Utils.getFileFromName(this, listName).renameTo(Utils.getFileFromName(this,newName))){

            newName = Utils.getUniqueListName(lists, newName);

            setTitle(newName);

            Utils.setFragmentFile(currentFragment, newName);

            listName = newName;

            new SaveReadFile(Utils.getFileFromName(this,Constants.LIST_ITEMS_FILE)).renameItemInList(listId, newName);
        }
        else
            Toast.makeText(this, R.string.failed_to_change_name, Toast.LENGTH_LONG).show();
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

    @Override
    protected void onStart() {
        super.onStart();
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.container);
        getSupportFragmentManager().beginTransaction().detach(currentFragment).attach(currentFragment).commit();

        articles = Utils.getDefaultList(this);
    }

    public void setSharedListUpdated(boolean sharedListUpdated) {
        this.sharedListUpdated = sharedListUpdated;
    }
}
