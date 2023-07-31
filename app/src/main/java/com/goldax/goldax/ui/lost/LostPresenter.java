package com.goldax.goldax.ui.lost;

import com.goldax.goldax.data.ListResult;

import java.util.ArrayList;

public class LostPresenter implements LostContract.Presenter {
    private static final String TAG = LostPresenter.class.getSimpleName();

    private LostModel mModel;
    private LostContract.View mView;

    public LostPresenter(LostContract.View view) {
        mView = view;
        mModel = new LostModel(this);
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
