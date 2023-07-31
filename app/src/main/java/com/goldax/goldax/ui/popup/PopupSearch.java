package com.goldax.goldax.ui.popup;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.goldax.goldax.MainActivity;
import com.goldax.goldax.R;
import com.goldax.goldax.application.ApplicationController;
import com.goldax.goldax.data.DataConst;
import com.goldax.goldax.data.ListResult;
import com.goldax.goldax.data.SearchRequest;
import com.goldax.goldax.network.NetworkService;
import com.goldax.goldax.util.SharedPrefUtil;
import com.goldax.goldax.util.Utils;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;

public class PopupSearch extends Fragment {
    private static final String TAG = PopupSearch.class.getSimpleName();

    @BindView(R.id.popup_search_lost_location_text)
    public TextView mLocationText;
    @BindView(R.id.popup_search_lost_date)
    public TextView mDateText;
    @BindView(R.id.popup_search_category_spinner)
    public Spinner mCategorySpinner;
    @BindView(R.id.popup_search_lost_location_spinner_1)
    public Spinner mLocation1Spinner;
    @BindView(R.id.popup_search_lost_location_spinner_2)
    public Spinner mLocation2Spinner;
    @BindView(R.id.popup_search_lost_date_year)
    public Spinner mYearSpinner;
    @BindView(R.id.popup_search_lost_date_month)
    public Spinner mMonthSpinner;
    @BindView(R.id.popup_search_lost_date_day)
    public Spinner mDaySpinner;
    @BindView(R.id.popup_search_title_edit_text)
    public EditText mTitleEditText;
    @BindView(R.id.popup_search_image_btn)
    public ImageButton mImageSearchBtn;
    @BindView(R.id.popup_search_btn)
    public ImageButton mSearchBtn;
    @BindView(R.id.popup_search_reload_btn)
    public ImageButton mReloadBtn;

    private NetworkService mNetworkService;
    private int mCurType = -1;

