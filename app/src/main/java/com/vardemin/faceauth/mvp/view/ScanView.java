package com.vardemin.faceauth.mvp.view;

import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy;
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;
import com.vardemin.faceauth.mvp.model.camera.FacePosition;

public interface ScanView extends BaseView {
    @StateStrategyType(AddToEndSingleStrategy.class)
    void showWaitingDialog(boolean state);

    @StateStrategyType(AddToEndSingleStrategy.class)
    void onScanStart();

    @StateStrategyType(AddToEndSingleStrategy.class)
    void showMissingFace();

    @StateStrategyType(AddToEndSingleStrategy.class)
    void notifyPendingPose(FacePosition position);

    @StateStrategyType(SkipStrategy.class)
    void notifyDifferentPerson();
}
