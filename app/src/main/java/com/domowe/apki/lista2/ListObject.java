package com.domowe.apki.lista2;

/**
 * List item - name, create date, type
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
        return mName + ";" + mCreateDate + ";" + mType;
    }
}