    private MainActivity mMainActivity;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.popup_search, container, false);
        ButterKnife.bind(this, viewGroup);

        mMainActivity = (MainActivity) getActivity();

        return viewGroup;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (getArguments() != null) {
            mCurType = getArguments().getInt(DataConst.KEY.POPUP_TYPE);
        }
        initLayout();
        mNetworkService = ApplicationController.getInstance().getNetworkService();
    }

    private void initLayout() {
        mImageSearchBtn.setOnClickListener(mClickListener);
        mSearchBtn.setOnClickListener(mClickListener);
        mReloadBtn.setOnClickListener(mClickListener);

        if (mCurType == DataConst.FRAGMENT_TYPE.LOST) {
            mLocationText.setText("분실지역");
            mDateText.setText("분실날짜");
        } else if (mCurType == DataConst.FRAGMENT_TYPE.PICK) {
            mLocationText.setText("습득지역");
            mDateText.setText("습득날짜");
        }

        ArrayAdapter categoryAdapter = new ArrayAdapter<>(ApplicationController.getInstance(), R.layout.i_dropdown_item, getResources().getStringArray(R.array.category));
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mCategorySpinner.setAdapter(categoryAdapter);

        ArrayAdapter yearAdapter = new ArrayAdapter<>(ApplicationController.getInstance(), R.layout.i_dropdown_item, getResources().getStringArray(R.array.date_year));
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mYearSpinner.setAdapter(yearAdapter);


        ArrayAdapter preLocationAdapter = new ArrayAdapter<>(ApplicationController.getInstance(), R.layout.i_dropdown_item, getResources().getStringArray(R.array.location));
        preLocationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mLocation1Spinner.setAdapter(preLocationAdapter);
        mLocation1Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ArrayAdapter adapter = (ArrayAdapter) parent.getAdapter();
                String value = (String) adapter.getItem(position);
                ArrayAdapter afterLocationAdapter = new ArrayAdapter<>(ApplicationController.getInstance(), R.layout.i_dropdown_item, Utils.getDetailLocFromLocation(value));
                mLocation2Spinner.setAdapter(afterLocationAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ArrayAdapter preMonthAdapter = new ArrayAdapter<>(ApplicationController.getInstance(), R.layout.i_dropdown_item, getResources().getStringArray(R.array.date_month));
        preMonthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mMonthSpinner.setAdapter(preMonthAdapter);
        mMonthSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ArrayAdapter adapter = (ArrayAdapter) parent.getAdapter();
                String value = (String) adapter.getItem(position);
                ArrayAdapter afterDayAdapter = new ArrayAdapter<>(ApplicationController.getInstance(), R.layout.i_dropdown_item, Utils.getDayFromMonth(value));
                mDaySpinner.setAdapter(afterDayAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.popup_search_image_btn:
                    selectImageIntent();
                    break;
                case R.id.popup_search_reload_btn:
                    if (mCategorySpinner != null) {
                        mCategorySpinner.setSelection(0);
                    }
                    if (mLocation1Spinner != null) {
                        mLocation1Spinner.setSelection(0);
                    }
                    if (mLocation2Spinner != null) {
                        mLocation2Spinner.setSelection(0);
                    }
                    if (mYearSpinner != null) {
                        mYearSpinner.setSelection(0);
                    }
                    if (mMonthSpinner != null) {
                        mMonthSpinner.setSelection(0);
                    }
                    if (mDaySpinner != null) {
                        mDaySpinner.setSelection(0);
                    }
                    if (mTitleEditText != null) {
                        mTitleEditText.setText("");
                    }
                    mMainActivity.reloadItemList();
                    break;
                case R.id.popup_search_btn:
                    String title = mTitleEditText.getText().toString();
                    String category = (String) mCategorySpinner.getSelectedItem();
                    String location1 = (String) mLocation1Spinner.getSelectedItem();
                    String location2 = (String) mLocation2Spinner.getSelectedItem();
                    String location = location1 + ", " + location2;
                    String type = mCurType == DataConst.FRAGMENT_TYPE.PICK ? "found" : "lost";

                    StringBuilder sb = new StringBuilder();
                    sb.append(mYearSpinner.getSelectedItem());
                    sb.append("-");
                    sb.append(mMonthSpinner.getSelectedItem());
                    sb.append("-");
                    sb.append(mDaySpinner.getSelectedItem());
                    String date = sb.toString();

                    boolean existTitle = !TextUtils.isEmpty(title);
                    boolean existCategory = !"카테고리 선택".equals(category);
                    boolean existLocation1 = !"장소".equals(location1);
                    boolean existLocation2 = !"세부장소".equals(location2);
                    boolean existYear = !"-".equals(mYearSpinner.getSelectedItem());
                    boolean existMonth = !"-".equals(mMonthSpinner.getSelectedItem());
                    boolean existDay = !"-".equals(mDaySpinner.getSelectedItem());

                    boolean existLocation = existLocation1 && existLocation2;
                    boolean existDate = existYear && existMonth && existDay;

                    if (!existTitle && !existCategory && !existLocation && !existDate) {
                        Utils.showToast("검색할 항목을 선택해주세요.");
                        return;
                    }

                    if (!existTitle) {
                        title = null;
                    }
                    if (!existCategory) {
                        category = null;
                    }
                    if (!existLocation) {
                        location = null;
                    }
                    if (!existDate) {
                        date = null;
                    }

                    SearchRequest requestData = new SearchRequest(title, category, date, location, type);
                    requestCategorySearch(requestData);
                    break;
            }
        }
    };

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
//                            int limitWidth = (int) Utils.changePixel2DP(1024);
//                            int limitHeight = (int) Utils.changePixel2DP(1024);
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
//                            Log.d(TAG, "onActivityResult: size | " + resultWidth + " | " + resultHeight);
//
//                            Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, (int) resultWidth, (int) resultHeight, true);
//                            if (resizedBitmap != null) {
//                                String filePath = Utils.createImgFile(ApplicationController.getInstance(), resizedBitmap);
//                                if (filePath != null) {
//                                    requestImageSearch(filePath);
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
                            int limitWidth = (int) Utils.changePixel2DP(1024);
                            int limitHeight = (int) Utils.changePixel2DP(1024);
                            float resultWidth = bitmap.getWidth();
                            float resultHeight = bitmap.getHeight();

                            float width = resultWidth / 100;
                            float widthScale = limitWidth / width;


                            float height = resultHeight / 100;
                            float heightScale = limitHeight / height;

                            resultWidth *= (widthScale / 100);
                            resultHeight *= (heightScale / 100);

                            Log.d(TAG, "onActivityResult: size | " + resultWidth + " | " + resultHeight);

                            Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, (int) resultWidth, (int) resultHeight, true);
                            if (resizedBitmap != null) {
                                String filePath = Utils.createImgFile(ApplicationController.getInstance(), resizedBitmap);
                                if (filePath != null) {
                                    requestImageSearch(filePath);
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

    private void selectImageIntent() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        intent.putExtra("crop", "true");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(Utils.getImgTempFile()));
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        startActivityForResult(intent, DataConst.REQUEST_CODE_CROP_IMAGE);
    }

    private void requestCategorySearch(SearchRequest requestData) {
        String token = SharedPrefUtil.getInstance().get(DataConst.KEY.SP_LOGIN_TOKEN, null);
        if (token == null) {
            Utils.showToast("로그인을 해주세요.");
            return;
        }

        Call<ListResult> caller = caller = mNetworkService.callCategorySearch(token, requestData);
        caller.enqueue(new Callback<ListResult>() {
            @Override
            public void onResponse(Call<ListResult> call, Response<ListResult> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "onResponse: requestRegisterPick() response is success");

                    ListResult result = response.body();
                    if (result.isSuccess) {
                        if (Utils.isListEmpty(result.result)) {
                            Utils.showToast("비슷한 물건을 찾지 못했어요.");
                        } else {
                            mMainActivity.reloadItemList(result.result);
                        }
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
            public void onFailure(Call<ListResult> call, Throwable t) {
                t.printStackTrace();
                Utils.showToast("네트워크를 확인해주세요.");
            }
        });
    }

    private void requestImageSearch(String filePath) {
        String token = SharedPrefUtil.getInstance().get(DataConst.KEY.SP_LOGIN_TOKEN, null);
        if (token == null) {
            Utils.showToast("로그인을 해주세요.");
            return;
        }

        File imageFile = Utils.getImgFile(filePath);
        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), imageFile);
        MultipartBody.Part part = MultipartBody.Part.createFormData("image", imageFile.getName(), requestBody);

        Call<ListResult> caller = null;
        if (mCurType == DataConst.FRAGMENT_TYPE.PICK) {
            // 습득물인 경우 교체 처리
            caller = mNetworkService.callFoundImageSearch(token, part);
        } else {
            caller = mNetworkService.callLostImageSearch(token, part);
        }

        caller.enqueue(new Callback<ListResult>() {
            @Override
            public void onResponse(Call<ListResult> call, Response<ListResult> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "onResponse: requestImageSearch() response is success");

                    ListResult result = response.body();
                    if (result.isSuccess) {
                        if (Utils.isListEmpty(result.result)) {
                            Utils.showToast("비슷한 물건을 찾지 못했어요.");
                        } else {
                            mMainActivity.reloadItemList(result.result);
                        }
                    } else {
                        Utils.showToast("[failed] " + result.status);
                    }

                } else {
                    String msg = "[" + response.code() + "] " + response.message();
                    Utils.showToast(msg);
                    Log.d(TAG, "onResponse: requestImageSearch() response is not success! ["
                            + response.code() + " | " + response.message() + "]");
                    Log.d(TAG, "onResponse: requestImageSearch() failed.. " + response.errorBody().toString());
                }

                if (imageFile.exists()) {
                    imageFile.delete();
                }
            }
            @Override
            public void onFailure(Call<ListResult> call, Throwable t) {
                t.printStackTrace();
                if (imageFile.exists()) {
                    imageFile.delete();
                }
                Utils.showToast("네트워크를 확인해주세요.");
            }
        });
    }

