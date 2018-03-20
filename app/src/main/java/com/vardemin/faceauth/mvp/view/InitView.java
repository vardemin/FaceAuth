package com.vardemin.faceauth.mvp.view;

import android.support.annotation.UiThread;

import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;

public interface InitView extends BaseView {

    @StateStrategyType(AddToEndSingleStrategy.class)
    void onResult(String json);

    @StateStrategyType(AddToEndSingleStrategy.class)
    void onInputStart();

    @StateStrategyType(AddToEndSingleStrategy.class)
    void onScanStart();

    @StateStrategyType(AddToEndSingleStrategy.class)
    void onMissingFace();

    @StateStrategyType(AddToEndSingleStrategy.class)
    void onFace();
}
