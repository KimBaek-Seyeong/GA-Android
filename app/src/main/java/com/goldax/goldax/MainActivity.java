package com.goldax.goldax;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Group;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.goldax.goldax.application.ApplicationController;
import com.goldax.goldax.data.DataConst;
import com.goldax.goldax.data.ListResult;
import com.goldax.goldax.data.UserData;
import com.goldax.goldax.ui.chat.ChatConst;
import com.goldax.goldax.ui.chat.ChatFragment;
import com.goldax.goldax.ui.chat.DTO.ChatRoomDTO;
import com.goldax.goldax.ui.chat.recycler.ChatRoomData;
import com.goldax.goldax.ui.home.HomeFragment;
import com.goldax.goldax.ui.lost.LostFragment;
import com.goldax.goldax.ui.mypage.MyPageFragment;
import com.goldax.goldax.ui.pick.PickFragment;
import com.goldax.goldax.ui.popup.PopupChat;
import com.goldax.goldax.ui.popup.PopupDetail;
import com.goldax.goldax.ui.popup.PopupMyList;
import com.goldax.goldax.ui.popup.PopupRegister;
import com.goldax.goldax.ui.popup.PopupSearch;
import com.goldax.goldax.util.SharedPrefUtil;
import com.goldax.goldax.util.Utils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    @BindView(R.id.a_main_root)
    ConstraintLayout mRootLayout;
    @BindView(R.id.a_main_frame)
    FrameLayout mFrameLayout;
    @BindView(R.id.a_main_search_frame)
    FrameLayout mSearchFrameLayout;
    @BindView(R.id.a_main_full_frame)
    FrameLayout mFullFrameLayout;
    @BindView(R.id.a_main_loading_group)
    Group mLoadingGroup;

    @BindView(R.id.a_main_home_layout)
    public ConstraintLayout mHomeLayout;
    @BindView(R.id.a_main_lost_layout)
    public ConstraintLayout mLostLayout;
    @BindView(R.id.a_main_pick_layout)
    public ConstraintLayout mPickLayout;
    @BindView(R.id.a_main_chat_layout)
    public ConstraintLayout mChatLayout;
    @BindView(R.id.a_main_mypage_layout)
    public ConstraintLayout mMyPageLayout;
    @BindView(R.id.a_main_bottom_scroll_thumb)
    public View mScrollBar;

    private ActionBar mActionBar;
    private TextView mActionBarText;
    private ImageView mActionBarImage;

    private FragmentManager mFragmentManager;
    private Fragment mFragment; // 현재 노출 중인 탭 프래그먼트
    private Fragment mSearchFragment;
    private Fragment mRegisterFragment;
    private Fragment mDetailFragment;
    private Fragment mMyListFragment;
    private Fragment mChatDetailFragment;

    private ArrayList<Fragment> mFragmentList; // 현재 노출 중인 Popup 리스트
    private ArrayList<ViewGroup> mBottomLayoutList;

    private @DataConst.FRAGMENT_TYPE int mCurType;
    private Menu mMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        View customActionBar = LayoutInflater.from(ApplicationController.getInstance()).inflate(R.layout.toolbar, null);

        mActionBar = getSupportActionBar();
        mActionBar.setDisplayShowCustomEnabled(true);
        mActionBar.setDisplayShowTitleEnabled(false); // 기본 제목 제거
        mActionBar.setDisplayHomeAsUpEnabled(false); // back 버튼
        mActionBar.setHomeAsUpIndicator(R.drawable.general_back);
        ActionBar.LayoutParams layoutParams = new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mActionBar.setCustomView(customActionBar, layoutParams);

        mActionBarImage = customActionBar.findViewById(R.id.toolbar_image);
        mActionBarText = customActionBar.findViewById(R.id.toolbar_title);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                     || checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "권한 설정 완료");
            } else {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE},
                        1);
                Log.d(TAG, "권한 설정 요청");
            }
        }

        ButterKnife.bind(this);

        initLayout();

        mFragmentList = new ArrayList<>();

        mFragmentManager = getSupportFragmentManager();
        mFragment = new HomeFragment();

        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        transaction.replace(mFrameLayout.getId(), mFragment);
        transaction.commitAllowingStateLoss();
        mCurType = DataConst.FRAGMENT_TYPE.HOME;
    }

    private void initLayout() {
        mBottomLayoutList = new ArrayList<>();
        mBottomLayoutList.add(mHomeLayout);
        mBottomLayoutList.add(mLostLayout);
        mBottomLayoutList.add(mPickLayout);
        mBottomLayoutList.add(mChatLayout);
        mBottomLayoutList.add(mMyPageLayout);

        mHomeLayout.setBackgroundResource(R.drawable.selector_tab_bg);
        ImageView iconView = mHomeLayout.findViewById(R.id.i_bottom_layout_icon);
        TextView textView = mHomeLayout.findViewById(R.id.i_bottom_layout_text);
        iconView.setImageResource(R.drawable.baseline_home_white_36);
        textView.setText("홈");
        mHomeLayout.setSelected(true);

        mLostLayout.setBackgroundResource(R.drawable.selector_tab_bg);
        iconView = mLostLayout.findViewById(R.id.i_bottom_layout_icon);
        textView = mLostLayout.findViewById(R.id.i_bottom_layout_text);
        iconView.setBackgroundResource(R.drawable.tab_lost);
        textView.setText("분실물");

        mPickLayout.setBackgroundResource(R.drawable.selector_tab_bg);
        iconView = mPickLayout.findViewById(R.id.i_bottom_layout_icon);
        textView = mPickLayout.findViewById(R.id.i_bottom_layout_text);
        iconView.setBackgroundResource(R.drawable.tab_pick);
        textView.setText("습득물");

        mChatLayout.setBackgroundResource(R.drawable.selector_tab_bg);
        iconView = mChatLayout.findViewById(R.id.i_bottom_layout_icon);
        textView = mChatLayout.findViewById(R.id.i_bottom_layout_text);
        iconView.setBackgroundResource(R.drawable.tab_chat);
        textView.setText("채팅");

        mMyPageLayout.setBackgroundResource(R.drawable.selector_tab_bg);
        iconView = mMyPageLayout.findViewById(R.id.i_bottom_layout_icon);
        textView = mMyPageLayout.findViewById(R.id.i_bottom_layout_text);
        iconView.setBackgroundResource(R.drawable.baseline_account_box_white_36);
        textView.setText("MY");

        // 스크롤바 필요시 주석 해제
//        ConstraintLayout.LayoutParams conLayoutParams = (ConstraintLayout.LayoutParams) mScrollBar.getLayoutParams();
//        mHomeLayout.measure(0,0);
//        conLayoutParams.width = mHomeLayout.getMeasuredWidth() * (int) Utils.getDensity();
//
//        mScrollBar.setLayoutParams(conLayoutParams);

        mHomeLayout.setOnClickListener(mClickListener);
        mLostLayout.setOnClickListener(mClickListener);
        mPickLayout.setOnClickListener(mClickListener);
        mChatLayout.setOnClickListener(mClickListener);
        mMyPageLayout.setOnClickListener(mClickListener);
    }

    public void showSearchPopup(@DataConst.FRAGMENT_TYPE int curType) {
        if (mFragmentManager == null) {
            mFragmentManager = getSupportFragmentManager();
        }

        mSearchFragment = new PopupSearch();

        Bundle bundle = new Bundle();
        bundle.putInt(DataConst.KEY.POPUP_TYPE, curType);
        mSearchFragment.setArguments(bundle);

        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        transaction.replace(mSearchFrameLayout.getId(), mSearchFragment);
        transaction.commitAllowingStateLoss();
    }

    public void hideSearchPopup() {
        if (mFragmentManager == null) {
            mFragmentManager = getSupportFragmentManager();
        }

        if (mSearchFragment != null) {
            FragmentTransaction transaction = mFragmentManager.beginTransaction();
            transaction.remove(mSearchFragment);
            transaction.commitAllowingStateLoss();

            mSearchFragment = null;
        }
    }

    public void showRegisterFragment(@DataConst.FRAGMENT_TYPE int curType) {
        if (mFragmentManager == null) {
            mFragmentManager = getSupportFragmentManager();
        }

        mRegisterFragment = new PopupRegister();

        Bundle bundle = new Bundle();
        bundle.putInt(DataConst.KEY.POPUP_TYPE, curType);
        mRegisterFragment.setArguments(bundle);

        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        transaction.replace(mFullFrameLayout.getId(), mRegisterFragment);
        transaction.commitAllowingStateLoss();

        mFragmentList.add(mRegisterFragment);

        if (mActionBar != null) {
            mActionBar.setDisplayHomeAsUpEnabled(true);
            String actionBarText = mActionBarText.getText().toString();
            if (curType == DataConst.FRAGMENT_TYPE.LOST) {
                actionBarText = "분실물 등록";
            } else if (curType == DataConst.FRAGMENT_TYPE.PICK) {
                actionBarText = "습득물 등록";
            }
            mActionBarText.setText(actionBarText);
            mActionBarImage.setVisibility(View.GONE);
        }
        visibilityActionBar(false, false);
    }

    public void hideRegisterFragment() {
        if (mFragmentManager == null) {
            mFragmentManager = getSupportFragmentManager();
        }

        if (mRegisterFragment != null) {
            FragmentTransaction transaction = mFragmentManager.beginTransaction();
            transaction.remove(mRegisterFragment);
            transaction.commitAllowingStateLoss();

            mFragmentList.remove(mRegisterFragment);
            mRegisterFragment = null;
        }

        if (mActionBarText != null) {
            mActionBar.setDisplayHomeAsUpEnabled(false);
            String actionBarText = mActionBarText.getText().toString();
            if (mCurType == DataConst.FRAGMENT_TYPE.LOST) {
                actionBarText = "분실물";
            } else if (mCurType == DataConst.FRAGMENT_TYPE.PICK) {
                actionBarText = "습득물";
            }
            mActionBarText.setText(actionBarText);
            mActionBarImage.setVisibility(View.VISIBLE);
        }
        visibilityActionBar(true, false);
    }

    public void showDetailFragment(Bundle bundle) {
        if (mFragmentManager == null) {
            mFragmentManager = getSupportFragmentManager();
        }

        mDetailFragment = new PopupDetail();

        if (bundle == null) {
            bundle = new Bundle();
        }

        int curType = bundle.getInt(DataConst.KEY.POPUP_TYPE, -1);
        if (curType == -1) {
            bundle.putInt(DataConst.KEY.POPUP_TYPE, mCurType);
        }
        mDetailFragment.setArguments(bundle);

        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        transaction.replace(mFullFrameLayout.getId(), mDetailFragment);
        transaction.commitAllowingStateLoss();

        mFragmentList.add(mDetailFragment);

        if (mActionBarText != null) {
            mActionBar.setDisplayHomeAsUpEnabled(true);
            mActionBarText.setText("");
            mActionBarImage.setVisibility(View.GONE);
        }

        boolean isRemoveShow = false;
        Object item = bundle.getSerializable(DataConst.KEY.ITEM_DATA);
        if (item instanceof ListResult.ResultData) {
            ListResult.ResultData data = (ListResult.ResultData) item;
            String writerIdx = "" + data.userId;
            String userIdx = SharedPrefUtil.getInstance().get(DataConst.KEY.SP_USER_INDEX, "");

            isRemoveShow = writerIdx.equalsIgnoreCase(userIdx);
        }

        MenuItem menuItem = mMenu.findItem(R.id.action_menu_complete);
        if (menuItem != null) {
            String completeTitle = curType == DataConst.FRAGMENT_TYPE.LOST ? "습득완료" : "전달완료";
            menuItem.setTitle(completeTitle);
        }

        visibilityActionBar(false, isRemoveShow);
    }

    public void hideDetailFragment(boolean moveToChat, ChatRoomDTO chatRoomDTO) {
        if (mFragmentManager == null) {
            mFragmentManager = getSupportFragmentManager();
        }

        if (mDetailFragment != null) {
            FragmentTransaction transaction = mFragmentManager.beginTransaction();
            transaction.remove(mDetailFragment);
            transaction.commitAllowingStateLoss();

            mFragmentList.remove(mDetailFragment);
            mDetailFragment = null;
        }

        boolean needActionBar = false;
        if (mActionBarText != null) {
            mActionBar.setDisplayHomeAsUpEnabled(false);
            String actionBarText = mActionBarText.getText().toString();
            if (mCurType == DataConst.FRAGMENT_TYPE.LOST) {
                actionBarText = "분실물";
                needActionBar = true;
            } else if (mCurType == DataConst.FRAGMENT_TYPE.PICK) {
                actionBarText = "습득물";
                needActionBar = true;
            } else if (mCurType == DataConst.FRAGMENT_TYPE.HOME) {
                actionBarText = "금도끼";
                needActionBar = false;
            } else if (mCurType == DataConst.FRAGMENT_TYPE.MYPAGE) {
                actionBarText = "마이페이지";
                needActionBar = false;
            }
            mActionBarText.setText(actionBarText);
            mActionBarImage.setVisibility(View.VISIBLE);
        }
        visibilityActionBar(needActionBar, false);

        if (moveToChat && chatRoomDTO != null) {
            showChatTabFromDetail(chatRoomDTO);
        }
    }

    public void showChatDetailFragment(ChatRoomData roomData) {
        if (mFragmentManager == null) {
            mFragmentManager = getSupportFragmentManager();
        }

        mChatDetailFragment = new PopupChat();

        Bundle bundle = new Bundle();
        bundle.putSerializable(DataConst.KEY.CHAT_ROOM_DATA, roomData);
        mChatDetailFragment.setArguments(bundle);

        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        transaction.replace(mFullFrameLayout.getId(), mChatDetailFragment);
        transaction.commitAllowingStateLoss();

        mFragmentList.add(mChatDetailFragment);

        if (mActionBarText != null) {
            mActionBar.setDisplayHomeAsUpEnabled(true);

            String targetUserName = Utils.getChatUsername(roomData.value);

            mActionBarText.setText(targetUserName);
            mActionBarImage.setVisibility(View.GONE);
        }
        visibilityActionBar(false, false);
    }

    public void hideChatDetailFragment() {
        if (mFragmentManager == null) {
            mFragmentManager = getSupportFragmentManager();
        }

        if (mChatDetailFragment != null) {
            FragmentTransaction transaction = mFragmentManager.beginTransaction();
            transaction.remove(mChatDetailFragment);
            transaction.commitAllowingStateLoss();

            mFragmentList.remove(mChatDetailFragment);
            mChatDetailFragment = null;
        }

        boolean needActionBar = false;
        if (mActionBarText != null) {
            mActionBar.setDisplayHomeAsUpEnabled(false);
            String actionBarText = mActionBarText.getText().toString();
            if (mCurType == DataConst.FRAGMENT_TYPE.LOST) {
                actionBarText = "분실물";
                needActionBar = true;
            } else if (mCurType == DataConst.FRAGMENT_TYPE.PICK) {
                actionBarText = "습득물";
                needActionBar = true;
            } else if (mCurType == DataConst.FRAGMENT_TYPE.HOME) {
                actionBarText = "금도끼";
                needActionBar = false;
            } else if (mCurType == DataConst.FRAGMENT_TYPE.MYPAGE) {
                actionBarText = "마이페이지";
                needActionBar = false;
            } else if (mCurType == DataConst.FRAGMENT_TYPE.CHAT) {
                actionBarText = "채팅";
                needActionBar = false;
            }
            mActionBarText.setText(actionBarText);
            mActionBarImage.setVisibility(View.VISIBLE);
        }
        visibilityActionBar(needActionBar, false);
    }

    public void showMyListFragment(@DataConst.FRAGMENT_TYPE int curType, ArrayList<ListResult.ResultData> itemList, UserData userData) {
        if (mFragmentManager == null) {
            mFragmentManager = getSupportFragmentManager();
        }

        mMyListFragment = new PopupMyList();

        Bundle bundle = new Bundle();
        bundle.putInt(DataConst.KEY.POPUP_TYPE, curType);
        bundle.putSerializable(DataConst.KEY.ITEM_DATA, itemList);
        bundle.putSerializable(DataConst.KEY.MY_DATA, userData);
        mMyListFragment.setArguments(bundle);

        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        transaction.replace(mFullFrameLayout.getId(), mMyListFragment);
        transaction.commitAllowingStateLoss();

        mFragmentList.add(mMyListFragment);

        if (mActionBarText != null) {
            mActionBar.setDisplayHomeAsUpEnabled(true);

            String title = curType == DataConst.FRAGMENT_TYPE.LOST ? "내가 올린 분실물" : "내가 올린 습득물";
            mActionBarText.setText(title);
            mActionBarImage.setVisibility(View.GONE);
        }
        visibilityActionBar(false, false);
    }

    public void hideMyListFragment() {
        if (mFragmentManager == null) {
            mFragmentManager = getSupportFragmentManager();
        }

        if (mMyListFragment != null) {
            FragmentTransaction transaction = mFragmentManager.beginTransaction();
            transaction.remove(mMyListFragment);
            transaction.commitAllowingStateLoss();

            mFragmentList.remove(mMyListFragment);
            mMyListFragment = null;
        }

        boolean needActionBar = false;
        if (mActionBarText != null) {
            mActionBar.setDisplayHomeAsUpEnabled(false);
            String actionBarText = mActionBarText.getText().toString();
            if (mCurType == DataConst.FRAGMENT_TYPE.LOST) {
                actionBarText = "분실물";
                needActionBar = true;
            } else if (mCurType == DataConst.FRAGMENT_TYPE.PICK) {
                actionBarText = "습득물";
                needActionBar = true;
            } else if (mCurType == DataConst.FRAGMENT_TYPE.HOME) {
                actionBarText = "금도끼";
                needActionBar = false;
            } else if (mCurType == DataConst.FRAGMENT_TYPE.MYPAGE) {
                actionBarText = "마이페이지";
                needActionBar = false;
            }
            mActionBarText.setText(actionBarText);
            mActionBarImage.setVisibility(View.VISIBLE);
        }
        visibilityActionBar(needActionBar, false);
    }

    /**
     * 받은 아이템 리스트를 통해 리로드 처리
     * @param itemList ArrayList<ListResult.ResultData>
     */
    public void reloadItemList(ArrayList<ListResult.ResultData> itemList) {
        if (mFragment instanceof LostFragment) {
            ((LostFragment) mFragment).initLayout(itemList);
        } else if (mFragment instanceof PickFragment) {
            ((PickFragment) mFragment).initLayout(itemList);
        }
    }

    /**
     * 해당 화면 초기 진입처럼 리로드 처리
     */
    public void reloadItemList() {
        if (mFragment instanceof LostFragment) {
            ((LostFragment) mFragment).requestData();
        } else if (mFragment instanceof PickFragment) {
            ((PickFragment) mFragment).requestData();
        }
    }

    private View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int viewId = view.getId();

            hideSearchPopup();

            int targetType = mCurType;
            if (viewId == mHomeLayout.getId()) {
                targetType = DataConst.FRAGMENT_TYPE.HOME;
                hideSearchPopup();
            } else if (viewId == mLostLayout.getId()) {
                targetType = DataConst.FRAGMENT_TYPE.LOST;
                showSearchPopup(targetType);
            } else if (viewId == mPickLayout.getId()) {
                targetType = DataConst.FRAGMENT_TYPE.PICK;
                showSearchPopup(targetType);
            } else if (viewId == mChatLayout.getId()) {
                targetType = DataConst.FRAGMENT_TYPE.CHAT;
                hideSearchPopup();
            } else if (viewId == mMyPageLayout.getId()) {
                targetType = DataConst.FRAGMENT_TYPE.MYPAGE;
                hideSearchPopup();
            } else {
                Utils.showToast("알 수 없는 공간을 선택하였습니다.");
                hideSearchPopup();
            }

            changeTab(targetType);
        }
    };

    private void changeTab(@DataConst.FRAGMENT_TYPE int type) {
        if (mCurType == type) {
            Log.d(TAG, "changeTab() called. same type ! do nothing !");
            return;
        }

        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        Fragment targetFragment = null;
        ViewGroup targetLayout = null;
        boolean isShow = false;
        String actionBarText = getString(R.string.app_name);
        switch (type) {
            case DataConst.FRAGMENT_TYPE.HOME:
                targetFragment = new HomeFragment();
                targetLayout = mHomeLayout;
                break;
            case DataConst.FRAGMENT_TYPE.LOST:
                targetFragment = new LostFragment();
                targetLayout = mLostLayout;
                isShow = true;
                actionBarText = "분실물";
                break;
            case DataConst.FRAGMENT_TYPE.PICK:
                targetFragment = new PickFragment();
                targetLayout = mPickLayout;
                isShow = true;
                actionBarText = "습득물";
                break;
            case DataConst.FRAGMENT_TYPE.CHAT:
                targetFragment = new ChatFragment();
                targetLayout = mChatLayout;
                actionBarText = "채팅";
                break;
            case DataConst.FRAGMENT_TYPE.MYPAGE:
                targetFragment = new MyPageFragment();
                targetLayout = mMyPageLayout;
                actionBarText = "마이페이지";
                break;
        }

        if (targetFragment != null) {
            for (ViewGroup layout : mBottomLayoutList) {
                if (layout.getId() == targetLayout.getId()) {
                    layout.setSelected(true);
                } else {
                    layout.setSelected(false);
                }
            }
            mFragment = targetFragment;
            transaction.replace(mFrameLayout.getId(), targetFragment);
            transaction.commitAllowingStateLoss();

            mCurType = type;
            if (mActionBarText != null) {
                mActionBarText.setText(actionBarText);
                mActionBarImage.setVisibility(View.VISIBLE);
            }
            visibilityActionBar(isShow, false);
        }
    }

    public void showChatTabFromDetail(ChatRoomDTO chatRoomDTO) {
        Log.d(TAG, "showChatTabFromDetail() called. | " + chatRoomDTO);
        hideSearchPopup();

        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        Fragment chatFragment = new ChatFragment();
        String actionBarText = "채팅";

        for (ViewGroup layout : mBottomLayoutList) {
            if (layout.getId() == mChatLayout.getId()) {
                layout.setSelected(true);
            } else {
                layout.setSelected(false);
            }
        }

        Bundle bundle = new Bundle();
        bundle.putSerializable(ChatConst.CHAT_ROOT_DATA, chatRoomDTO);
        chatFragment.setArguments(bundle);

        mFragment = chatFragment;
        transaction.replace(mFrameLayout.getId(), chatFragment);
        transaction.commitAllowingStateLoss();

        mCurType = DataConst.FRAGMENT_TYPE.CHAT;
        if (mActionBarText != null) {
            mActionBarText.setText(actionBarText);
            mActionBarImage.setVisibility(View.VISIBLE);
        }

        visibilityActionBar(false, false);
    }

    private ViewGroup getLayoutFromType(@DataConst.FRAGMENT_TYPE int type) {
        ViewGroup viewGroup = mHomeLayout;
        switch (type) {
            case DataConst.FRAGMENT_TYPE.HOME:
                viewGroup = mHomeLayout;
                break;
            case DataConst.FRAGMENT_TYPE.LOST:
                viewGroup = mLostLayout;
                break;
            case DataConst.FRAGMENT_TYPE.PICK:
                viewGroup = mPickLayout;
                break;
            case DataConst.FRAGMENT_TYPE.CHAT:
                viewGroup = mChatLayout;
                break;
            case DataConst.FRAGMENT_TYPE.MYPAGE:
                viewGroup = mMyPageLayout;
                break;
        }
        return viewGroup;
    }

    private void visibilityActionBar(boolean isWriteShow, boolean isDotShow) {
        if (mMenu == null) {
            return;
        }
        if (isWriteShow) {
            mMenu.findItem(R.id.action_menu_add).setVisible(true);
            mMenu.findItem(R.id.action_menu_remove).setVisible(false);
            mMenu.findItem(R.id.action_menu_complete).setVisible(false);
        } else {
            mMenu.findItem(R.id.action_menu_add).setVisible(false);

            mMenu.findItem(R.id.action_menu_remove).setVisible(isDotShow);
            mMenu.findItem(R.id.action_menu_complete).setVisible(isDotShow);
        }
    }

    public void moveToTab(int targetTab) {
        ViewGroup viewGroup = getLayoutFromType(targetTab);

        viewGroup.performClick();
    }

    public void updateLastChat(String roomName, String lastChat) {
        if (mFragment instanceof ChatFragment) {
            ((ChatFragment) mFragment).updateLastChat(roomName, lastChat);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        mMenu = menu;
        visibilityActionBar(false, false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // 팝업 유무 상관 없이 글쓰기 버튼 동작 처리
        if (item.getItemId() == R.id.action_menu_add) {
            if (!mFragmentList.contains(mRegisterFragment)) {
                showRegisterFragment(mCurType);
            }
            return true;
        }

        if (!Utils.isListEmpty(mFragmentList)) {
            Fragment curFragment = mFragmentList.get(mFragmentList.size() - 1);
            Log.d(TAG, "onOptionsItemSelected() curFragment: " + curFragment.getClass().getSimpleName());

            switch (item.getItemId()) {
                case android.R.id.home:
                    if (curFragment instanceof PopupRegister) {
                        hideRegisterFragment();
                    } else if (curFragment instanceof PopupDetail) {
                        hideDetailFragment(false, null);
                    } else if (curFragment instanceof PopupMyList) {
                        hideMyListFragment();
                    } else if (curFragment instanceof PopupChat) {
                        hideChatDetailFragment();
                    }
                    break;
                case R.id.action_menu_remove:
                    if (curFragment instanceof PopupDetail) {
                        ((PopupDetail) curFragment).removeItem();
                    }
                    return true;
                case R.id.action_menu_complete:
                    if (curFragment instanceof PopupDetail) {
                        ((PopupDetail) curFragment).completeItem();
                    }
                    return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (Utils.isListEmpty(mFragmentList)) {
            super.onBackPressed();
        } else {
            Fragment curFragment = mFragmentList.get(mFragmentList.size() - 1);
            if (curFragment instanceof PopupRegister) {
                hideRegisterFragment();
            } else if (curFragment instanceof PopupDetail) {
                hideDetailFragment(false, null);
            } else if (curFragment instanceof PopupMyList) {
                hideMyListFragment();
            } else if (curFragment instanceof PopupChat) {
                hideChatDetailFragment();
            }
        }
    }
}
