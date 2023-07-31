package com.goldax.goldax.ui.popup;

import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.goldax.goldax.MainActivity;
import com.goldax.goldax.R;
import com.goldax.goldax.application.ApplicationController;
import com.goldax.goldax.data.DataConst;
import com.goldax.goldax.ui.chat.DTO.ChatDTO;
import com.goldax.goldax.ui.chat.recycler.ChatRoomData;
import com.goldax.goldax.ui.popup.chat.MessageAdapter;
import com.goldax.goldax.ui.popup.chat.MessageData;
import com.goldax.goldax.util.SharedPrefUtil;
import com.goldax.goldax.util.Utils;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PopupChat extends Fragment {
    private static final String TAG = PopupChat.class.getSimpleName();

    @BindView(R.id.popup_chat_detail_reyclerview)
    public RecyclerView mRecyclerView;
    @BindView(R.id.popup_chat_detail_send_edit)
    public EditText mEditText;
    @BindView(R.id.popup_chat_detail_send_btn)
    public ImageButton mSendBtn;

    private MainActivity mMainActivity;

    private @DataConst.FRAGMENT_TYPE int mCurType;
    ChatRoomData mRoomData;

    private FirebaseDatabase mDatabase;
    private DatabaseReference mDatabaseReference;
    private DatabaseReference mDatabaseRoomReference;
    private ArrayList<MessageData> mMessageList;

    private int mMyId;
    private String mMyName;

    private MessageAdapter mMessageAdapter;
    private LinearLayoutManager mMessageLayoutManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.popup_chat_detail, container, false);
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
            mRoomData = (ChatRoomData) getArguments().getSerializable(DataConst.KEY.CHAT_ROOM_DATA);
            // TODO 채팅 상대방 정보 넘겨받아야함.
        }

        mMyId = -1;
        try {
            String myIdStr = SharedPrefUtil.getInstance().get(DataConst.KEY.SP_USER_INDEX, "-1");
            mMyId = Integer.parseInt(myIdStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mMyName = SharedPrefUtil.getInstance().get(DataConst.KEY.SP_LOGIN_NAME, "CAN_NOT_EXCESS_NAME");

        if (mMyId == -1 || mMyName.equals("CAN_NOT_EXCESS_NAME")) {
            mMainActivity.hideChatDetailFragment();
        }

        mMessageList = new ArrayList<>();

        initDatabase();
        initLayout();
    }

    private void initDatabase() {
        mDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mDatabase.getReference(mRoomData.key);
        mDatabaseRoomReference = mDatabase.getReference();

        mDatabaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String key = dataSnapshot.getKey();

                if (DataConst.CHAT_LAST_CHAT_KEY.equalsIgnoreCase(key)) {
                    // do nothing.
                } else {
                    ChatDTO value = dataSnapshot.getValue(ChatDTO.class);
                    addMessageList(value);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void initLayout() {
        mMessageAdapter = new MessageAdapter(mMessageList, mRoomData);
        mMessageLayoutManager = new LinearLayoutManager(ApplicationController.getInstance(), RecyclerView.VERTICAL, false);

        mRecyclerView.setAdapter(mMessageAdapter);
        mRecyclerView.setLayoutManager(mMessageLayoutManager);
        mRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.top = Utils.changeDP2Pixel(5);
            }
        });

        mSendBtn.setOnClickListener(mClickListener);
    }

    private View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            try {
                if (mEditText.getText().toString().equals("")) {
                    return;
                }

                String message = mEditText.getText().toString();
                ChatDTO chatDTO = new ChatDTO(mMyId, mMyName, message);
                mDatabaseReference.push().setValue(chatDTO);
                mEditText.setText("");

                // 부모 레퍼런스에 마지막 채팅 내역 추가
                mDatabaseRoomReference.child(mRoomData.key).child(DataConst.CHAT_LAST_CHAT_KEY).setValue(message);

                // 채팅 목록의 마지막 채팅 노출 텍스트 변경 처리
                mMainActivity.updateLastChat(mRoomData.key, message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private void addMessageList(ChatDTO value) {
        int userId = value.getUserId();
        String userName = value.getUserName();
        String message = value.getMessage();

        MessageData messageData = new MessageData();
        messageData.userName = userName;
        messageData.message = message;
        messageData.isMy = userId == mMyId && userName.equalsIgnoreCase(mMyName);

        mMessageList.add(messageData);
        mMessageAdapter.notifyItemInserted(mMessageAdapter.getItemCount() - 1);
        mRecyclerView.scrollToPosition(mMessageAdapter.getItemCount() - 1);
    }

}
