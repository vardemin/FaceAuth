package com.vardemin.faceauth.mvp.model.camera;

import android.content.Context;

import com.vardemin.faceauth.R;

public enum FacePosition {
    STRAIGHT,
    RIGHT,
    TOP_RIGHT,
    TOP,
    TOP_LEFT,
    LEFT,
    BOTTOM_LEFT,
    BOTTOM,
    BOTTOM_RIGHT,
    UNRECOGNIZED;

    public String getDesription(Context context) {
        String str = "";
        switch (this) {
            case STRAIGHT: str = context.getString(R.string.pose_straight); break;
            case TOP: str = context.getString(R.string.pose_top); break;
            case TOP_LEFT: str = context.getString(R.string.pose_top_left); break;
            case LEFT: str = context.getString(R.string.pose_left); break;
            case BOTTOM_LEFT: str = context.getString(R.string.pose_bottom_left); break;
            case BOTTOM: str = context.getString(R.string.pose_bottom); break;
            case BOTTOM_RIGHT: str = context.getString(R.string.pose_bottom_right); break;
            case RIGHT: str = context.getString(R.string.pose_right); break;
            case TOP_RIGHT: str = context.getString(R.string.pose_top_right); break;
        }
        return str;
    }
}
