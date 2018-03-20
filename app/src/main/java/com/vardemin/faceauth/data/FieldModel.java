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

    public FieldModel() {

    }

    /*@Override
    public boolean equals(Object obj) {
        if (obj instanceof FieldModel) {
            FieldModel model = (FieldModel) obj;
            return model.field.equals(field) && model.inputType==inputType;
        }
        else return false;
    }*/

    public FieldModel(String field, int inputType) {
        this.field = field;
        this.inputType = inputType;
    }


    public void setValue(String value) {
        this.value = value;
    }
}
