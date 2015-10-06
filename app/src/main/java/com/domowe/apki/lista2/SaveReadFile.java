package com.domowe.apki.lista2;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * Saving files in one place ;)
 */
public class SaveReadFile {
    private final File file;
    private boolean fileCreated = true;
    public SaveReadFile(File file){
        this.file = file;
        if(!file.exists())
            try {
                fileCreated = file.createNewFile();

            } catch (IOException e) {
                e.printStackTrace();
            }
        Log.v("save file", "start");
    }

    public void writeProviderItems(IndexDataProvider provider){
        if(fileCreated)
            new WriteProviderToFile().execute(provider);
    }

    public void writeProviderItems(AbstractDataProvider provider){
        if(fileCreated)
            new WriteProviderToFile2().execute(provider);
    }

    public void removeItemFromList(int id){
        try {
            ArrayList<String> list = new LoadItems().execute().get();
            list.remove(id);

            if(fileCreated)
                new WriteListToFile().execute(list);
        } catch (InterruptedException | IndexOutOfBoundsException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    public void renameItemInList(int id, String name){
        try {
            ArrayList<String> list = new LoadItems().execute().get();
            String temp = list.get(id);
            list.set(id, name + temp.substring(temp.indexOf(";")));

            if(fileCreated)
                new WriteListToFile().execute(list);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    public void writeNote(String note){
        if(fileCreated)
            new WriteNote().execute(note);
    }

    public void addToFile(String string){
        if(fileCreated)
            new AddToFile().execute(string);
    }

    public void writeListToFile(ArrayList<String> list){
        if(fileCreated)
            new WriteListToFile().execute(list);
    }

    public ArrayList<String> readFile(){
        try {
            return new LoadItems().execute().get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    private class AddToFile extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... strings) {
            Log.v("save file", "background");
            try {
                FileWriter f;
                f = new FileWriter(file,true);
                f.write(strings[0] + "\r\n");
                Log.v("save file", strings[0]);
                Log.v("save file", file.toString());
                f.flush();
                f.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private class WriteNote extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... strings) {
            try {
                FileWriter f;
                f = new FileWriter(file);
                f.write(strings[0]);
                f.flush();
                f.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private class LoadItems extends AsyncTask<Void,Void,ArrayList<String>> {
        @Override
        protected ArrayList<String> doInBackground(Void... voids) {
            ArrayList<String> resultList = new ArrayList<>();
            if(file.exists()){
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        resultList.add(line);
                    }
                    reader.close();
                }
                catch (IOException ex) {
                    throw new RuntimeException("Error in reading file: "+ex);
                }
            }
            return resultList;
        }
    }
    private class WriteListToFile extends AsyncTask<ArrayList, Void, Void>{

        @Override
        protected Void doInBackground(ArrayList... arrayLists) {
            try {
                FileWriter f;
                f = new FileWriter(file);
                int size = arrayLists[0].size();
                for(int i=0;i<size;i++){
                    String line = (String) arrayLists[0].get(i);
                    if(line != null){
                        f.write(line+"\r\n");
                    }
                }
                f.flush();
                f.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private class WriteProviderToFile extends AsyncTask<IndexDataProvider, Void, Void>{

        @Override
        protected Void doInBackground(IndexDataProvider... providers) {
            try {
                FileWriter f;
                f = new FileWriter(file);
                int size = providers[0].getCount();
                for(int i=0;i<size;i++){
                    String line = providers[0].getItem(i).toString();
                    if(line != null){
                        f.write(line+"\r\n");
                    }
                }
                f.flush();
                f.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private class WriteProviderToFile2 extends AsyncTask<AbstractDataProvider, Void, Void>{

        @Override
        protected Void doInBackground(AbstractDataProvider... providers) {
            try {
                FileWriter f;
                f = new FileWriter(file);
                int size = providers[0].getCount();
                for(int i=1;i<size;i++){
                    String line = providers[0].getItem(i).toString();
                    if(line != null){
                        f.write(line+"\r\n");
                    }
                }
                f.flush();
                f.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}