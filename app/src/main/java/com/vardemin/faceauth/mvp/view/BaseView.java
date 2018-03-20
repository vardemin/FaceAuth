package com.vardemin.faceauth.mvp.view;

import android.support.annotation.UiThread;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;

public interface BaseView extends MvpView {
    /**
     * Show message to user
     *
     * @param msg to display
     */
    @StateStrategyType(AddToEndSingleStrategy.class)
    @UiThread
    void showMessage(String msg);

    /**
     * Show loading indicator
     *
     * @param state show or dismiss
     */
    @StateStrategyType(AddToEndSingleStrategy.class)
    @UiThread
    void showLoading(boolean state);
}
