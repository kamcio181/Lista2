package com.domowe.apki.lista2;

/**
 * Created by Kamil on 2015-09-19.
 */
public class ListObject {
    private final long mId;
    private final String mName;
    private final String mCreateDate;
    private final String mType;

    public ListObject(long id, String name, String createDate, String type) {
        this.mId = id;
        this.mCreateDate = createDate;
        this.mName = name;
        this.mType = type;
    }

    public long getId() { return mId; }

    public String getCreateDate() {
        return mCreateDate;
    }

    public String getName() {
        return mName;
    }

    public String getType() {
        return mType;
    }

    public String toString(){
        return new StringBuilder().append(mName).append(";").append(mCreateDate).append(";").append(mType).toString();
    }
}
