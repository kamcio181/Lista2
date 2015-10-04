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
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class ListDataProvider extends AbstractDataProvider {
    private List<ConcreteData> mData;
    private ConcreteData mLastRemovedData;
    private int mLastRemovedPosition = -1;

    public ListDataProvider(File file) {

        mData = new LinkedList<>();

        try {
            mData = new LoadArticles().execute(file).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Data getItem(int index) {
        if (index < 0 || index >= getCount()) {
            throw new IndexOutOfBoundsException("index = " + index);
        }

        return mData.get(index);
    }

    @Override
    public int undoLastRemoval() {
        if (mLastRemovedData != null) {
            int insertedPosition;
            if (mLastRemovedPosition >= 0 && mLastRemovedPosition < mData.size()) {
                insertedPosition = mLastRemovedPosition;
            } else {
                insertedPosition = mData.size();
            }

            mData.add(insertedPosition, mLastRemovedData);

            mLastRemovedData = null;
            mLastRemovedPosition = -1;

            return insertedPosition;
        } else {
            return -1;
        }
    }

    @Override
    public void addItem(String name, String quantity) {
        mData.add(1,new ConcreteData(mData.size(), false, Constants.ITEM_VIEW_TYPE_SECTION_ITEM_ACTIVE, name, quantity, false));
    }

    @Override
    public void moveItem(int fromPosition, int toPosition) {
        if (fromPosition == toPosition) {
            return;
        }

        final ConcreteData item = mData.remove(fromPosition);

        mData.add(toPosition, item);
        mLastRemovedPosition = -1;
    }

    @Override
    public void removeItem(int position) {
        //noinspection UnnecessaryLocalVariable
        final ConcreteData removedItem = mData.remove(position);

        mLastRemovedData = removedItem;
        mLastRemovedPosition = position;
    }

    public void removeInactiveItems(int last) {
        int mDataSize = mData.size();
        for (int i = last; i<mDataSize; i++ ){
            mData.remove(last);
            Log.v("provider", "usunięto");
        }

    }

    private class LoadArticles extends AsyncTask<File,Void,List<ConcreteData>> {
        @Override
        protected List<ConcreteData> doInBackground(File... files) {
            List<ConcreteData> resultList = new LinkedList<>();
            int i = 1;
            resultList.add(new ConcreteData(0,true,Constants.ITEM_VIEW_TYPE_SECTION_HEADER,"Do kupienia","",false));
            if(files[0].exists()){
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(files[0])));
                    String line;
                    String[] fields;

                    int mode = Constants.ITEM_VIEW_TYPE_SECTION_ITEM_ACTIVE;

                    while ((line = reader.readLine()) != null) {
                        fields = line.split(";");
                        if(fields.length>1) {
                            resultList.add(new ConcreteData(i, false, mode, fields[0], fields[1], false));
                        }
                        else {
                            resultList.add(new ConcreteData(i, true, Constants.ITEM_VIEW_TYPE_SECTION_HEADER, "Kupione", "", false));
                            mode = Constants.ITEM_VIEW_TYPE_SECTION_ITEM_INACTIVE;
                        }
                        i++;
                    }
                    reader.close();
                    }
                catch (IOException ex) {
                    throw new RuntimeException("Error in reading file: "+ex);
                }
            }else
                resultList.add(new ConcreteData(i, true, Constants.ITEM_VIEW_TYPE_SECTION_HEADER, "Kupione", "", false));

            return resultList;
        }
    }

    public static final class ConcreteData extends Data {

        private final long mId;
        private final boolean mIsSectionHeader;
        private final String mText;
        private int mViewType;
        private final String mQuantity;
        private boolean mNewItem;

        ConcreteData(long id, boolean isSectionHeader, int viewType, String text, String quantity, boolean newItem) {
            mId = id;
            mIsSectionHeader = isSectionHeader;
            mViewType = viewType;
            mText = text;
            mQuantity = quantity;
            mNewItem = newItem;
        }

        @Override
        public boolean isSectionHeader() {
            return mIsSectionHeader;
        }

        @Override
        public int getViewType() {
            return mViewType;
        }

        @Override
        public void changeViewType() {

            mViewType = mViewType == Constants.ITEM_VIEW_TYPE_SECTION_ITEM_ACTIVE ? Constants.ITEM_VIEW_TYPE_SECTION_ITEM_INACTIVE : Constants.ITEM_VIEW_TYPE_SECTION_ITEM_ACTIVE;

        }

        @Override
        public boolean isNewItem() {
            return mNewItem;
        }

        @Override
        public void setNewItem(boolean mNewItem) {
            this.mNewItem = mNewItem;
        }

        @Override
        public long getId() {
            return mId;
        }

        @Override
        public String toString() {
            return new StringBuilder().append(mText).append(";").append(mQuantity).toString();
        }

        @Override
        public String getText() {
            return mText;
        }

        @Override
        public String getQuantity() {
            return mQuantity;
        }

    }
}
