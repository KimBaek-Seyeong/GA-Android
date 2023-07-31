package com.goldax.goldax.ui.lost;

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

public class LostModel {
    private static final String TAG = LostModel.class.getSimpleName();

    private LostContract.Presenter mPresenter;

    private NetworkService mNetworkService;

    public LostModel(LostContract.Presenter lostPresenter) {
        mPresenter = lostPresenter;
        mNetworkService = ApplicationController.getInstance().getNetworkService();
    }

    public void requestData() {
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
                    Log.d(TAG, "onResponse: requestData() response is success");

                    ListResult result = response.body();
                    if (result.isSuccess) {
                        ArrayList<ListResult.ResultData> list = result.result;

                        mPresenter.setInitData(list);
                    } else {
                        mPresenter.setInitData(null);
                    }
                } else {
                    mPresenter.setInitData(null);
                    Log.d(TAG, "onResponse: requestData() response is not success! ["
                            + response.code() + " | " + response.message() + "]");
                    Log.d(TAG, "onResponse: requestData() failed.. " + response.errorBody().toString());
                }
            }
            @Override
            public void onFailure(Call<ListResult> call, Throwable t) {
                t.printStackTrace();
                Utils.showToast("네트워크를 확인해주세요.");
                mPresenter.setInitData(null);
            }
        });
    }
}
