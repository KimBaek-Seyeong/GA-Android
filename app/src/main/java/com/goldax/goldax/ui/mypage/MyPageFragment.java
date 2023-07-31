package com.goldax.goldax.ui.mypage;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.goldax.goldax.MainActivity;
import com.goldax.goldax.R;
import com.goldax.goldax.application.ApplicationController;
import com.goldax.goldax.data.DataConst;
import com.goldax.goldax.data.ListResult;
import com.goldax.goldax.data.UserData;
import com.goldax.goldax.layout.CircleView;
import com.goldax.goldax.util.ImageLoader;
import com.goldax.goldax.util.ImageRequest;
import com.goldax.goldax.util.SharedPrefUtil;
import com.goldax.goldax.util.Utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.app.Activity.RESULT_OK;

public class MyPageFragment extends Fragment implements MyPageContract.View {
    private static final String TAG = MyPageFragment.class.getSimpleName();

    @BindView(R.id.f_mypage_profile_image)
    public CircleView mProfileImage;
    @BindView(R.id.f_mypage_profile_change_btn)
    public ImageButton mImageChangeBtn;
    @BindView(R.id.f_mypage_profile_title)
    public ConstraintLayout mProfileTitle;
    @BindView(R.id.f_mypage_upload_title)
    public ConstraintLayout mUploadTitle;
    @BindView(R.id.f_mypage_other_title)
    public ConstraintLayout mOtherTitle;

    @BindView(R.id.f_mypage_profile_nickname)
    public ConstraintLayout mProfileNickname;
    @BindView(R.id.f_mypage_profile_email)
    public ConstraintLayout mProfileEmail;

    @BindView(R.id.f_mypage_upload_pick)
    public ConstraintLayout mUploadPick;
    @BindView(R.id.f_mypage_upload_lost)
    public ConstraintLayout mUploadLost;

    @BindView(R.id.f_mypage_other_logout)
    public ConstraintLayout mOtherLogout;
    @BindView(R.id.f_mypage_other_withdrawal)
    public ConstraintLayout mOtherWithdrawal;

    private MyPageContract.Presenter mPresenter;
    private MyPageContract.View mView;

