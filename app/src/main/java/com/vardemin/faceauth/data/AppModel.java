package com.vardemin.faceauth.data;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class AppModel extends RealmObject {

    @PrimaryKey
    @Required
    private String application;

}
