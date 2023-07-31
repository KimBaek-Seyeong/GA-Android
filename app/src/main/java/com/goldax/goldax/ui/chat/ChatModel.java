package com.goldax.goldax.ui.chat;

import android.text.TextUtils;
import android.util.Log;

import com.goldax.goldax.application.ApplicationController;
import com.goldax.goldax.data.DataConst;
import com.goldax.goldax.data.UserData;
import com.goldax.goldax.data.UsersResult;
import com.goldax.goldax.network.NetworkService;
import com.goldax.goldax.util.SharedPrefUtil;
import com.goldax.goldax.util.Utils;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatModel {
    private static final String TAG = ChatModel.class.getSimpleName();

    private ChatContract.Presenter mPresenter;

    private NetworkService mNetworkService;

    private UserData mMyData;

    public ChatModel(ChatContract.Presenter homePresenter) {
        mPresenter = homePresenter;
        mNetworkService = ApplicationController.getInstance().getNetworkService();
    }

    public void setUserData(int userId, String userName) {
        mMyData = new UserData();
        mMyData.id = userId;
        mMyData.name = userName;
    }

    public void requestTargetProfileImage(String key, String value, String lastChat) {
        String userId = Utils.getChatUserId(value);

        if (TextUtils.isEmpty(userId)) {
            return;
        }

        int id = -1;
        try {
            id = Integer.parseInt(userId);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        Log.d(TAG, "requestUserData() called.");
        String token = SharedPrefUtil.getInstance().get(DataConst.KEY.SP_LOGIN_TOKEN, null);
        if (token == null) {
            Utils.showToast("로그인을 해주세요.");
            return;
        }

        Call<UsersResult> caller = mNetworkService.callUserProfileImage(token, id);
        caller.enqueue(new Callback<UsersResult>() {
            @Override
            public void onResponse(Call<UsersResult> call, Response<UsersResult> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "onResponse: requestData() response is success");

                    UsersResult result = response.body();
                    if (result.isSuccess) {
                        try {
                            // 서버에서 avatar 필드 자체를 내려주지 않는 경우에 대한 방어 처리
                            // (result.result.user.avatar 가 null 인 경우 방어)
                            mPresenter.addList(key, value, lastChat, result.result.user.avatar);
                        } catch (NullPointerException ne) {
                            mPresenter.addList(key, value, lastChat, null);
                        }
                    } else {
                        // do nothing.
                    }
                } else {
                    // do nothing.
                    Log.d(TAG, "onResponse: requestData() response is not success! ["
                            + response.code() + " | " + response.message() + "]");
                    Log.d(TAG, "onResponse: requestData() failed.. " + response.errorBody().toString());
                }
            }
            @Override
            public void onFailure(Call<UsersResult> call, Throwable t) {
                t.printStackTrace();
                Utils.showToast("네트워크를 확인해주세요.");
            }
        });
    }

    public void requestUserData() {
        Log.d(TAG, "requestUserData() called.");
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
                    } else {
                        // do nothing.
                    }
                } else {
                    // do nothing.
                    Log.d(TAG, "onResponse: requestData() response is not success! ["
                            + response.code() + " | " + response.message() + "]");
                    Log.d(TAG, "onResponse: requestData() failed.. " + response.errorBody().toString());
                }
            }
            @Override
            public void onFailure(Call<UsersResult> call, Throwable t) {
                t.printStackTrace();
                Utils.showToast("네트워크를 확인해주세요.");
            }
        });
    }

    public UserData getUserData() {
        return mMyData;
    }
}
