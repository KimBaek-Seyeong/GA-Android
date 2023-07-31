package com.goldax.goldax.ui.lost;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
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
import com.goldax.goldax.data.DataConst;
import com.goldax.goldax.data.ListResult;
import com.goldax.goldax.ui.home.HomeFragment;
import com.goldax.goldax.ui.lost.recycler.LostAdapter;
import com.goldax.goldax.util.Utils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LostFragment extends Fragment implements LostContract.View {
    private static final String TAG = HomeFragment.class.getSimpleName();

    @BindView(R.id.f_board_recyclerview)
    public RecyclerView mRecyclerView;

    private LostAdapter mAdapter;
    private GridLayoutManager mLayoutManager;

    private LostContract.Presenter mPresenter;
    private LostContract.View mView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.f_board, container, false);
        ButterKnife.bind(this, viewGroup);

        return viewGroup;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mView = this;
        mPresenter = new LostPresenter(this);

        requestData();
    }

    @Override
    public void initLayout(ArrayList<ListResult.ResultData> list) {
        if (Utils.isListEmpty(list)) {
            // 리스트가 비어있는 경우 별도 UI 처리 하지 않음
            Utils.showToast("게시판이 비어있습니다.");
            return;
        }

        mAdapter = new LostAdapter(list, mItemClickListener);
        mLayoutManager = new GridLayoutManager(ApplicationController.getInstance(), 3, RecyclerView.VERTICAL, false);

        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(mLayoutManager);
    }

    @Override
    public void requestData() {
        mPresenter.requestData();
    }

    private View.OnClickListener mItemClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Activity activity = getActivity();
            if (activity instanceof MainActivity) {
                Object item = v.getTag();

                Bundle bundle = new Bundle();
                if (item instanceof ListResult.ResultData) {
                    bundle.putSerializable(DataConst.KEY.ITEM_DATA, (ListResult.ResultData) item);
                    bundle.putInt(DataConst.KEY.POPUP_TYPE, DataConst.FRAGMENT_TYPE.LOST);
                }

                ((MainActivity) activity).showDetailFragment(bundle);
            }
        }
    };
}
