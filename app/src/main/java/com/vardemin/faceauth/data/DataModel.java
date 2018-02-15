package com.vardemin.faceauth.data;

import com.google.gson.annotations.Expose;

import java.util.Date;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * Created by user on 22.01.18.
 */

public class DataModel extends RealmObject {

    public AppModel getApplication() {
        return application;
    }

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public RealmList<FieldModel> getFields() {
        return fields;
    }

    public Date getDate() {
        return date;
    }

    private AppModel application;

    @PrimaryKey
    @Required
    private String id;

    private String username;

    private RealmList<FieldModel> fields;

    private Date date = new Date();
}
