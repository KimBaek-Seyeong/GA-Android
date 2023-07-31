package com.goldax.goldax.ui.pick;

import com.goldax.goldax.data.ListResult;

import java.util.ArrayList;

public class PickPresenter implements PickContract.Presenter {
    private static final String TAG = PickPresenter.class.getSimpleName();

    private PickModel mModel;
    private PickContract.View mView;

    public PickPresenter(PickContract.View view) {
        mView = view;
        mModel = new PickModel(this);
    }

    @Override
    public void requestData() {
        mModel.requestData();
    }

    @Override
    public void setInitData(ArrayList<ListResult.ResultData> list) {
        mView.initLayout(list);
    }
}
