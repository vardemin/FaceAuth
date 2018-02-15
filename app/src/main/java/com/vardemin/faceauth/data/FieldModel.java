package com.vardemin.faceauth.data;

import io.realm.RealmObject;

public class FieldModel extends RealmObject {
    public String getField() {
        return field;
    }

    public int getInputType() {
        return inputType;
    }

    public String getValue() {
        return value;
    }

    private String field;
    private int inputType;
    private String value;
}
