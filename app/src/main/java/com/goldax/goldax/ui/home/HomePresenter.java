package com.goldax.goldax.ui.home;

import com.goldax.goldax.data.ListResult;

import java.util.ArrayList;

public class HomePresenter implements HomeContract.Presenter {
    private static final String TAG = HomePresenter.class.getSimpleName();

    private HomeModel mModel;
    private HomeContract.View mView;

    public HomePresenter(HomeContract.View view) {
        mView = view;
        mModel = new HomeModel(this);
    }

    @Override
    public void requestPickData() {
        mModel.requestPickData();
    }

    @Override
    public void requestLostData() {
        mModel.requestLostData();
    }

    @Override
    public void setPickData(ArrayList<ListResult.ResultData> list) {
        mView.initPickLayout(list);
    }

    @Override
    public void setLostData(ArrayList<ListResult.ResultData> list) {
        mView.initLostLayout(list);
    }
}
