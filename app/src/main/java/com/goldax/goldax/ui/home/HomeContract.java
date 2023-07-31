package com.goldax.goldax.ui.home;

import com.goldax.goldax.data.ListResult;

import java.util.ArrayList;

public class HomeContract {
    public interface View {
        void initPickLayout(ArrayList<ListResult.ResultData> list);

        void initLostLayout(ArrayList<ListResult.ResultData> list);

        void requestData();
    }

    public interface Presenter {
        void requestPickData();

        void requestLostData();

        void setPickData(ArrayList<ListResult.ResultData> list);

        void setLostData(ArrayList<ListResult.ResultData> list);
    }
}
