package com.vardemin.faceauth.mvp.model;

import com.vardemin.faceauth.data.DataModel;

import java.util.List;

import io.realm.RealmModel;
import io.realm.RealmObject;

public interface ILocalRepository {
    void save(RealmObject object);
    void save(List<RealmObject> objects);
    void delete(RealmObject object);
    void delete(List<RealmObject> objects);


    <E extends RealmModel> E findObjectById (Class<E> _type, String id);
}
