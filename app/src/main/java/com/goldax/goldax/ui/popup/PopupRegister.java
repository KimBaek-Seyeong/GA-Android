package com.goldax.goldax.ui.popup;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.goldax.goldax.MainActivity;
import com.goldax.goldax.R;
import com.goldax.goldax.application.ApplicationController;
import com.goldax.goldax.data.DataConst;
import com.goldax.goldax.data.LostRegisterRequest;
import com.goldax.goldax.data.LostRegisterResult;
import com.goldax.goldax.data.PickRegisterRequest;
import com.goldax.goldax.data.PickRegisterResult;
import com.goldax.goldax.network.NetworkService;
import com.goldax.goldax.ui.popup.register.ImageAdapter;
import com.goldax.goldax.util.SharedPrefUtil;
import com.goldax.goldax.util.Utils;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;

public class PopupRegister extends Fragment {
    private static final String TAG = PopupRegister.class.getSimpleName();

    private MainActivity mMainActivity;

    @BindView(R.id.popup_register_image_add_btn)
    public ConstraintLayout mImageAddBtn;
    @BindView(R.id.popup_register_image_recyclerview)
    public RecyclerView mImageRecyclerView;
    @BindView(R.id.popup_register_item_title_edit)
    public EditText mItemTitleEdit;
    @BindView(R.id.popup_register_item_desc_edit)
    public EditText mItemDescEdit;
    @BindView(R.id.popup_register_item_category)
    public Spinner mItemCategorySpinner;
    @BindView(R.id.popup_register_item_location_1)
    public Spinner mItemLocation1Spinner;
    @BindView(R.id.popup_register_item_location_2)
    public Spinner mItemLocation2Spinner;
    @BindView(R.id.popup_register_item_date_year)
    public Spinner mItemYearSpinner;
    @BindView(R.id.popup_register_item_date_month)
    public Spinner mItemMonthSpinner;
    @BindView(R.id.popup_register_item_date_day)
    public Spinner mItemDaySpinner;
    @BindView(R.id.popup_register_submit_btn)
    public Button mSubmitBtn;
    @BindView(R.id.popup_register_item_date_layout)
    public ConstraintLayout mDateLayout;
    @BindView(R.id.popup_register_item_money_edit)
    public EditText mMoneyEdit;

    private NetworkService mNetworkService;
    private @DataConst.FRAGMENT_TYPE int mCurType;

