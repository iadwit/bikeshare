package com.student.bikeshare.retorfitClientAndService;


import com.student.bikeshare.models.DataModifiedModel;
import com.student.bikeshare.models.MailModel;
import com.student.bikeshare.models.ResponseModel;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface WebServices {

    @GET("set.php")
    Call<ResponseModel> set_field(@Query("id") int id, @Query("field") int field, @Query("value") String value);

    @GET("set_values.php")
    Call<Integer> get_field(@Query("id") int id, @Query("field") int field);

    @GET("get_values.php")
    Call<DataModifiedModel> get_fields(@Query("id") int id, @Query("os") String os);

    @POST("send_mail.php")
    Call<ResponseModel> sendMail(@Body MailModel mailModel);


}