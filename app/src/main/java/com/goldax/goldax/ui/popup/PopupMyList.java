package com.goldax.goldax.ui.popup;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.goldax.goldax.MainActivity;
import com.goldax.goldax.R;
import com.goldax.goldax.application.ApplicationController;
import com.goldax.goldax.base.MainBaseAdapter;
import com.goldax.goldax.data.DataConst;
import com.goldax.goldax.data.ListResult;
import com.goldax.goldax.data.UserData;
import com.goldax.goldax.ui.lost.recycler.LostAdapter;
import com.goldax.goldax.ui.pick.recycler.PickAdapter;
import com.goldax.goldax.util.Utils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PopupMyList extends Fragment {
    private static final String TAG = PopupMyList.class.getSimpleName();

    private MainActivity mMainActivity;

    @BindView(R.id.popup_mylist_recyclerview)
    public RecyclerView mRecyclerView;

    private @DataConst.FRAGMENT_TYPE int mCurType;
    private ArrayList<ListResult.ResultData> mItemList;
    private UserData mUserData;

    private MainBaseAdapter mAdapter;
    private GridLayoutManager mLayoutManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.popup_mylist, container, false);
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
            mItemList = (ArrayList<ListResult.ResultData>) getArguments().getSerializable(DataConst.KEY.ITEM_DATA);
            mUserData = (UserData) getArguments().getSerializable(DataConst.KEY.MY_DATA);
        }

        initLayout();
    }

    private void initLayout() {
        if (Utils.isListEmpty(mItemList)) {
            Utils.showToast("게시판이 비어있습니다.");
            mMainActivity.hideSearchPopup();
            return;
        }

        if (mCurType == DataConst.FRAGMENT_TYPE.LOST) {
            mAdapter = new LostAdapter(mItemList, mItemClickListener);
        } else {
            mAdapter = new PickAdapter(mItemList, mItemClickListener);
        }
        mLayoutManager = new GridLayoutManager(ApplicationController.getInstance(), 3, RecyclerView.VERTICAL, false);

        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(mLayoutManager);
    }

    private View.OnClickListener mItemClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Object item = view.getTag();

            Bundle bundle = new Bundle();
            if (item instanceof ListResult.ResultData) {
                bundle.putSerializable(DataConst.KEY.ITEM_DATA, (ListResult.ResultData) item);
                bundle.putSerializable(DataConst.KEY.MY_DATA, mUserData);
                bundle.putInt(DataConst.KEY.POPUP_TYPE, mCurType);
            }

            mMainActivity.showDetailFragment(bundle);
        }
    };

}
