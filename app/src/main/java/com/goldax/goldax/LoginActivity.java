package com.goldax.goldax;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.goldax.goldax.application.ApplicationController;
import com.goldax.goldax.data.DataConst;
import com.goldax.goldax.data.LoginRequest;
import com.goldax.goldax.data.LoginResult;
import com.goldax.goldax.data.UsersResult;
import com.goldax.goldax.network.NetworkService;
import com.goldax.goldax.util.SharedPrefUtil;
import com.goldax.goldax.util.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = LoginActivity.class.getSimpleName();

    @BindView(R.id.login_email_edit_text)
    public EditText mEmailEdit;
    @BindView(R.id.login_password_edit_text)
    public EditText mPasswordEdit;
    @BindView(R.id.login_sign_in_btn)
    public Button mSignInBtn;
    @BindView(R.id.login_sign_up_btn)
    public Button mSignUpBtn;

    private NetworkService mNetworkService;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        Utils.setDensity(ApplicationController.getInstance());

        View customActionBar = LayoutInflater.from(ApplicationController.getInstance()).inflate(R.layout.toolbar, null);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false); // 기본 제목 제거
        actionBar.setDisplayHomeAsUpEnabled(false); // back 버튼
        actionBar.setHomeAsUpIndicator(R.drawable.general_back);
        ActionBar.LayoutParams layoutParams = new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        actionBar.setCustomView(customActionBar, layoutParams);

        mNetworkService = ApplicationController.getInstance().getNetworkService();

        String token = SharedPrefUtil.getInstance().get(DataConst.KEY.SP_LOGIN_TOKEN, null);
        if (token != null) {
            requestUserData();
            return;
        }

        mSignInBtn.setOnClickListener(mClickListener);
        mSignUpBtn.setOnClickListener(mClickListener);
    }

    private View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.login_sign_in_btn:
                    // 로그인 버튼 클릭 시
                    String email = mEmailEdit.getText().toString();
                    String password = mPasswordEdit.getText().toString();

                    if (TextUtils.isEmpty(email)) {
                        Utils.showToast("이메일을 입력해주세요.");
                        return;
                    } else if (TextUtils.isEmpty(password)) {
                        Utils.showToast("비밀번호를 입력해주세요.");
                        return;
                    }

                    LoginRequest requestData = new LoginRequest(email, password);
                    requestLogin(requestData);
                    break;
                case R.id.login_sign_up_btn:
                    // 회원가입 버튼 클릭 시
                    Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                    startActivity(intent);
                    break;
            }
        }
    };

    private void requestLogin(LoginRequest requestData) {
        Call<LoginResult> caller = mNetworkService.callLogin(requestData);
        caller.enqueue(new Callback<LoginResult>() {
            @Override
            public void onResponse(Call<LoginResult> call, Response<LoginResult> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "onResponse: requestLogin() response is success");

                    LoginResult result = response.body();
                    if (result.isSuccess) {
                        String token = result.result.token;
                        String name = result.result.profile.name;
                        int userIdx = result.result.profile.id;

                        SharedPrefUtil.getInstance().set(DataConst.KEY.SP_LOGIN_TOKEN, token);
                        SharedPrefUtil.getInstance().set(DataConst.KEY.SP_LOGIN_NAME, name);
                        SharedPrefUtil.getInstance().set(DataConst.KEY.SP_USER_INDEX, "" + userIdx);

                        Utils.showToast(name + "님 반가워요!");

                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        //앞서 쌓여있던 NoLogin된 메인 액티비티 제거하라는 플래그
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    } else {
                        String desc = result.description;
                        String msg = "[" + response.code() + "] " + desc;
                        Utils.showToast(msg);
                    }
                } else {
                    String desc = "";
                    if (response.code() == 400 || response.code() == 401) {
                        desc = "로그인에 실패하였습니다.";
                    }

                    String msg = "[" + response.code() + "] " + desc;
                    Utils.showToast(msg);

                    Log.d(TAG, "onResponse: requestLogin() response is not success! ["
                            + response.code() + " | " + response.message() + "]");
                    Log.d(TAG, "onResponse: requestLogin() failed.. " + response.errorBody().toString());
                }
            }
            @Override
            public void onFailure(Call<LoginResult> call, Throwable t) {
                // 이미지 검출 통신 실패시.
                t.printStackTrace();
                Utils.showToast("네트워크를 확인해주세요.");
            }
        });
    }

    public void requestUserData() {
        String token = SharedPrefUtil.getInstance().get(DataConst.KEY.SP_LOGIN_TOKEN, null);
        if (token == null) {
            Utils.showToast("로그인 정보가 만료되었어요. 다시 로그인 해주세요.");
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
                        String name = SharedPrefUtil.getInstance().get(DataConst.KEY.SP_LOGIN_NAME, null);
                        Utils.showToast(name + "님 반가워요!");

                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        //앞서 쌓여있던 NoLogin된 메인 액티비티 제거하라는 플래그
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                        return;
                    } else {
                        Utils.showToast("로그인 정보가 만료되었어요. 다시 로그인 해주세요.");

                        mSignInBtn.setOnClickListener(mClickListener);
                        mSignUpBtn.setOnClickListener(mClickListener);
                    }
                } else {
                    mSignInBtn.setOnClickListener(mClickListener);
                    mSignUpBtn.setOnClickListener(mClickListener);

                    Log.d(TAG, "onResponse: requestData() response is not success! ["
                            + response.code() + " | " + response.message() + "]");
                    Log.d(TAG, "onResponse: requestData() failed.. " + response.errorBody().toString());
                }
            }
            @Override
            public void onFailure(Call<UsersResult> call, Throwable t) {
                t.printStackTrace();
                Utils.showToast("네트워크를 확인해주세요.");

                mSignInBtn.setOnClickListener(mClickListener);
                mSignUpBtn.setOnClickListener(mClickListener);
            }
        });
    }
}