//    private void requestImageDetect(String filePath) {
//        Log.d(TAG, "requestImageDetect() called. " + mKakaoNetworkService + " | " + filePath);
//        if (mKakaoNetworkService == null) {
//            return;
//        }
//
//        File imageFile = Utils.getImgFile(filePath);
//        if (imageFile == null) {
//            Log.d(TAG, "requestImageDetect: Image File is not found. do nothing.");
//            return;
//        }
//
//        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), imageFile);
//        MultipartBody.Part uploadFile = MultipartBody.Part.createFormData("file", imageFile.getName(), requestFile);
//
//        Log.d(TAG, "requestImageDetect: imageFile: " + imageFile.getName() + " | filePath: " + filePath + " | " + imageFile.length());
//        Log.d(TAG, "requestImageDetect: requestFile: " + requestFile.contentType());
//        Log.d(TAG, "requestImageDetect: uploadFile: " + uploadFile.body());
//
//        Call<ProductDetectResult> caller = mKakaoNetworkService.callProductDetect(uploadFile);
//        caller.enqueue(new Callback<ProductDetectResult>() {
//            @Override
//            public void onResponse(Call<ProductDetectResult> call, Response<ProductDetectResult> response) {
//                if (response.isSuccessful()) {
//                    Log.d(TAG, "onResponse: requestImageDetect() response is success");
//
//                    ProductDetectResult.ResultData result = response.body().result;
//                    Utils.showToast("[success] " + result.toString());
//                    if (result.objects != null) {
//                        for (ProductDetectResult.ResultData.Product data : result.objects) {
//                            Log.d(TAG, "onResponse: data: " + data.toString());
//                        }
//                    }
//
//                } else {
//                    String msg = "[" + response.code() + "] " + response.message();
//                    Utils.showToast(msg);
//                    Log.d(TAG, "onResponse: requestImageDetect() response is not success! ["
//                            + response.code() + " | " + response.message() + "]");
//                    Log.d(TAG, "onResponse: requestImageDetect() failed.. " + response.errorBody().toString());
//                }
//            }
//            @Override
//            public void onFailure(Call<ProductDetectResult> call, Throwable t) {
//                // 이미지 검출 통신 실패시.
//                t.printStackTrace();
//                Utils.showToast("이미지 검출 통신 실패.");
//            }
//        });
//    }
}
