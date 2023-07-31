package com.goldax.goldax;

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
import com.goldax.goldax.data.SignUpRequest;
import com.goldax.goldax.data.SignUpResult;
import com.goldax.goldax.network.NetworkService;
import com.goldax.goldax.util.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpActivity extends AppCompatActivity {
    private static final String TAG = SignUpActivity.class.getSimpleName();

    @BindView(R.id.sign_up_email_edit_text)
    public EditText mEmailEditText;
    @BindView(R.id.sign_up_password_edit_text)
    public EditText mPasswordEditText;
    @BindView(R.id.sign_up_name_edit_text)
    public EditText mNameEditText;
    @BindView(R.id.sign_up_school_number_edit_text)
    public EditText mSchoolNumEditText;
    @BindView(R.id.sign_up_submit_btn)
    public Button mSubmitBtn;

    private NetworkService mNetworkService;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);

        View customActionBar = LayoutInflater.from(ApplicationController.getInstance()).inflate(R.layout.toolbar, null);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false); // 기본 제목 제거
        actionBar.setDisplayHomeAsUpEnabled(false); // back 버튼
        actionBar.setHomeAsUpIndicator(R.drawable.general_back);
        ActionBar.LayoutParams layoutParams = new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        actionBar.setCustomView(customActionBar, layoutParams);

        mSubmitBtn.setOnClickListener(mClickListener);
        mNetworkService = ApplicationController.getInstance().getNetworkService();
    }

    private View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String email = mEmailEditText.getText().toString();
            String password = mPasswordEditText.getText().toString();
            String name = mNameEditText.getText().toString();
            String classId = mSchoolNumEditText.getText().toString();

            if (TextUtils.isEmpty(email)) {
                Utils.showToast("이메일을 입력해주세요.");
                return;
            } else if (TextUtils.isEmpty(password)) {
                Utils.showToast("비밀번호를 입력해주세요.");
                return;
            } else if (TextUtils.isEmpty(name)) {
                Utils.showToast("이름을 입력해주세요.");
                return;
            } else if (TextUtils.isEmpty(classId)) {
                Utils.showToast("학번을 입력해주세요.");
                return;
            }

            SignUpRequest requestData = new SignUpRequest(email, password, name, classId);
            requestSignUp(requestData);
        }
    };

    private void requestSignUp(SignUpRequest requestData) {
        Call<SignUpResult> caller = mNetworkService.callSignUp(requestData);
        caller.enqueue(new Callback<SignUpResult>() {
            @Override
            public void onResponse(Call<SignUpResult> call, Response<SignUpResult> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "onResponse: requestSignUp() response is success");

                    SignUpResult result = response.body();
                    if (result.isSuccess) {
                        Utils.showToast("회원가입을 완료하였습니다.");
                        finish();
                    } else {
                        Utils.showToast("[failed] " + result.status);
                    }

                } else {
                    String msg = "[" + response.code() + "] " + response.message();
                    Utils.showToast(msg);
                    Log.d(TAG, "onResponse: requestSignUp() response is not success! ["
                            + response.code() + " | " + response.message() + "]");
                    Log.d(TAG, "onResponse: requestSignUp() failed.. " + response.errorBody().toString());
                }
            }
            @Override
            public void onFailure(Call<SignUpResult> call, Throwable t) {
                // 이미지 검출 통신 실패시.
                t.printStackTrace();
                Utils.showToast("네트워크를 확인해주세요.");
            }
        });
    }
}