    private MainActivity mMainActivity;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.f_mypage, container, false);
        ButterKnife.bind(this, viewGroup);

        return viewGroup;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mView = this;
        mPresenter = new MyPagePresenter(this);
        mMainActivity = (MainActivity) getActivity();

        initializedLayout();

        requestData();
    }

    private void initializedLayout() {
        if (mProfileImage != null) {
            mProfileImage.setIcon(R.drawable.default_profile);
        }

        if (mImageChangeBtn != null) {
            mImageChangeBtn.setOnClickListener(mItemClickListener);
        }

        if (mProfileTitle != null) {
            TextView title = mProfileTitle.findViewById(R.id.i_mypage_menu_title_text);
            if (title != null) {
                title.setText("프로필");
            }
        }

        if (mUploadTitle != null) {
            TextView title = mUploadTitle.findViewById(R.id.i_mypage_menu_title_text);
            if (title != null) {
                title.setText("내가 올린 게시글");
            }
        }

        if (mOtherTitle != null) {
            TextView title = mOtherTitle.findViewById(R.id.i_mypage_menu_title_text);
            if (title != null) {
                title.setText("기타");
            }
        }

        if (mProfileNickname != null) {
            TextView key = mProfileNickname.findViewById(R.id.i_mypage_menu_text);
            if (key != null) {
                key.setText("닉네임");
            }
        }

        if (mProfileEmail != null) {
            TextView key = mProfileEmail.findViewById(R.id.i_mypage_menu_text);
            if (key != null) {
                key.setText("이메일");
            }
        }

        if (mUploadPick != null) {
            TextView key = mUploadPick.findViewById(R.id.i_mypage_menu_text);
            TextView value = mUploadPick.findViewById(R.id.i_mypage_menu_value);
            if (key != null) {
                key.setText("습득물");
            }
            if (value != null) {
                value.setText(">");
            }

            mUploadPick.setOnClickListener(mItemClickListener);
        }

        if (mUploadLost != null) {
            TextView key = mUploadLost.findViewById(R.id.i_mypage_menu_text);
            TextView value = mUploadLost.findViewById(R.id.i_mypage_menu_value);
            if (key != null) {
                key.setText("분실물");
            }
            if (value != null) {
                value.setText(">");
            }

            mUploadLost.setOnClickListener(mItemClickListener);
        }

        if (mOtherLogout != null) {
            TextView key = mOtherLogout.findViewById(R.id.i_mypage_menu_text);
            if (key != null) {
                key.setText("로그아웃");
            }

            mOtherLogout.setOnClickListener(mItemClickListener);
        }

        if (mOtherWithdrawal != null) {
            TextView key = mOtherWithdrawal.findViewById(R.id.i_mypage_menu_text);
            if (key != null) {
                key.setText("회원탈퇴");
            }
        }
    }

    @Override
    public void initLayout(UserData data) {
        if (mProfileImage != null) {
            if (TextUtils.isEmpty(data.avatar)) {
                mProfileImage.setIcon(R.drawable.default_profile);
            } else {
                ImageRequest imageRequest = new ImageRequest.Builder(mProfileImage.mIconView, data.avatar)
                        .setErrorResId(R.drawable.default_profile)
                        .setRound(true)
                        .build();
                ImageLoader.loadImage(imageRequest);
            }
        }

        if (mProfileNickname != null) {
            TextView value = mProfileNickname.findViewById(R.id.i_mypage_menu_value);
            if (value != null) {
                value.setText(data.name);
            }
        }

        if (mProfileEmail != null) {
            TextView value = mProfileEmail.findViewById(R.id.i_mypage_menu_value);
            if (value != null) {
                value.setText(data.email);
            }
        }
    }

    @Override
    public void requestData() {
        mPresenter.requestData();
    }

    @Override
    public void setProfileImage(String filePath) {
        if (mProfileImage != null) {
            mProfileImage.setIcon(filePath);
        }
    }

    @Override
    public void showPickData(ArrayList<ListResult.ResultData> itemList) {
        mMainActivity.showMyListFragment(DataConst.FRAGMENT_TYPE.PICK, itemList, mPresenter.getMyData());
    }

    @Override
    public void showLostData(ArrayList<ListResult.ResultData> itemList) {
        mMainActivity.showMyListFragment(DataConst.FRAGMENT_TYPE.LOST, itemList, mPresenter.getMyData());
    }

    private View.OnClickListener mItemClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int viewId = v.getId();

            switch (viewId) {
                case R.id.f_mypage_other_logout:
                    Utils.showToast("로그아웃 되었습니다.");
                    SharedPrefUtil.getInstance().removeLoginData(getActivity());
                    break;
                case R.id.f_mypage_upload_pick:
                    mPresenter.requestPickData();
                    break;
                case R.id.f_mypage_upload_lost:
                    mPresenter.requestLostData();
                    break;
                case R.id.f_mypage_profile_change_btn:
                    selectImageIntent();
                    break;
            }
        }
    };

    private void selectImageIntent() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, DataConst.REQUEST_CODE_SELECT_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult() called. " + requestCode + " | " + resultCode + " | " + data);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case DataConst.REQUEST_CODE_SELECT_IMAGE:
                    Bitmap bitmap = null;
                    try {
                        ContentResolver contentResolver = ApplicationController.getInstance().getContentResolver();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                            Uri uri = data.getData();
                            ImageDecoder.Source source = ImageDecoder.createSource(contentResolver, uri);
                            bitmap = ImageDecoder.decodeBitmap(source);
                        } else {
                            String dataString = data.getDataString();
                            bitmap = MediaStore.Images.Media.getBitmap(contentResolver, Uri.parse(dataString));
                        }
                        Log.d(TAG, "makeImgFile() bitmap: " + bitmap);
                        if (bitmap != null) {
                            String filePath = Utils.createImgFile(ApplicationController.getInstance(), bitmap);
                            if (filePath != null) {
                                mPresenter.changeProfileImage(filePath);
                            } else {
                                String guide = getString(R.string.select_image_failed);
                                Utils.showToast(guide);
                            }
                        } else {
                            Utils.showToast("이미지를 변환하는데 실패하였어요.");
                        }
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (OutOfMemoryError e) {
                        Utils.showToast("이미지 용량이 너무 커서 불러올 수 없어요.");
                    }
                    break;
            }
        }
    }
}
