package com.goldax.goldax.ui.chat;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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
import com.goldax.goldax.data.UserData;
import com.goldax.goldax.ui.chat.DTO.ChatRoomDTO;
import com.goldax.goldax.ui.chat.recycler.ChatAdapter;
import com.goldax.goldax.ui.chat.recycler.ChatRoomData;
import com.goldax.goldax.ui.chat.recycler.ChatViewHolder;
import com.goldax.goldax.util.SharedPrefUtil;
import com.goldax.goldax.util.Utils;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChatFragment extends Fragment implements ChatContract.View {
    private static final String TAG = ChatFragment.class.getSimpleName();

    private ChatContract.Presenter mPresenter;
    private ChatContract.View mView;

    @BindView(R.id.f_chat_recyclerview)
    public RecyclerView mChatRecyclerView;

    private ChatAdapter mChatAdapter;
    private GridLayoutManager mChatLayoutManager;

    private FirebaseDatabase mDatabase;
    private DatabaseReference mDatabaseReference;
    private ArrayList<ChatRoomData> mChatList;

    // 채팅방 바로 진입을 위한 데이터
    private ChatRoomDTO mInputDTO;

    private boolean mIsAlreadyProcess; // 상세보기 -> 바로 채팅탭 진입 시 진입 완료 여부 플래그
    private boolean mAfterCallback; // 콜백을 받은 이후에 동작하기 위한 플래그
    private long mMyRoomSize; // 내가 그릴 채팅방을 다 그렸는지 체크를 위한 value

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.f_chat, container, false);
        ButterKnife.bind(this, viewGroup);

        return viewGroup;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Bundle bundle = getArguments();
        if (bundle != null) {
            mInputDTO = (ChatRoomDTO) bundle.getSerializable(ChatConst.CHAT_ROOT_DATA);
        }

        mView = this;
        mPresenter = new ChatPresenter(this);

        mChatList = new ArrayList<ChatRoomData>();

        // 유저 정보 저장.
        int myId = -1;
        try {
            String myIdStr = SharedPrefUtil.getInstance().get(DataConst.KEY.SP_USER_INDEX, "-1");
            myId = Integer.parseInt(myIdStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String myName = SharedPrefUtil.getInstance().get(DataConst.KEY.SP_LOGIN_NAME, "");
        mPresenter.setUserData(myId, myName);

        initChatList();
        initDatabase();
    }

    private void initDatabase() {
        mDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mDatabase.getReference();

        // 최초 리스트 구성 시 마지막 채팅 내역 노출하기 위한 리스너
        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange() called. | child count: " + dataSnapshot.getChildrenCount() );

                if (dataSnapshot.getChildrenCount() == 0 && mInputDTO != null) {
                    Log.d(TAG, "onDataChange() | Global first input chatroom !");
                    mAfterCallback = true;
                    openChat(mInputDTO);
                    return;
                }

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Log.d(TAG, "onDataChange() called. " + snapshot.getKey());
                    String key = snapshot.getKey();
                    String lastChat = "";

                    // 내가 그릴 방의 갯수 체크
                    int myId = mPresenter.getUserData().id;
                    String myName = mPresenter.getUserData().name;
                    String parseMyData = myId + "^" + myName;
                    if (key.contains(parseMyData)) {
                        mMyRoomSize++;
                    }

                    ChatViewHolder holder = getViewHolderFromRoomName(key);
                    if (holder == null) {
                        continue;
                    }

                    for (DataSnapshot childSanpshot : snapshot.getChildren()) {
                        if (DataConst.CHAT_LAST_CHAT_KEY.equalsIgnoreCase(childSanpshot.getKey())) {
                            lastChat = childSanpshot.getValue().toString();
                            String holderText = holder.mMessageText.getText().toString();

                            Log.d(TAG, "onDataChange() | " + key + " | " + lastChat + " | " + holderText);

                            if (!lastChat.equalsIgnoreCase(holderText)) {
                                holder.mMessageText.setText(lastChat);
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mDatabaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                // 리스트의 아이템을 검색하거나 추가되었을 때 수신
                Log.d(TAG, "onChildAdded() called. " + dataSnapshot.getKey() + " | " + dataSnapshot.getValue());
                String key = dataSnapshot.getKey();
                String targetData = "";
                String lastChat = "";

                for (DataSnapshot childSanpshot : dataSnapshot.getChildren()) {
                    if (DataConst.CHAT_LAST_CHAT_KEY.equalsIgnoreCase(childSanpshot.getKey())) {
                        lastChat = childSanpshot.getValue().toString();
                    }
                }

                String[] splitKey = Utils.getChatName(key);

                int myId = mPresenter.getUserData().id;
                String myName = mPresenter.getUserData().name;
                String parseMyData = myId + "^" + myName;

                for (int i = 0; i < 2; i++) {
                    String splitData = splitKey[i];
                    if (!splitData.equalsIgnoreCase(parseMyData)) {
                        targetData = splitData;
                    }
                }

                Log.d(TAG, "onChildAdded() by pass. | " + key + " | " + targetData + " | " + lastChat);

                if (!TextUtils.isEmpty(targetData)) {
                    mPresenter.requestTargetProfileImage(key, targetData, lastChat);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                // 리스트의 아이템에 변화가 있을 때 수신
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                // 리스트의 아이템이 삭제 되었을 때 수신
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                // 리스트의 순서가 변경되었을 때 수신
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private View.OnClickListener mItemClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mPresenter.getUserData() == null) {
                Utils.showToast("내 정보를 불러오고 있어요. 잠시 후 다시 선택하여 주세요.");
                return;
            }
            ChatRoomData roomData = (ChatRoomData) v.getTag();
            Log.d(TAG, "mItemClickListener() roomName: " + roomData);

            Activity activity = getActivity();
            if (activity instanceof MainActivity) {
                ((MainActivity) activity).showChatDetailFragment(roomData);
            }

        }
    };

    @Override
    public void initChatList() {
        mChatAdapter = new ChatAdapter(mChatList, mItemClickListener);
        mChatLayoutManager = new GridLayoutManager(ApplicationController.getInstance(), 1, RecyclerView.VERTICAL, false) {
            @Override
            public void onLayoutCompleted(RecyclerView.State state) {
                super.onLayoutCompleted(state);
                Log.d(TAG, "onLayoutCompleted() called. | " + mAfterCallback + " | " + mIsAlreadyProcess);
                if (mAfterCallback && !mIsAlreadyProcess) {
                    Log.d(TAG, "onLayoutCompleted() | " + mInputDTO);
                    if (mInputDTO != null ) {
                        openChat(mInputDTO);
                    }
                }
            }
        };

        mChatRecyclerView.setAdapter(mChatAdapter);
        mChatRecyclerView.setLayoutManager(mChatLayoutManager);
    }

    @Override
    public void addChatList(String key, String value, String lastChat, String imageUrl) {
        Log.d(TAG, "addChatList() called. | " + key);
        UserData myData = mPresenter.getUserData();
        if (myData == null) {
            Utils.showToast("내 정보를 불러오지 못했어요. 채팅 탭을 재진입해주세요.");
            return;
        }
        String parseMyData = myData.id + "^" + myData.name;
        if (key.contains(parseMyData)) {
            ChatRoomData data = new ChatRoomData(key, value + "^" + imageUrl, lastChat);
            mChatList.add(data);
            mChatAdapter.notifyItemInserted(mChatAdapter.getItemCount() - 1);
        }

        mAfterCallback = true;

        if (!mIsAlreadyProcess && mInputDTO != null && mMyRoomSize == mChatAdapter.getItemCount()) {
            // 내가 그려야할 채팅방을 다 불러왔으나 처리가 되지 않은 경우 방생성 처리
            openChat(mInputDTO);
        }
    }

    @Override
    public void openChat(ChatRoomDTO roomDTO) {
        Log.d(TAG, "openChat() called. roomDTO: " + roomDTO.toString());
        if (roomDTO == null) {
            return;
        }

        if (!mAfterCallback) {
            return;
        }

        int itemIdx = -1;
        for (int i = 0; i < mChatList.size(); i++) {
            ChatRoomData roomData = mChatList.get(i);
            if (roomData.key.equalsIgnoreCase(roomDTO.getParseData1())
                    || roomData.key.equalsIgnoreCase(roomDTO.getParseData2())) {
                itemIdx = i;
                break;
            }
        }

        Log.d(TAG, "openChat() itemIdx: " + itemIdx);
        if (itemIdx != -1) {
            // 방을 찾은 경우
            ChatViewHolder holder = (ChatViewHolder) mChatRecyclerView.findViewHolderForAdapterPosition(itemIdx);
            if (holder != null) {
                holder.itemView.performClick();
                mIsAlreadyProcess = true;
            }
        } else {
            if (mMyRoomSize == mChatList.size()) {
                // 내가 그릴 방 콜백을 다 가져왔는데도 없는 경우 방 생성
                addRoom(roomDTO);
                mIsAlreadyProcess = true;
            }
        }
    }

    @Override
    public void addRoom(ChatRoomDTO roomDTO) {
        Log.d(TAG, "addRoom() called. roomDTO: " + roomDTO.toString());
        if (roomDTO == null) {
            return;
        }

        // 채팅방 생성 시, 중복된 채팅방 생성을 막기 위해 인덱스값이 높은게 무조건 앞으로 채팅방 제목을 구성하도록 처리
        String roomName = roomDTO.getParseData1();
        Integer roomIdx = mChatRecyclerView.getChildCount() + 2658905;

        Map<String, Object> updateMap = new HashMap<>();
        updateMap.put(roomName, roomIdx);
        mDatabaseReference.updateChildren(updateMap);
        mDatabaseReference.child(roomName).child(DataConst.CHAT_LAST_CHAT_KEY).setValue("");
    }

    @Override
    public void updateLastChat(String roomName, String lastChat) {
        if (mChatAdapter != null) {
            Log.d(TAG, "updateLastChat() called. | " + roomName + " | " + lastChat);
            for (int i = 0; i < mChatAdapter.getItemCount(); i++) {
                ChatViewHolder holder = (ChatViewHolder) mChatRecyclerView.findViewHolderForAdapterPosition(i);

                if (holder == null) {
                    Log.d(TAG, "updateLastChat() | holder is null.");
                    return;
                }

                ChatRoomData roomData = (ChatRoomData) holder.itemView.getTag();
                if (roomName.equalsIgnoreCase(roomData.key)) {
                    holder.mMessageText.setText(lastChat);
                }
            }
        }
    }

    private ChatViewHolder getViewHolderFromRoomName(String roomName) {
        ChatViewHolder viewHolder = null;
        if (mChatAdapter != null) {
            Log.d(TAG, "getViewHolderFromRoomName() called. | " + roomName);
            if (TextUtils.isEmpty(roomName)) {
                return null;
            }

            for (int i = 0; i < mChatAdapter.getItemCount(); i++) {
                ChatViewHolder holder = (ChatViewHolder) mChatRecyclerView.findViewHolderForAdapterPosition(i);

                if (holder == null) {
                    return null;
                }

                Object object = holder.itemView.getTag();
                ChatRoomData roomData = (ChatRoomData) holder.itemView.getTag();
                if (roomName.equalsIgnoreCase(roomData.key)) {
                    viewHolder = holder;
                }
            }
        }

        return viewHolder;
    }
}
