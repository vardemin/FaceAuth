package com.vardemin.faceauth.data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class DataModel extends RealmObject {

    public String getApplication() {
        return application;
    }

    public String getId() {
        return id;
    }

    public String getUsername() {
        return user;
    }

    public RealmList<FieldModel> getFields() {
        return fields;
    }

    public Date getDate() {
        return date;
    }

    private String application;

    public DataModel(String id, String application, String user, List<FieldModel> fields, Date date) {
        this.application = application;
        this.id = id;
        this.user = user;
        this.fields = new RealmList<>();
        this.fields.addAll(fields);
        this.date = date;
    }

    @PrimaryKey
    @Required
    private String id;

    private String user;

    private RealmList<FieldModel> fields;

    private FaceModel face;

    private Date date = new Date();

    public DataModel() {}


    public void setId(String id) {
        this.id = id;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setFields(RealmList<FieldModel> fields) {
        this.fields = fields;
    }

    public void setApplication(String application) {
        this.application = application;
    }

    public FaceModel getFace() {
        return face;
    }

    public void setFace(FaceModel face) {
        this.face = face;
    }

    @Override
    public String toString() {
        JSONObject object = new JSONObject();
        try {
            object.put("id", id);
            object.put("application", application);
            object.put("user", user);
            JSONArray array = new JSONArray();
            for (FieldModel fieldModel: fields) {
                JSONObject field = new JSONObject();
                field.put("field", fieldModel.getField());
                field.put("type", fieldModel.getInputType());
                field.put("value", fieldModel.getValue());
                array.put(field);
            }
            object.put("fields", array);
            return  object.toString();
        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }
    }
}
