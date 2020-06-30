package com.example.myserverapp.server;

import com.example.myserverapp.data.SetUserPrettyNameRequest;
import com.example.myserverapp.data.Ticket;
import com.example.myserverapp.data.TokenResponse;
import com.example.myserverapp.data.User;
import com.example.myserverapp.data.UserResponse;

import org.json.JSONObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface MyServerInterface {

    @GET("/users/0")
    Call<User> connectivityCheck();


    @GET("/users/{user_id}")
    Call<User> getUser(@Path("user_id") String userId);

    @GET("/users/{user_name}")
    Call<UserResponse> getUserFromServer(@Path("user_name") String userId, @Header("Authorization: token {theToken}") String theToken);

    @GET("/users/{user_name}/token/")
    Call<TokenResponse> getUserToken(@Path("user_name") String userName);

    @POST
    Call<UserResponse> postUserPrettyName(@Body SetUserPrettyNameRequest request);


    @GET("/todos")
    Call<List<Ticket>> getAllTicketsForUser(@Query("user_id") String userId);


    // the result will be the ticket as was created in the server (e.g. change in the "id" field, etc)
    @POST("/todos")
    Call<Ticket> insertNewTicket(@Body Ticket ticket);

}



/*
 server endpoint:
 https://jsonplaceholder.typicode.com/
 methods to have:
 * connectivity check A - users/0
 * connectivity check B - todos/0
 * getUser(user_id)
 * getAllTodos(user_id)
 * putNewTodo(todoId)
 */