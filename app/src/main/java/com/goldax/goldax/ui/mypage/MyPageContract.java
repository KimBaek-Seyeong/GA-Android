package com.goldax.goldax.ui.mypage;

import com.goldax.goldax.data.ListResult;
import com.goldax.goldax.data.UserData;

import java.util.ArrayList;

public class MyPageContract {
    public interface View {
        // 레이아웃 초기화
        void initLayout(UserData data);

        // data request
        void requestData();

        void setProfileImage(String filePath);

        void showPickData(ArrayList<ListResult.ResultData> itemList);

        void showLostData(ArrayList<ListResult.ResultData> itemList);
    }

    public interface Presenter {
        void requestData();

        void setInitData(UserData data);

        void changeProfileImage(String filePath);

        void setProfileImage(String filePath);

        void requestPickData();

        void setPickData(ArrayList<ListResult.ResultData> itemList);

        void requestLostData();

        void setLostData(ArrayList<ListResult.ResultData> itemList);

        UserData getMyData();
    }
}
