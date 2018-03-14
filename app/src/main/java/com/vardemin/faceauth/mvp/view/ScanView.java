package com.vardemin.faceauth.mvp.view;

import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy;
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;

public interface ScanView extends BaseView  {
    @StateStrategyType(AddToEndSingleStrategy.class)
    void showWaitingDialog(boolean state);
    @StateStrategyType(AddToEndSingleStrategy.class)
    void onScanStart();
}
