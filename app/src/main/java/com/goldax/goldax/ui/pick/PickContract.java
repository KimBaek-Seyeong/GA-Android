package com.goldax.goldax.ui.pick;

import com.goldax.goldax.data.ListResult;

import java.util.ArrayList;

public class PickContract {
    public interface View {
        // 레이아웃 초기화
        void initLayout(ArrayList<ListResult.ResultData> list);

        // data request
        void requestData();
    }

    public interface Presenter {
        void requestData();

        void setInitData(ArrayList<ListResult.ResultData> list);
    }
}
