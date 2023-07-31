package com.goldax.goldax.ui.mypage;

import android.text.TextUtils;
import android.util.Log;

import com.goldax.goldax.application.ApplicationController;
import com.goldax.goldax.data.DataConst;
import com.goldax.goldax.data.ListResult;
import com.goldax.goldax.data.UserData;
import com.goldax.goldax.data.UsersResult;
import com.goldax.goldax.network.NetworkService;
import com.goldax.goldax.util.SharedPrefUtil;
import com.goldax.goldax.util.Utils;

import java.io.File;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyPageModel {
    private static final String TAG = MyPageModel.class.getSimpleName();

    private MyPageContract.Presenter mPresenter;

    private NetworkService mNetworkService;

    private UserData mMyData;
    private ArrayList<ListResult.ResultData> mMyPickList;
    private ArrayList<ListResult.ResultData> mMyLostList;

    public MyPageModel(MyPageContract.Presenter lostPresenter) {
        mPresenter = lostPresenter;
        mNetworkService = ApplicationController.getInstance().getNetworkService();
    }

    public void requestData() {
        String token = SharedPrefUtil.getInstance().get(DataConst.KEY.SP_LOGIN_TOKEN, null);
        if (token == null) {
            Utils.showToast("로그인을 해주세요.");
            return;
        }

        Call<UsersResult> caller = mNetworkService.callUsers(token);
        caller.enqueue(new Callback<UsersResult>() {
            @Override
            public void onResponse(Call<UsersResult> call, Response<UsersResult> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "onResponse: requestData() response is success");

                    UsersResult result = response.body();
                    if (result.isSuccess) {
                        mMyData = result.result.user;
                        mPresenter.setInitData(mMyData);
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
            public void onFailure(Call<UsersResult> call, Throwable t) {
                t.printStackTrace();
                Utils.showToast("네트워크를 확인해주세요.");
                mPresenter.setInitData(null);
            }
        });
    }

    public void changeProfileImage(String filePath) {
        String token = SharedPrefUtil.getInstance().get(DataConst.KEY.SP_LOGIN_TOKEN, null);
        if (token == null) {
            Utils.showToast("로그인을 해주세요.");
            return;
        }

        if (TextUtils.isEmpty(filePath)) {
            Utils.showToast("이미지를 불러오는데 실패하였어요.");
            return;
        }

        File imageFile = Utils.getImgFile(filePath);
        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), imageFile);
        MultipartBody.Part part = MultipartBody.Part.createFormData("image", imageFile.getName(), requestBody);

        Call<Void> caller = mNetworkService.callChangeProfileImage(token, part);
        caller.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "onResponse: changeProfileImage() response is success");
                    mPresenter.setProfileImage(filePath);
                } else {
                    Log.d(TAG, "onResponse: changeProfileImage() response is not success! ["
                            + response.code() + " | " + response.message() + "]");
                    Log.d(TAG, "onResponse: changeProfileImage() failed.. " + response.errorBody().toString());
                }
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                t.printStackTrace();
                Utils.showToast("네트워크를 확인해주세요.");
            }
        });
    }

    public void requestPickData() {
        Log.d(TAG, "requestPickData() called.");
        String token = SharedPrefUtil.getInstance().get(DataConst.KEY.SP_LOGIN_TOKEN, null);
        if (token == null) {
            Utils.showToast("로그인을 해주세요.");
            return;
        }

        Call<ListResult> caller = mNetworkService.callMyPickList(token);
        caller.enqueue(new Callback<ListResult>() {
            @Override
            public void onResponse(Call<ListResult> call, Response<ListResult> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "onResponse: requestPickData() response is success");

                    ListResult result = response.body();
                    if (result.isSuccess) {
                        mMyPickList = result.result;

                        mPresenter.setPickData(mMyPickList);
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
        Log.d(TAG, "requestLostData() called.");
        String token = SharedPrefUtil.getInstance().get(DataConst.KEY.SP_LOGIN_TOKEN, null);
        if (token == null) {
            Utils.showToast("로그인을 해주세요.");
            return;
        }

        Call<ListResult> caller = mNetworkService.callMyLostList(token);
        caller.enqueue(new Callback<ListResult>() {
            @Override
            public void onResponse(Call<ListResult> call, Response<ListResult> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "onResponse: requestLostData() response is success");

                    ListResult result = response.body();
                    if (result.isSuccess) {
                        mMyLostList = result.result;

                        mPresenter.setLostData(mMyLostList);
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

    public UserData getMyData() {
        return mMyData;
    }
}
