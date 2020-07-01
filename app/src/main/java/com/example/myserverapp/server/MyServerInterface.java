package com.example.myserverapp.server;

import com.example.myserverapp.data.SetUserPrettyNameRequest;
import com.example.myserverapp.data.TokenResponse;
import com.example.myserverapp.data.User;
import com.example.myserverapp.data.UserResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface MyServerInterface {

    @GET("/user")
    Call<UserResponse> getUserFromServer(@Header("Authorization") String theToken);

    @GET("/users/{user_name}/token/")
    Call<TokenResponse> getUserToken(@Path("user_name") String userName);

    @Headers({
            "Content-Type:application/json"
    })
    @POST("/user/edit/")
    Call<UserResponse> postUserPrettyName(@Body SetUserPrettyNameRequest request, @Header("Authorization") String theToken);

}
