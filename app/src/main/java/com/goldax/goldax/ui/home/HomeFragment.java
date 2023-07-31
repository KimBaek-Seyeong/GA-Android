package com.goldax.goldax.ui.home;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
import com.goldax.goldax.ui.lost.recycler.LostAdapter;
import com.goldax.goldax.ui.pick.recycler.PickAdapter;
import com.goldax.goldax.util.Utils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.goldax.goldax.data.DataConst.VIEW_TYPE_DUMMY;


public class HomeFragment extends Fragment implements HomeContract.View {
    private static final String TAG = HomeFragment.class.getSimpleName();

    private HomeContract.Presenter mPresenter;
    private HomeContract.View mView;

    @BindView(R.id.f_home_guide_text)
    public TextView mGuideText;
    @BindView(R.id.f_home_pick_recycler)
    public RecyclerView mPickRecycler;
    @BindView(R.id.f_home_lost_recycler)
    public RecyclerView mLostRecycler;

    private LostAdapter mLostAdapter;
    private GridLayoutManager mLostLayoutManager;

    private PickAdapter mPickAdapter;
    private GridLayoutManager mPickLayoutManager;

    private boolean mCompleteLostData; // 분실물 서버 조회 완료 유무
    private boolean mCompletePickData; // 습득물 서버 조회 완료 유무

    private int mCompleteCount;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.f_home, container, false);
        ButterKnife.bind(this, viewGroup);

        return viewGroup;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mView = this;
        mPresenter = new HomePresenter(this);

        mCompleteCount = 0;

        requestData();
    }

    @Override
    public void initPickLayout(ArrayList<ListResult.ResultData> list) {
        if (Utils.isListEmpty(list)) {
            // 리스트가 비어있는 경우 별도 UI 처리 하지 않음
            return;
        }

        ArrayList<ListResult.ResultData> filterList = new ArrayList<>();
        for (ListResult.ResultData item : list) {
            if (item.isClosed || item.isDeleted) {
                if (item.isClosed) {
                    mCompleteCount++;
                }
                continue;
            }
            filterList.add(item);

            if (filterList.size() >= 9) {
                break;
            }
        }

        mPickAdapter = new PickAdapter(filterList, mItemPickClickListener);
        mPickAdapter.setFromHome(true);
        mPickLayoutManager = new GridLayoutManager(ApplicationController.getInstance(), 1, RecyclerView.HORIZONTAL, false);

        mPickRecycler.setAdapter(mPickAdapter);
        mPickRecycler.setLayoutManager(mPickLayoutManager);

        mCompletePickData = true;
        if (mCompletePickData && mCompleteLostData) {
            initGuideText();
        }
    }

    @Override
    public void initLostLayout(ArrayList<ListResult.ResultData> list) {
        if (Utils.isListEmpty(list)) {
            // 리스트가 비어있는 경우 별도 UI 처리 하지 않음
            return;
        }

        ArrayList<ListResult.ResultData> filterList = new ArrayList<>();
        for (ListResult.ResultData item : list) {
            if (item.isClosed || item.isDeleted) {
                if (item.isClosed) {
                    mCompleteCount++;
                }
                continue;
            }
            filterList.add(item);

            if (filterList.size() >= 9) {
                break;
            }
        }

        mLostAdapter = new LostAdapter(filterList, mItemLostClickListener);
        mLostAdapter.setFromHome(true);
        mLostLayoutManager = new GridLayoutManager(ApplicationController.getInstance(), 1, RecyclerView.HORIZONTAL, false);

        mLostRecycler.setAdapter(mLostAdapter);
        mLostRecycler.setLayoutManager(mLostLayoutManager);

        mCompleteLostData = true;
        if (mCompletePickData && mCompleteLostData) {
            initGuideText();
        }
    }

    @Override
    public void requestData() {
        Log.d(TAG, "requestData() called.");

        mPresenter.requestPickData();
        mPresenter.requestLostData();
    }

    private void initGuideText() {
        if (mLostAdapter != null && mPickAdapter != null) {
            mGuideText.setText(getString(R.string.home_guide_text, mCompleteCount));
        }
    }

    private View.OnClickListener mItemLostClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Activity activity = getActivity();
            if (activity instanceof MainActivity) {
                int pos = (int) v.getTag(R.attr.key_image_pos);
                int viewType = mLostAdapter.getItemViewType(pos);

                if (viewType == VIEW_TYPE_DUMMY) {
                    int landingType = (int) v.getTag(R.attr.key_landing_type);
                    ((MainActivity) activity).moveToTab(landingType);
                } else {
                    Object item = v.getTag();

                    Bundle bundle = new Bundle();
                    if (item instanceof ListResult.ResultData) {
                        bundle.putSerializable(DataConst.KEY.ITEM_DATA, (ListResult.ResultData) item);
                        bundle.putInt(DataConst.KEY.POPUP_TYPE, DataConst.FRAGMENT_TYPE.LOST);
                    }

                    ((MainActivity) activity).showDetailFragment(bundle);
                }
            }
        }
    };

    private View.OnClickListener mItemPickClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Activity activity = getActivity();
            if (activity instanceof MainActivity) {
                int pos = (int) v.getTag(R.attr.key_image_pos);
                int viewType = mPickAdapter.getItemViewType(pos);

                if (viewType == VIEW_TYPE_DUMMY) {
                    int landingType = (int) v.getTag(R.attr.key_landing_type);
                    ((MainActivity) activity).moveToTab(landingType);
                } else {
                    Object item = v.getTag();

                    Bundle bundle = new Bundle();
                    if (item instanceof ListResult.ResultData) {
                        bundle.putSerializable(DataConst.KEY.ITEM_DATA, (ListResult.ResultData) item);
                        bundle.putInt(DataConst.KEY.POPUP_TYPE, DataConst.FRAGMENT_TYPE.PICK);
                    }

                    ((MainActivity) activity).showDetailFragment(bundle);
                }
            }
        }
    };
}
