package com.vardemin.faceauth.mvp.model.repository;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;

import com.vardemin.faceauth.mvp.model.ILocalRepository;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmObject;

public class LocalRepository implements ILocalRepository {
    private static final String NAME = "faceauth.realm";
    private final Realm realm;

    private Handler handler;

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
        handler= new Handler(Looper.getMainLooper());
    }

    public Realm getRealm() {
        return realm;
    }

    public void save(RealmObject object) {
        handler.post(() -> {
            realm.beginTransaction();
            realm.copyToRealmOrUpdate(object);
            realm.commitTransaction();
        });
    }

    public void save(List<RealmObject> objects) {
        handler.post(() -> {
            realm.beginTransaction();
            realm.copyToRealmOrUpdate(objects);
            realm.commitTransaction();
        });
    }

    public void delete(RealmObject object) {
        handler.post(() -> {
            realm.beginTransaction();
            object.deleteFromRealm();
            realm.commitTransaction();
        });
    }

    public void delete(List<RealmObject> objects) {
        handler.post(() -> {
            realm.beginTransaction();
            for (RealmObject object : objects) {
                object.deleteFromRealm();
            }
            realm.commitTransaction();
        });
    }

}
