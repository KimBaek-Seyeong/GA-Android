package com.goldax.goldax.ui.mypage;

import com.goldax.goldax.data.ListResult;
import com.goldax.goldax.data.UserData;

import java.util.ArrayList;

public class MyPagePresenter implements MyPageContract.Presenter {
    private static final String TAG = MyPagePresenter.class.getSimpleName();

    private MyPageModel mModel;
    private MyPageContract.View mView;

    public MyPagePresenter(MyPageContract.View view) {
        mView = view;
        mModel = new MyPageModel(this);
    }

    @Override
    public void requestData() {
        mModel.requestData();
    }

    @Override
    public void setInitData(UserData data) {
        mView.initLayout(data);
    }

    @Override
    public void changeProfileImage(String filePath) {
        mModel.changeProfileImage(filePath);
    }

    @Override
    public void setProfileImage(String filePath) {
        mView.setProfileImage(filePath);
    }

    @Override
    public void requestPickData() {
        mModel.requestPickData();
    }

    @Override
    public void setPickData(ArrayList<ListResult.ResultData> itemList) {
        mView.showPickData(itemList);
    }

    @Override
    public void requestLostData() {
        mModel.requestLostData();
    }

    @Override
    public void setLostData(ArrayList<ListResult.ResultData> itemList) {
        mView.showLostData(itemList);
    }

    @Override
    public UserData getMyData() {
        return mModel.getMyData();
    }
}
