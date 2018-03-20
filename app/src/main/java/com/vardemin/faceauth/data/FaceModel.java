package com.vardemin.faceauth.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;

public class FaceModel extends RealmObject {
    public RealmList<Float> getDescriptors() {
        return descriptors;
    }

    private RealmList<Float> descriptors;

    public FaceModel() {}

    public FaceModel(float[] descriptors) {
        this.descriptors = new RealmList<>();
        List<Float> floats = new ArrayList<>();
        for (float val : descriptors) {
            floats.add(val);
        }
        this.descriptors.addAll(floats);
    }
}
