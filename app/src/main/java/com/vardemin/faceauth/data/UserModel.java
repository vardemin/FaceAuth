package com.vardemin.faceauth.data;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class UserModel extends RealmObject {

    @PrimaryKey
    @Required
    private String name;

    public String getName() {
        return name;
    }

    public RealmList<String> getSources() {
        return sources;
    }

    private RealmList<String> sources = new RealmList<>();

    public void addSource(String src) {
        this.sources.add(src);
    }

}
