package com.goldax.goldax.ui.home;

import android.util.Log;

import com.goldax.goldax.application.ApplicationController;
import com.goldax.goldax.data.DataConst;
import com.goldax.goldax.data.ListResult;
import com.goldax.goldax.network.NetworkService;
import com.goldax.goldax.util.SharedPrefUtil;
import com.goldax.goldax.util.Utils;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeModel {
    private static final String TAG = HomeModel.class.getSimpleName();

    private HomeContract.Presenter mPresenter;

    private NetworkService mNetworkService;

    private ArrayList<ListResult.ResultData> mPickList;
    private ArrayList<ListResult.ResultData> mLostList;

    public HomeModel(HomeContract.Presenter homePresenter) {
        mPresenter = homePresenter;
        mNetworkService = ApplicationController.getInstance().getNetworkService();

        mPickList = new ArrayList<>();
        mLostList = new ArrayList<>();
    }

    public void requestPickData() {
        Log.d(TAG, "requestPickData() called.");
        String token = SharedPrefUtil.getInstance().get(DataConst.KEY.SP_LOGIN_TOKEN, null);
        if (token == null) {
            Utils.showToast("로그인을 해주세요.");
            return;
        }

        Call<ListResult> caller = mNetworkService.callListPick(token);
        caller.enqueue(new Callback<ListResult>() {
            @Override
            public void onResponse(Call<ListResult> call, Response<ListResult> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "onResponse: requestPickData() response is success");

                    ListResult result = response.body();
                    if (result.isSuccess) {
                        mPickList = result.result;

                        mPresenter.setPickData(mPickList);
                    } else {
                        mPresenter.setPickData(null);
                    }
                } else {
                    mPresenter.setPickData(null);
                    Log.d(TAG, "onResponse: requestPickData() response is not success! ["
                            + response.code() + " | " + response.message() + "]");
                    Log.d(TAG, "onResponse: requestPickData() failed.. " + response.errorBody().toString());
                }
            }
            @Override
            public void onFailure(Call<ListResult> call, Throwable t) {
                t.printStackTrace();
                Utils.showToast("네트워크를 확인해주세요.");
                mPresenter.setPickData(null);
            }
        });
    }

    public void requestLostData() {
        String token = SharedPrefUtil.getInstance().get(DataConst.KEY.SP_LOGIN_TOKEN, null);
        if (token == null) {
            Utils.showToast("로그인을 해주세요.");
            return;
        }

        Call<ListResult> caller = mNetworkService.callListLost(token);
        caller.enqueue(new Callback<ListResult>() {
            @Override
            public void onResponse(Call<ListResult> call, Response<ListResult> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "onResponse: requestLostData() response is success");

                    ListResult result = response.body();
                    if (result.isSuccess) {
                        mLostList = result.result;

                        mPresenter.setLostData(mLostList);
                    } else {
                        mPresenter.setLostData(null);
                    }
                } else {
                    mPresenter.setLostData(null);
                    Log.d(TAG, "onResponse: requestLostData() response is not success! ["
                            + response.code() + " | " + response.message() + "]");
                    Log.d(TAG, "onResponse: requestLostData() failed.. " + response.errorBody().toString());
                }
            }
            @Override
            public void onFailure(Call<ListResult> call, Throwable t) {
                t.printStackTrace();
                Utils.showToast("네트워크를 확인해주세요.");
                mPresenter.setLostData(null);
            }
        });
    }
}
