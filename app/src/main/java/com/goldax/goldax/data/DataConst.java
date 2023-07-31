package com.goldax.goldax.data;

import androidx.annotation.IntDef;
import androidx.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class DataConst {

    public static final int REQUEST_CODE_SELECT_IMAGE = 5000;
    public static final int REQUEST_CODE_CROP_IMAGE = 5001;
    public static final String FILE_PREFIX = "temp";
    public static final String FILE_SUFFIX = ".jpeg";

    public static final int VIEW_TYPE_DUMMY = 3000;
    public static final int VIEW_TYPE_ITEM = 3001;

    public static final int CHAT_VIEW_TYPE_MY = 4000;
    public static final int CHAT_VIEW_TYPE_TARGET = 4001;

    public static final String CHAT_LAST_CHAT_KEY = "CHAT_LAST_CHAT_KEY";

    // 각종 키값 정의
    @StringDef({
            KEY.SP_LOGIN_TOKEN,
            KEY.SP_LOGIN_NAME,
            KEY.POPUP_TYPE,
            KEY.ITEM_DATA,
            KEY.MY_DATA,
            KEY.CHAT_ROOM_DATA
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface KEY {
        String SP_LOGIN_TOKEN = "SP_LOGIN_TOKEN"; // SharedPreferences 키
        String SP_LOGIN_NAME = "SP_LOGIN"; // SharedPreferences 키
        String SP_USER_INDEX = "SP_USER_INDEX"; // SharedPreferences 키
        String POPUP_TYPE = "POPUP_TYPE"; // Popup type Bundle 키
        String ITEM_DATA = "ITEM_DATA";
        String MY_DATA = "MY_DATA";
        String CHAT_ROOM_DATA = "CHAT_ROOM_DATA";
    }

    // 하단 버튼 Fragment type 정의
    @IntDef({FRAGMENT_TYPE.HOME
            , FRAGMENT_TYPE.LOST
            , FRAGMENT_TYPE.PICK
            , FRAGMENT_TYPE.CHAT
            , FRAGMENT_TYPE.MYPAGE
    })
    public @interface FRAGMENT_TYPE {
        int HOME = 1000;
        int LOST = 1001;
        int PICK = 1002;
        int CHAT = 1003;
        int MYPAGE = 1004;
    }
}
