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

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class IndexDataProvider {
    private List<ListObject> mData;
    private final ArrayList<String> listNames = new ArrayList<>();
    public IndexDataProvider(File file) {

        mData = new LinkedList<>();

        try {
            mData = new LoadListItems().execute(file).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<String> getListNames() { return listNames; }


    public void addItem(String name, String date, String type) {
        mData.add(1,new ListObject(mData.size(), name, date, type));
    }

    public int getCount() {
        return mData.size();
    }

    public ListObject getItem(int index) {
        if (index < 0 || index >= getCount()) {
            throw new IndexOutOfBoundsException("index = " + index);
        }
        return mData.get(index);
    }

    public void moveItem(int fromPosition, int toPosition) {
        if (fromPosition == toPosition) {
            return;
        }

        final ListObject item = mData.remove(fromPosition);
        mData.add(toPosition, item);
    }

    private class LoadListItems extends AsyncTask<File,Void,List<ListObject>> {
        @Override
        protected List<ListObject> doInBackground(File... files) {
            List<ListObject> resultList = new LinkedList<>();
            if(files[0].exists()){
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(files[0])));
                    String line;
                    String[] fields;
                    int i = 0;
                    while ((line = reader.readLine()) != null) {
                        fields = line.split(";");
                        resultList.add(new ListObject(i, fields[0],fields[1],fields[2]));
                        Log.v("lists provider", line);
                        listNames.add(fields[0]);
                        Log.v("name", fields[0]);
                        i++;
                    }
                    reader.close();
                }
                catch (IOException ex) {
                    throw new RuntimeException("Error in reading file: "+ex);
                }
            }
            else{
                new SaveReadFile(files[0]).addToFile("Wspólna;;lista");
                resultList.add(new ListObject(0,"Wspólna","","lista"));
            }
            return resultList;
        }


    }
}
