package com.example.myserverapp.work;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.myserverapp.data.TokenResponse;
import com.example.myserverapp.data.User;
import com.example.myserverapp.data.UserResponse;
import com.example.myserverapp.server.MyServerInterface;
import com.example.myserverapp.server.ServerHolder;
import com.google.gson.Gson;

import java.io.IOException;

import retrofit2.Response;

public class GetTokenWorker extends Worker {
    public GetTokenWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        MyServerInterface serverInterface = ServerHolder.getInstance().serverInterface;

        String userName = getInputData().getString("key_user_id");

        try {
            Response<TokenResponse> response = serverInterface.getUserToken(userName).execute();
            TokenResponse tokenResponse = response.body();
            String toJson = new Gson().toJson(tokenResponse);

            Data outputData = new Data.Builder()
                    .putString("key_output_user", toJson)
                    .build();

            return Result.success(outputData);

        } catch (IOException e) {
            e.printStackTrace();
            return Result.retry();
        }
    }
}

