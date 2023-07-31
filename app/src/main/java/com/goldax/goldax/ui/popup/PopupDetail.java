package com.goldax.goldax.ui.popup;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Group;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.goldax.goldax.ui.chat.DTO.ChatRoomDTO;
import com.google.android.material.tabs.TabLayout;
import com.goldax.goldax.MainActivity;
import com.goldax.goldax.R;
import com.goldax.goldax.application.ApplicationController;
import com.goldax.goldax.data.DataConst;
import com.goldax.goldax.data.ListResult;
import com.goldax.goldax.data.UserData;
import com.goldax.goldax.layout.CircleView;
import com.goldax.goldax.network.NetworkService;
import com.goldax.goldax.ui.popup.detail.ViewPagerAdapter;
import com.goldax.goldax.util.ImageLoader;
import com.goldax.goldax.util.ImageRequest;
import com.goldax.goldax.util.SharedPrefUtil;
import com.goldax.goldax.util.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PopupDetail extends Fragment {
    private static final String TAG = PopupDetail.class.getSimpleName();

    private MainActivity mMainActivity;

    @BindView(R.id.popup_detail_image_viewpager)
    public ViewPager mViewPager;
    @BindView(R.id.popup_detail_image_viewpager_tab_layout)
    public TabLayout mTabLayout;
    @BindView(R.id.popup_detail_content_title)
    public TextView mTitle;
    @BindView(R.id.popup_detail_content_desc)
    public TextView mDesc;
    @BindView(R.id.popup_detail_content_reward)
    public ConstraintLayout mRewardLayout;
    @BindView(R.id.popup_detail_content_category)
    public ConstraintLayout mCategoryLayout;
    @BindView(R.id.popup_detail_content_input_date)
    public ConstraintLayout mInputDateLayout;
    @BindView(R.id.popup_detail_content_write_date)
    public ConstraintLayout mWriteDateLayout;
    @BindView(R.id.popup_detail_content_location)
    public ConstraintLayout mLocationLayout;
    @BindView(R.id.popup_detail_profile_image)
    public CircleView mProfileImage;
    @BindView(R.id.popup_detail_profile_name)
    public TextView mProfileName;
    @BindView(R.id.popup_detail_profile_message_btn)
    public ImageView mMessageBtn;

    @BindView(R.id.popup_detail_closed_mask_group)
    public Group mMaskGroup;
    @BindView(R.id.popup_detail_closed_mask_text)
    public TextView mMaskText;

    private @DataConst.FRAGMENT_TYPE int mCurType;
    private ListResult.ResultData mItem;
    private UserData mUserData;

    private NetworkService mNetworkService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.popup_detail, container, false);
        ButterKnife.bind(this, viewGroup);

        viewGroup.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // 하위 레이아웃으로 이벤트 내려가지 않도록 막음
                return true;
            }
        });

        return viewGroup;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mMainActivity = (MainActivity) getActivity();

        if (getArguments() != null) {
            mCurType = getArguments().getInt(DataConst.KEY.POPUP_TYPE);
            mItem = (ListResult.ResultData) getArguments().getSerializable(DataConst.KEY.ITEM_DATA);
            mUserData = (UserData) getArguments().getSerializable(DataConst.KEY.MY_DATA);
        }

        initLayout();
    }

    private void initLayout() {
        if (mItem == null) {
            Log.d(TAG, "initLayout() called. mItem is null. do nothing ");
            return;
        }

        if (mViewPager != null) {
            ViewPagerAdapter adapter = new ViewPagerAdapter(mMainActivity, mItem.images);
            mViewPager.setAdapter(adapter);

            if (mTabLayout != null) {
                if (!Utils.isListEmpty(mItem.images) && mItem.images.size() >= 2) {
                    mTabLayout.setupWithViewPager(mViewPager, true);
                }
            }
        }

        if (mTitle != null) {
            mTitle.setText(mItem.title);
        }

        if (mDesc != null) {
            mDesc.setText(mItem.contents);
        }

        if (mRewardLayout != null) {
            TextView keyView = mRewardLayout.findViewById(R.id.i_content_layout_data_key);
            if (keyView != null) {
                keyView.setText("보상금");
            }

            TextView valueView = mRewardLayout.findViewById(R.id.i_content_layout_data_value);
            if (valueView != null) {
                String reward = "";
                if (TextUtils.isEmpty(mItem.reward)) {
                    reward = "0";
                } else {
                    reward = mItem.reward;
                }

                valueView.setText(Utils.addComma(reward) + " 원");
            }
        }

        if (mCategoryLayout != null) {
            TextView keyView = mCategoryLayout.findViewById(R.id.i_content_layout_data_key);
            if (keyView != null) {
                keyView.setText("카테고리");
            }

            TextView valueView = mCategoryLayout.findViewById(R.id.i_content_layout_data_value);
            if (valueView != null) {
                valueView.setText(mItem.category);
            }
        }

        if (mInputDateLayout != null) {
            TextView keyView = mInputDateLayout.findViewById(R.id.i_content_layout_data_key);
            if (keyView != null) {
                String key = mCurType == DataConst.FRAGMENT_TYPE.PICK ? "습득일자" : "분실일자";
                keyView.setText(key);
            }

            TextView valueView = mInputDateLayout.findViewById(R.id.i_content_layout_data_value);
            if (valueView != null) {
                if (mCurType == DataConst.FRAGMENT_TYPE.LOST) {
                    String lostAt = mItem.lostAt;
                    if (!TextUtils.isEmpty(lostAt) && lostAt.length() > 9) {
                        lostAt = lostAt.substring(0, 10);
                    }
                    valueView.setText(lostAt);
                } else if (mCurType == DataConst.FRAGMENT_TYPE.PICK) {
                    String foundAt = mItem.foundAt;
                    if (!TextUtils.isEmpty(foundAt) && foundAt.length() > 9) {
                        foundAt = foundAt.substring(0, 10);
                    }
                    valueView.setText(foundAt);
                }
            }
        }

        if (mWriteDateLayout != null) {
            TextView keyView = mWriteDateLayout.findViewById(R.id.i_content_layout_data_key);
            if (keyView != null) {
                keyView.setText("등록일자");
            }

            TextView valueView = mWriteDateLayout.findViewById(R.id.i_content_layout_data_value);
            if (valueView != null) {
                String createAt = mItem.createdAt;
                if (!TextUtils.isEmpty(createAt) && createAt.length() > 9) {
                    createAt = createAt.substring(0, 10);
                }
                valueView.setText(createAt);
            }
        }

        if (mLocationLayout != null) {
            TextView keyView = mLocationLayout.findViewById(R.id.i_content_layout_data_key);
            if (keyView != null) {
                String key = mCurType == DataConst.FRAGMENT_TYPE.PICK ? "습득장소" : "분실장소";
                keyView.setText(key);
            }

            TextView valueView = mLocationLayout.findViewById(R.id.i_content_layout_data_value);
            if (valueView != null) {
                String location = mItem.location;
                if (location != null) {
                    String[] locations = location.split(", ");
                    if (locations != null && locations.length >= 2) {
                        location = locations[0] + " " + locations[1];
                    }
                }

                valueView.setText(location);
            }
        }

        if (mItem.user == null) {
            // 마이 페이지를 통해 들어온 경우 유저 데이터 셋팅
            mItem.user = mUserData;
        }

        if (mProfileImage != null) {
            if (TextUtils.isEmpty(mItem.user.avatar)) {
                mProfileImage.setIcon(R.drawable.default_profile);
            } else {
                ImageRequest imageRequest = new ImageRequest.Builder(mProfileImage.mIconView, mItem.user.avatar)
                        .setErrorResId(R.drawable.default_profile)
                        .setRound(true)
                        .build();
                ImageLoader.loadImage(imageRequest);
            }
        }

        if (mProfileName != null) {
            mProfileName.setText(mItem.user.name);
        }

        if (mMaskGroup != null) {
            if (mItem.isClosed) {
                String text = mCurType == DataConst.FRAGMENT_TYPE.LOST ? "습득완료" : "전달완료";
                if (mMaskText != null) {
                    mMaskText.setText(text);
                }
                mMaskGroup.setVisibility(View.VISIBLE);
            } else {
                mMaskGroup.setVisibility(View.GONE);
            }

        }

        if (mMessageBtn != null) {
            mMessageBtn.setOnClickListener(mClickListener);
        }
    }

    /**
     * 게시물 삭제 처리
     */
    public void removeItem() {
        if (mItem == null) {
            Log.d(TAG, "removeItem() called. mItem is null. do nothing.");
            return;
        }

        if (mItem.isDeleted) {
            Utils.showToast("이미 삭제되었어요.");
            mMainActivity.hideDetailFragment(false, null);
            mMainActivity.reloadItemList();
            return;
        }

        mNetworkService = ApplicationController.getInstance().getNetworkService();

        Log.d(TAG, "removeItem() called.");
        String token = SharedPrefUtil.getInstance().get(DataConst.KEY.SP_LOGIN_TOKEN, null);
        if (token == null) {
            Utils.showToast("로그인을 해주세요.");
            return;
        }

        Call<Void> caller = null;
        if (mCurType == DataConst.FRAGMENT_TYPE.LOST) {
            caller = mNetworkService.callDeleteLost(token, mItem.id);
        } else if (mCurType == DataConst.FRAGMENT_TYPE.PICK) {
            caller = mNetworkService.callDeletePick(token, mItem.id);
        }

        if (caller == null) {
            Log.d(TAG, "removeItem() mCurType is not PICK or LOST. can not delete item.");
            return;
        }

        caller.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "onResponse: removeItem() response is success");
                    Utils.showToast("삭제가 완료되었습니다.");
                    mMainActivity.hideDetailFragment(false, null);
                    mMainActivity.reloadItemList();
                } else {
                    Log.d(TAG, "onResponse: removeItem() response is not success! ["
                            + response.code() + " | " + response.message() + "]");
                    Log.d(TAG, "onResponse: removeItem() failed.. " + response.errorBody().toString());
                }
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                t.printStackTrace();
                Utils.showToast("네트워크를 확인해주세요.");
            }
        });
    }

    /**
     * 게시물 전달 완료 처리
     */
    public void completeItem() {
        if (mItem == null) {
            Log.d(TAG, "completeItem() called. mItem is null. do nothing.");
            return;
        }

        if (mItem.isClosed) {
            String closedText = mCurType == DataConst.FRAGMENT_TYPE.LOST ? "이미 습득 완료 되었어요." : "이미 전달 완료 되었어요.";
            Utils.showToast(closedText);
            mMainActivity.hideDetailFragment(false, null);
            mMainActivity.reloadItemList();
            return;
        }

        mNetworkService = ApplicationController.getInstance().getNetworkService();

        Log.d(TAG, "completeItem() called.");
        String token = SharedPrefUtil.getInstance().get(DataConst.KEY.SP_LOGIN_TOKEN, null);
        if (token == null) {
            Utils.showToast("로그인을 해주세요.");
            return;
        }

        Call<Void> caller = null;
        if (mCurType == DataConst.FRAGMENT_TYPE.LOST) {
            caller = mNetworkService.callCompleteLost(token, mItem.id);
        } else if (mCurType == DataConst.FRAGMENT_TYPE.PICK) {
            caller = mNetworkService.callCompletePick(token, mItem.id);
        }

        if (caller == null) {
            Log.d(TAG, "completeItem() mCurType is not PICK or LOST. can not complete item.");
            return;
        }

        caller.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "onResponse: completeItem() response is success");
                    String closedText = mCurType == DataConst.FRAGMENT_TYPE.LOST ? "습득 완료 처리가 되었어요." : "전달 완료 처리가 되었어요.";
                    Utils.showToast(closedText);
                    mMainActivity.hideDetailFragment(false, null);
                    mMainActivity.reloadItemList();
                } else {
                    Log.d(TAG, "onResponse: completeItem() response is not success! ["
                            + response.code() + " | " + response.message() + "]");
                    Log.d(TAG, "onResponse: completeItem() failed.. " + response.errorBody().toString());
                }
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                t.printStackTrace();
                Utils.showToast("네트워크를 확인해주세요.");
            }
        });
    }

    private View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            try {
                String myName = SharedPrefUtil.getInstance().get(DataConst.KEY.SP_LOGIN_NAME, "");
                String myIndex = SharedPrefUtil.getInstance().get(DataConst.KEY.SP_USER_INDEX, "-1");
                int myId = Integer.parseInt(myIndex);
                String targetName = mItem.user.name;
                int targetId = mItem.user.id;
                String targetProfileUrl = mItem.user.avatar;

                if (myId == targetId) {
                    // 내가 쓴 글일 경우
                    Utils.showToast("자신과는 채팅을 할 수 없어요.");
                    return;
                }

                ChatRoomDTO chatRoomDTO = new ChatRoomDTO(myName, myId, targetName, targetId, targetProfileUrl);
                mMainActivity.hideDetailFragment(true, chatRoomDTO);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

}
