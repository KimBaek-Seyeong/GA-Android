package com.goldax.goldax.util;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import com.goldax.goldax.R;
import com.goldax.goldax.application.ApplicationController;
import com.goldax.goldax.data.DataConst;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * Common한 Util class
 */
public class Utils {
    private static final String TAG = Utils.class.getSimpleName();

    private static Context mContext;
    private static float mDensity;

    public static void setDensity(Context context) {
        mContext = context;
        mDensity = context.getResources().getDisplayMetrics().density;
        Log.d(TAG, "setDensity() density: " + mDensity);
    }

    /**
     * 해상도별 dp -> pixel 단위 변환
     * @param d: int
     * @return int
     */
    public static int changeDP2Pixel(int d) {
        return (int) (d * mDensity + 0.5f);
    }

    /**
     * 해상도별 dp -> pixel 단위 변환
     * @param d: float
     * @return int
     */
    public static int changeDP2Pixel(float d) {
        return (int) (d * mDensity + 0.5f);
    }

    public static float changePixel2DP(int px) {
        return px / mDensity;
    }


    /**
     * List null or empty 체크
     * @param list
     * @return boolean
     */
    public static boolean isListEmpty(List<?> list) {
        if (list == null || list.size() == 0) {
            return true;
        }
        return false;
    }

    /**
     * Toast 노출
     * @param desc: String
     */
    public static void showToast(String desc) {
        Toast.makeText(mContext, desc, Toast.LENGTH_SHORT).show();
    }

    public static String createImgFile(Context context, Bitmap bitmap) {
        if (bitmap == null) {
            Log.d(TAG, "createImgFile() bitmap is null. do nothing.");
            return null;
        }
        String filePath = null;
        String dirPath = context.getCacheDir() + File.separator + "goldax";
        String filename = DataConst.FILE_PREFIX + System.currentTimeMillis() + DataConst.FILE_SUFFIX;

        File dir = new File(dirPath);
        if (!dir.exists()) {
            dir.mkdir();
            dir.setReadable(true);
            dir.setWritable(true);
            dir.setExecutable(true);
        }

        File imageFile = new File(dir, filename);
        FileOutputStream fos = null;
        try {
            imageFile.createNewFile();
            imageFile.setReadable(true);
            imageFile.setWritable(true);
            imageFile.setExecutable(true);

            fos = new FileOutputStream(imageFile);

            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);

            filePath = imageFile.getPath();
        } catch (FileNotFoundException fe) {
            fe.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }

        Log.d(TAG, "createImgFile() filePath: " + filePath);
        return filePath;
    }

    public static File getImgTempFile() {
        String filename = "temp_image_file.jpeg";
        File imageTempFile = new File(Environment.getExternalStorageDirectory(), filename);
        try {
            imageTempFile.createNewFile();
            imageTempFile.setReadable(true);
            imageTempFile.setWritable(true);
            imageTempFile.setExecutable(true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.d(TAG, "createImgFile() file: " + imageTempFile);
        return imageTempFile;
    }

    public static File getImgFile(String filePath) {
        Log.d(TAG, "getImgFile() called. filePath: " + filePath);
        File targetFile = new File(filePath);

        if (targetFile.exists()) {
            return targetFile;
        }
        return null;
    }

    public static String[] getDetailLocFromLocation(String location) {
        Resources resources = mContext.getResources();
        if ("제1캠퍼스".equals(location)) {
            return resources.getStringArray(R.array.location_1cam);
        } else if ("제2캠퍼스".equals(location)) {
            return resources.getStringArray(R.array.location_2cam);
        } else if ("부속건물".equals(location)) {
            return resources.getStringArray(R.array.location_building);
        } else {
            return resources.getStringArray(R.array.default_location);
        }
    }

    public static String[] getDayFromMonth(String month) {
        Resources resources = mContext.getResources();
        if ("02".equals(month)) {
            // 28일
            return resources.getStringArray(R.array.date_day_28);
        } else if ("01".equals(month) || "03".equals(month) || "05".equals(month) || "07".equals(month)
                || "08".equals(month) || "10".equals(month) || "12".equals(month)) {
            // 31일
            return resources.getStringArray(R.array.date_day_31);
        } else {
            // 30일
            return resources.getStringArray(R.array.date_day_30);
        }
    }

    /**
     * 천 단위 , 찍기
     *
     * @param number : String 형식의 숫자
     * @return String 형식의 , 포함된 숫자
     */
    public static String addComma(String number) {
        String result = number;

        try {
            if (number.matches("^[0-9]*$")) {
                result = String.format("%,d", Integer.valueOf(number));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public static RequestBody getRequestBodyPlainType(String value) {
        RequestBody requestBody = RequestBody.create(MediaType.parse("text/plain"), value);
        return requestBody;
    }

    /**
     * 키값을 통해 채팅방 파싱
     * "@" 기준 파싱
     *
     * @param key String - 4^hzoou@1^DD (userIdx^UserName@UserIdx^UserName)
     * @return
     */
    public static String[] getChatName(String key) {
        if (key == null) {
            return null;
        }

        String[] result = key.split("@");
        return result;
    }

    /**
     * 키값을 통해 채팅방 파싱
     * "^" 기준 파싱
     *
     * @param value String - 1^DD (userIdx^UserName)
     * @return
     */
    public static String getChatUsername(String value) {
        if (value == null) {
            return "";
        }

        String[] splitStr = value.split("\\^");
        if (splitStr.length < 2) {
            return "";
        }

        String result = splitStr[1];
        return result;
    }

    /**
     * 키값을 통해 채팅방 파싱
     * "^" 기준 파싱
     *
     * @param value String - 1^DD (userIdx^UserName)
     * @return
     */
    public static String getChatUserId(String value) {
        if (value == null) {
            return "";
        }

        String[] splitStr = value.split("\\^");
        if (splitStr.length < 2) {
            return "";
        }

        String result = splitStr[0];
        return result;
    }

    /**
     * 키값을 통해 채팅방 파싱 - 유저 이미지 url 파싱 (ChatViewHolder 에서만 사용)
     * "^" 기준 파싱
     *
     * @param value String - 1^DD^http:~~~ (userIdx^UserName^ImageUrl)
     * @return
     */
    public static String getChatUserImageUrl(String value) {
        if (value == null) {
            return "";
        }

        String[] splitStr = value.split("\\^");
        if (splitStr.length < 3) {
            return "";
        }

        String result = splitStr[2];
        return result;
    }
}
