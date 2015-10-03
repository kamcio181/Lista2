/*
 *    Copyright (C) 2015 Haruki Hasegawa
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.domowe.apki.lista2;

import android.content.Context;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

public class Utils {
    public static boolean isEmpty(Context context, EditText editText){
        if(editText.getText().toString().trim().equals("")){
            Toast.makeText(context, "Pole jest puste!",Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }
    public static String getQuantityValue(EditText editText){
        if(editText.getText().toString().trim().equals(""))
            return "1";
        return editText.getText().toString().trim();
    }
    public static String getUniqueListName(ArrayList<String> listsNames, String name){
        if(name.equals(""))
            name = "Bez nazwy";
        if(listsNames!=null && listsNames.contains(name)){
            int i = 1;
            String firstName = name;
            while(listsNames.contains(name)){
                name = firstName + " " + i;
                i++;
            }
        }

        return replaceSemiColons(name);
    }
    public static void setFragmentFile(Fragment fragment, String name){
        if(fragment instanceof NoteFragment)
            ((NoteFragment)fragment).setFile(getFileFromName(fragment.getActivity(),name));
        if(fragment instanceof PrivateListFragment)
            ((PrivateListFragment)fragment).setFile(getFileFromName(fragment.getActivity(),name));
    }
    public static File getFileFromName(Context context, String name){
        return new File(Environment.getExternalStorageDirectory(),name);
        //return new File(context.getFilesDir(),name);
    }
    public static void showKeyboard(Context context, EditText editText){
        editText.requestFocus();
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
    }
    public static String replaceSemiColons(String name){
        if (name.contains(";"))
            return name.replace(";",",");
        return name;
    }
    public static ArrayList<String> getDefaultList(Context context){
        ArrayList<String> defaultList = new ArrayList<>();
        if(getFileFromName(context, Constants.ARTICLE_LIST_FILE).exists())
            defaultList = new SaveReadFile(getFileFromName(context, Constants.ARTICLE_LIST_FILE)).readFile();
        if(defaultList.size()==0) {
            defaultList.add("Chleb");
            defaultList.add("Bułki");
            defaultList.add("Sos spaghetti");
            defaultList.add("Sos boloński");
            defaultList.add("Sos pieczeniowy");
            defaultList.add("Sos koperkowy");
            defaultList.add("Makaron");
            defaultList.add("Ryż");
            defaultList.add("Ketchup");
            defaultList.add("Majonez");
            defaultList.add("Surówka");
            defaultList.add("Jogurt owocowy");
            defaultList.add("Jogurt naturalny");
            defaultList.add("Śmietana");
            defaultList.add("Ser żółty");
            defaultList.add("Ser biały");
            defaultList.add("Twarożek");
            defaultList.add("Ser feta");
            defaultList.add("Wędlina");
            defaultList.add("Kiełbasa");
            defaultList.add("Frytki");
            defaultList.add("Zapiekanki");
            defaultList.add("Bułka czosnkowa");
            defaultList.add("Pizza");
            defaultList.add("Paluszki rybne");
            defaultList.add("Szpinak");
            defaultList.add("Czosnek");
            defaultList.add("Cebula");
            defaultList.add("Papryka");
            defaultList.add("Ziemniaki");
            defaultList.add("Ogórek zielony");
            defaultList.add("Pomidor");
            defaultList.add("Sałata lodowa");
            defaultList.add("Sałata masłowa");
            defaultList.add("Kapusta pekińska");
            defaultList.add("Kapusta kiszona");
            defaultList.add("Ogórek konserwowy");
            defaultList.add("Ogórek kiszony");
            defaultList.add("Lody");
            defaultList.add("Jabłka");
            defaultList.add("Mandarynki");
            defaultList.add("Pomarańcze");
            defaultList.add("Brzoskwinie");
            defaultList.add("Nektarynki");
            defaultList.add("Banany");
            defaultList.add("Mleko");
        }
        return defaultList;
    }
}
