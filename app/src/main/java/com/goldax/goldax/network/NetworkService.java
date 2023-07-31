package com.goldax.goldax.network;

import com.goldax.goldax.data.LoginRequest;
import com.goldax.goldax.data.LoginResult;
import com.goldax.goldax.data.ListResult;
import com.goldax.goldax.data.LostRegisterResult;
import com.goldax.goldax.data.SearchRequest;
import com.goldax.goldax.data.UsersResult;
import com.goldax.goldax.data.PickRegisterResult;
import com.goldax.goldax.data.SignUpRequest;
import com.goldax.goldax.data.SignUpResult;

import java.util.ArrayList;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface NetworkService {
    // 회원가입
    @POST("api/v1/users/register")
    Call<SignUpResult> callSignUp(@Body SignUpRequest requestData);

    // 회원가입
    @POST("api/v1/users/login")
    Call<LoginResult> callLogin(@Body LoginRequest requestData);

    // 유저 정보 조회
    @GET("api/v1/users")
    Call<UsersResult> callUsers(@Header("token") String token);

    // 유저 인덱스를 통한 정보 조회
    @GET("api/v1/users/{id}")
    Call<UsersResult> callUserProfileImage(@Header("token") String token, @Path("id") int id);

    // 유저 프로필 이미지 변경
    @PUT("/api/v1/users")
    @Multipart
    Call<Void> callChangeProfileImage(@Header("token") String token
                                        , @Part MultipartBody.Part image);

    // 습득물 등록
    @POST("api/v1/found")
    @Multipart
    Call<PickRegisterResult> callRegisterPick(@Header("token") String token
                                            , @Part("title") RequestBody title
                                            , @Part("foundAt") RequestBody date
                                            , @Part("contents") RequestBody desc
                                            , @Part("location") RequestBody location
                                            , @Part("category") RequestBody category
                                            , @Part("reward") RequestBody money
                                            , @Part ArrayList<MultipartBody.Part> images);

    // 습득물 조회
    @GET("api/v1/found")
    Call<ListResult> callListPick(@Header("token") String token);

    // 습득물 이미지 검색
    @POST("api/v1/search/image/found")
    @Multipart
    Call<ListResult> callFoundImageSearch(@Header("token") String token
                                    , @Part MultipartBody.Part image);

    // 내가 쓴 습득물 조회
    @GET("/api/v1/users/founds")
    Call<ListResult> callMyPickList(@Header("token") String token);

    // 분실물 등록
    @POST("api/v1/lost")
    @Multipart
    Call<LostRegisterResult> callRegisterLost(@Header("token") String token
                                            , @Part("title") RequestBody title
                                            , @Part("lostAt") RequestBody date
                                            , @Part("contents") RequestBody desc
                                            , @Part("location") RequestBody location
                                            , @Part("category") RequestBody category
                                            , @Part("reward") RequestBody money
                                            , @Part ArrayList<MultipartBody.Part> images);

    // 분실물 조회
    @GET("api/v1/lost")
    Call<ListResult> callListLost(@Header("token") String token);

    // 분실물 이미지 검색
    @POST("api/v1/search/image/lost")
    @Multipart
    Call<ListResult> callLostImageSearch(@Header("token") String token
                                        , @Part MultipartBody.Part image);

    // 분실물, 습득물 카테고리 검색
    @POST("/api/v1/search")
    Call<ListResult> callCategorySearch(@Header("token") String token, @Body SearchRequest requestData);

    // 내가 쓴 습득물 조회
    @GET("/api/v1/users/losts")
    Call<ListResult> callMyLostList(@Header("token") String token);

    // 분실물 게시물 제거
    @DELETE("/api/v1/lost/{id}")
    Call<Void> callDeleteLost(@Header("token") String token, @Path("id") int id);

    // 습득물 게시물 제거
    @DELETE("/api/v1/found/{id}")
    Call<Void> callDeletePick(@Header("token") String token, @Path("id") int id);

    // 분실물 습득 완료
    @POST("/api/v1/lost/{id}/done")
    Call<Void> callCompleteLost(@Header("token") String token, @Path("id") int id);

    // 습득물 습득 완료
    @POST("/api/v1/found/{id}/done")
    Call<Void> callCompletePick(@Header("token") String token, @Path("id") int id);
}
