package com.vardemin.faceauth.mvp.model;

import android.support.annotation.UiThread;

import java.util.List;

import io.realm.RealmObject;

public interface ILocalRepository {
    void save(RealmObject object);
    void save(List<RealmObject> objects);
    void delete(RealmObject object);
    void delete(List<RealmObject> objects);
}