    private ArrayList<String> mImagePaths;
    private ImageAdapter mImageAdapter;
    private GridLayoutManager mGridLayoutManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.popup_register, container, false);
        ButterKnife.bind(this, viewGroup);

        return viewGroup;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mMainActivity = (MainActivity) getActivity();

        if (getArguments() != null) {
            mCurType = getArguments().getInt(DataConst.KEY.POPUP_TYPE);
        }
        initLayout();
        mNetworkService = ApplicationController.getInstance().getNetworkService();
    }

    private void initLayout() {
        mSubmitBtn.setOnClickListener(mClickListener);
        mImageAddBtn.setOnClickListener(mImageAddClickListener);

        mImagePaths = new ArrayList<String>();
        mImageAdapter = new ImageAdapter(mImagePaths, mImageItemClickListener);
        mGridLayoutManager = new GridLayoutManager(ApplicationController.getInstance(), 1, RecyclerView.HORIZONTAL, false);

        mImageRecyclerView.setAdapter(mImageAdapter);
        mImageRecyclerView.setLayoutManager(mGridLayoutManager);
        mImageRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.left = Utils.changeDP2Pixel(2.5f);
                outRect.right = Utils.changeDP2Pixel(2.5f);
            }
        });

        ArrayAdapter categoryAdapter = new ArrayAdapter<>(ApplicationController.getInstance(), R.layout.i_dropdown_item, getResources().getStringArray(R.array.category));
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mItemCategorySpinner.setAdapter(categoryAdapter);

        ArrayAdapter yearAdapter = new ArrayAdapter<>(ApplicationController.getInstance(), R.layout.i_dropdown_item, getResources().getStringArray(R.array.date_year));
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mItemYearSpinner.setAdapter(yearAdapter);

        ArrayAdapter preLocationAdapter = new ArrayAdapter<>(ApplicationController.getInstance(), R.layout.i_dropdown_item, getResources().getStringArray(R.array.location));
        preLocationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mItemLocation1Spinner.setAdapter(preLocationAdapter);
        mItemLocation1Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ArrayAdapter adapter = (ArrayAdapter) parent.getAdapter();
                String value = (String) adapter.getItem(position);
                ArrayAdapter afterLocationAdapter = new ArrayAdapter<>(ApplicationController.getInstance(), R.layout.i_dropdown_item, Utils.getDetailLocFromLocation(value));
                mItemLocation2Spinner.setAdapter(afterLocationAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ArrayAdapter preMonthAdapter = new ArrayAdapter<>(ApplicationController.getInstance(), R.layout.i_dropdown_item, getResources().getStringArray(R.array.date_month));
        preMonthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mItemMonthSpinner.setAdapter(preMonthAdapter);
        mItemMonthSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ArrayAdapter adapter = (ArrayAdapter) parent.getAdapter();
                String value = (String) adapter.getItem(position);
                ArrayAdapter afterDayAdapter = new ArrayAdapter<>(ApplicationController.getInstance(), R.layout.i_dropdown_item, Utils.getDayFromMonth(value));
                mItemDaySpinner.setAdapter(afterDayAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String title = mItemTitleEdit.getText().toString();
            String desc = mItemDescEdit.getText().toString();
            String category = (String) mItemCategorySpinner.getSelectedItem();
            String location1 = (String) mItemLocation1Spinner.getSelectedItem();
            String location2 = (String) mItemLocation2Spinner.getSelectedItem();
            String money = mMoneyEdit.getText().toString();

            StringBuilder sb = new StringBuilder();
            sb.append(mItemYearSpinner.getSelectedItem());
            sb.append("-");
            sb.append(mItemMonthSpinner.getSelectedItem());
            sb.append("-");
            sb.append(mItemDaySpinner.getSelectedItem());
            String date = sb.toString();

            if (TextUtils.isEmpty(title)) {
                Utils.showToast("물품명을 입력해주세요.");
                return;
            } else if (TextUtils.isEmpty(desc)) {
                Utils.showToast("상세정보를 입력해주세요.");
                return;
            } else if ("카테고리 선택".equals(category)) {
                Utils.showToast("카테고리를 선택해주세요.");
                return;
            } else if ("장소".equals(location1)) {
                Utils.showToast("장소를 선택해주세요.");
                return;
            } else if ("세부장소".equals(location2)) {
                Utils.showToast("세부장소를 선택해주세요.");
                return;
            } else if (Utils.isListEmpty(mImagePaths)) {
                Utils.showToast("사진을 추가해주세요.");
                return;
            } else if (TextUtils.isEmpty(money)) {
                Utils.showToast("보상금 금액 혹은 0을 입력해주세요.");
                return;
            } else if ("-".equals(mItemYearSpinner.getSelectedItem())) {
                Utils.showToast("연도를 선택해주세요.");
                return;
            } else if ("-".equals(mItemMonthSpinner.getSelectedItem())) {
                Utils.showToast("월을 선택해주세요.");
                return;
            } else if ("-".equals(mItemDaySpinner.getSelectedItem())) {
                Utils.showToast("일을 선택해주세요.");
                return;
            }

            if (mCurType == DataConst.FRAGMENT_TYPE.LOST) {
                LostRegisterRequest requestData = new LostRegisterRequest(title, desc, category, location1, location2, date, money);
                requestRegisterLost(requestData, mImagePaths);
            } else if (mCurType == DataConst.FRAGMENT_TYPE.PICK) {
                PickRegisterRequest requestData = new PickRegisterRequest(title, desc, category, location1, location2, date, money);
                requestRegisterPick(requestData, mImagePaths);
            }
        }
    };

    private View.OnClickListener mImageAddClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mGridLayoutManager.getItemCount() < 5) {
                selectImageIntent();
            } else {
                Utils.showToast("이미지는 5장까지만 첨부가 가능합니다.");
            }
        }
    };

    private View.OnClickListener mImageItemClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int position = (int) view.getTag(R.attr.key_image_pos);

            mImageAdapter.removeItem(position);
        }
    };

    private void requestRegisterLost(LostRegisterRequest requestData, ArrayList<String> imagePaths) {
        String token = SharedPrefUtil.getInstance().get(DataConst.KEY.SP_LOGIN_TOKEN, null);
        if (token == null) {
            Utils.showToast("로그인을 해주세요.");
            return;
        }

        Log.d(TAG, "requestRegisterLost() called. " + requestData);

        ArrayList<MultipartBody.Part> images = new ArrayList<>();
        ArrayList<File> imageFiles = new ArrayList<>();
        for (String path : imagePaths) {
            File imageFile = Utils.getImgFile(path);
            if (imageFile != null) {
                RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), imageFile);
                MultipartBody.Part part = MultipartBody.Part.createFormData("images", imageFile.getName(), requestBody);
                images.add(part);

                imageFiles.add(imageFile);
            }
        }

        RequestBody title = Utils.getRequestBodyPlainType(requestData.title);
        RequestBody date = Utils.getRequestBodyPlainType(requestData.date);
        RequestBody desc = Utils.getRequestBodyPlainType(requestData.desc);
        String locationData = requestData.location1 + ", " + requestData.location2;
        RequestBody location = Utils.getRequestBodyPlainType(locationData);
        RequestBody category = Utils.getRequestBodyPlainType(requestData.category);
        RequestBody money = Utils.getRequestBodyPlainType(requestData.money);
        Call<LostRegisterResult> caller = mNetworkService.callRegisterLost(token, title, date, desc, location, category, money, images);
        caller.enqueue(new Callback<LostRegisterResult>() {
            @Override
            public void onResponse(Call<LostRegisterResult> call, Response<LostRegisterResult> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "onResponse: requestRegisterPick() response is success");

                    LostRegisterResult result = response.body();
                    if (result.isSuccess) {
                        Utils.showToast("글 작성이 완료되었어요.");
                        mMainActivity.hideRegisterFragment();
                        mMainActivity.reloadItemList();
                    } else {
                        Utils.showToast("[failed] " + result.status);
                    }

                } else {
                    String msg = "[" + response.code() + "] " + response.message();
                    Utils.showToast(msg);
                    Log.d(TAG, "onResponse: requestRegisterPick() response is not success! ["
                            + response.code() + " | " + response.message() + "]");
                    Log.d(TAG, "onResponse: requestRegisterPick() failed.. " + response.errorBody().toString());
                }

                if (!Utils.isListEmpty(imageFiles)) {
                    for (File file : imageFiles) {
                        if (file.exists()) {
                            file.delete();
                        }
                    }
                }
            }
            @Override
            public void onFailure(Call<LostRegisterResult> call, Throwable t) {
                if (!Utils.isListEmpty(imageFiles)) {
                    for (File file : imageFiles) {
                        if (file.exists()) {
                            file.delete();
                        }
                    }
                }
                t.printStackTrace();
                Utils.showToast("네트워크를 확인해주세요.");
            }
        });
    }


    /**
     * 습득물 등록 요청
     *
     * @param requestData
     * @param imagePaths
     */
    private void requestRegisterPick(PickRegisterRequest requestData, ArrayList<String> imagePaths) {
        String token = SharedPrefUtil.getInstance().get(DataConst.KEY.SP_LOGIN_TOKEN, null);
        if (token == null) {
            Utils.showToast("로그인을 해주세요.");
            return;
        }

        Log.d(TAG, "requestRegisterPick() called. " + requestData);

        ArrayList<MultipartBody.Part> images = new ArrayList<>();
        for (String path : imagePaths) {
            File imageFile = Utils.getImgFile(path);
            if (imageFile != null) {
                RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), imageFile);
                MultipartBody.Part part = MultipartBody.Part.createFormData("images", imageFile.getName(), requestBody);
                images.add(part);
            }
        }
        RequestBody title = Utils.getRequestBodyPlainType(requestData.title);
        RequestBody date = Utils.getRequestBodyPlainType(requestData.date);
        RequestBody desc = Utils.getRequestBodyPlainType(requestData.desc);
        String locationData = requestData.location1 + ", " + requestData.location2;
        RequestBody location = Utils.getRequestBodyPlainType(locationData);
        RequestBody category = Utils.getRequestBodyPlainType(requestData.category);
        RequestBody money = Utils.getRequestBodyPlainType(requestData.money);

        Call<PickRegisterResult> caller = mNetworkService.callRegisterPick(token, title, date, desc, location, category, money, images);
        caller.enqueue(new Callback<PickRegisterResult>() {
            @Override
            public void onResponse(Call<PickRegisterResult> call, Response<PickRegisterResult> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "onResponse: requestRegisterPick() response is success");

                    PickRegisterResult result = response.body();
                    if (result.isSuccess) {
                        Utils.showToast("글 작성이 완료되었어요.");
                        mMainActivity.hideRegisterFragment();
                        mMainActivity.reloadItemList();
                    } else {
                        Utils.showToast("[failed] " + result.status);
                    }

                } else {
                    String msg = "[" + response.code() + "] " + response.message();
                    Utils.showToast(msg);
                    Log.d(TAG, "onResponse: requestRegisterPick() response is not success! ["
                            + response.code() + " | " + response.message() + "]");
                    Log.d(TAG, "onResponse: requestRegisterPick() failed.. " + response.errorBody().toString());
                }
            }
            @Override
            public void onFailure(Call<PickRegisterResult> call, Throwable t) {
                // 이미지 검출 통신 실패시.
                t.printStackTrace();
                Utils.showToast("네트워크를 확인해주세요.");
            }
        });
    }

    private void selectImageIntent() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        intent.putExtra("crop", "true");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(Utils.getImgTempFile()));
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        startActivityForResult(intent, DataConst.REQUEST_CODE_CROP_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult() called. " + requestCode + " | " + resultCode + " | " + data);
        Bitmap bitmap = null;
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case DataConst.REQUEST_CODE_SELECT_IMAGE:
//                    try {
//                        ContentResolver contentResolver = ApplicationController.getInstance().getContentResolver();
//                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
//                            Uri uri = data.getData();
//                            ImageDecoder.Source source = ImageDecoder.createSource(contentResolver, uri);
//                            bitmap = ImageDecoder.decodeBitmap(source);
//                        } else {
//                            String dataString = data.getDataString();
//                            bitmap = MediaStore.Images.Media.getBitmap(contentResolver, Uri.parse(dataString));
//                        }
//                        Log.d(TAG, "makeImgFile() bitmap: " + bitmap);
//                        if (bitmap != null) {
//                            int limitWidth = (int) Utils.changePixel2DP(2048);
//                            int limitHeight = (int) Utils.changePixel2DP(2048);
//                            float resultWidth = bitmap.getWidth();
//                            float resultHeight = bitmap.getHeight();
//
//                            float width = resultWidth / 100;
//                            float widthScale = limitWidth / width;
//
//
//                            float height = resultHeight / 100;
//                            float heightScale = limitHeight / height;
//
//                            resultWidth *= (widthScale / 100);
//                            resultHeight *= (heightScale / 100);
//
//                            Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, (int) resultWidth, (int) resultHeight, true);
//                            if (resizedBitmap != null) {
//                                String filePath = Utils.createImgFile(ApplicationController.getInstance(), resizedBitmap);
//                                if (filePath != null) {
//                                    if (mImageAdapter != null) {
//                                        mImageAdapter.addItemList(filePath);
//                                    }
//                                } else {
//                                    String guide = getString(R.string.select_image_failed);
//                                    Utils.showToast(guide);
//                                }
//                            } else {
//                                Utils.showToast("이미지를 변환하는데 실패하였어요.");
//                            }
//                        } else {
//                            Utils.showToast("이미지를 변환하는데 실패하였어요.");
//                        }
//                    } catch (FileNotFoundException e) {
//                        e.printStackTrace();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    } catch (OutOfMemoryError e) {
//                        Utils.showToast("이미지 용량이 너무 커서 불러올 수 없어요.");
//                    }
                    break;
                case DataConst.REQUEST_CODE_CROP_IMAGE:
                    try {
                        ContentResolver contentResolver = ApplicationController.getInstance().getContentResolver();
                        Uri uri = Uri.fromFile(Utils.getImgTempFile());
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                            ImageDecoder.Source source = ImageDecoder.createSource(contentResolver, uri);
                            bitmap = ImageDecoder.decodeBitmap(source);
                        } else {
                            bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri);
                        }

                        if (bitmap != null) {
                            int limitWidth = (int) Utils.changePixel2DP(2048);
                            int limitHeight = (int) Utils.changePixel2DP(2048);
                            float resultWidth = bitmap.getWidth();
                            float resultHeight = bitmap.getHeight();

                            float width = resultWidth / 100;
                            float widthScale = limitWidth / width;


                            float height = resultHeight / 100;
                            float heightScale = limitHeight / height;

                            resultWidth *= (widthScale / 100);
                            resultHeight *= (heightScale / 100);

                            Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, (int) resultWidth, (int) resultHeight, true);
                            if (resizedBitmap != null) {
                                String filePath = Utils.createImgFile(ApplicationController.getInstance(), resizedBitmap);
                                if (filePath != null) {
                                    if (mImageAdapter != null) {
                                        mImageAdapter.addItemList(filePath);
                                    }
                                } else {
                                    String guide = getString(R.string.select_image_failed);
                                    Utils.showToast(guide);
                                }
                            } else {
                                Utils.showToast("이미지를 변환하는데 실패하였어요.");
                            }
                        } else {
                            Utils.showToast("이미지를 변환하는데 실패하였어요.");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    }

}
