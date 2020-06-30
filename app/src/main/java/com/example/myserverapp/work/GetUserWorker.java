package com.example.myserverapp.work;

import android.content.Context;

import com.example.myserverapp.data.User;
import com.example.myserverapp.data.UserResponse;
import com.example.myserverapp.server.MyServerInterface;
import com.example.myserverapp.server.ServerHolder;
import com.google.gson.Gson;

import java.io.IOException;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import retrofit2.Response;

public class GetUserWorker extends Worker {
    public GetUserWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        MyServerInterface serverInterface = ServerHolder.getInstance().serverInterface;

        String userName = getInputData().getString("key_user_id");
        String token = getInputData().getString("token");

        try {
            Response<UserResponse> response = serverInterface.getUserFromServer(userName, token).execute();
            User user = response.body().data;
            String userAsJson = new Gson().toJson(user);

            Data outputData = new Data.Builder()
                    .putString("key_output_user", userAsJson)
                    .build();

            return Result.success(outputData);

        } catch (IOException e) {
            e.printStackTrace();
            return Result.retry();
        }
    }
}
