package com.vardemin.faceauth.mvp.model.repository;

import android.content.Context;
import android.provider.Settings;

import com.vardemin.faceauth.data.UserModel;
import com.vardemin.faceauth.mvp.model.ILocalRepository;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmObject;
import io.realm.RealmResults;

/**
 * Created by user on 22.01.18.
 */

public class LocalRepository implements ILocalRepository {
    private static final String NAME = "faceauth.realm";
    private final Realm realm;

    public LocalRepository(Context context) {
        byte[] _key = Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID).getBytes();
        byte[] key = new byte[64];
        System.arraycopy(_key,0,key,0,_key.length);
        RealmConfiguration secureConfig = new RealmConfiguration.Builder()
                .name(NAME)
                .deleteRealmIfMigrationNeeded()
                .encryptionKey(key)
                .build();
        this.realm = Realm.getInstance(secureConfig);
    }

    public Realm getRealm() {
        return realm;
    }

    public void save(RealmObject object) {
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(object);
        realm.commitTransaction();
    }

    public void save(List<RealmObject> objects) {
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(objects);
        realm.commitTransaction();
    }

    public void delete(RealmObject object) {
        realm.beginTransaction();
        object.deleteFromRealm();
        realm.commitTransaction();
    }

    public void delete(List<RealmObject> objects) {
        realm.beginTransaction();
        for (RealmObject object: objects) {
            object.deleteFromRealm();
        }
        realm.commitTransaction();
    }




}
