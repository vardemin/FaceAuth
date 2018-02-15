package com.vardemin.faceauth.mvp.view;

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
    void showMessage(String msg);

    /**
     * Show loading indicator
     *
     * @param state show or dismiss
     */
    @StateStrategyType(AddToEndSingleStrategy.class)
    void showLoading(boolean state);
}
