package com.example.myserverapp.work;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.myserverapp.data.SetUserPrettyNameRequest;
import com.example.myserverapp.data.UserResponse;
import com.example.myserverapp.server.MyServerInterface;
import com.example.myserverapp.server.ServerHolder;
import com.google.gson.Gson;

import java.io.IOException;

import retrofit2.Response;

public class PostNewPrettyNameWorker extends Worker {
    public PostNewPrettyNameWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        MyServerInterface serverInterface = ServerHolder.getInstance().serverInterface;
        String token = "token " + getInputData().getString("key_get_token");
        String newName = getInputData().getString("key_get_new_pretty_name");
        try {
            SetUserPrettyNameRequest setPrettyNameRequest = new SetUserPrettyNameRequest(newName);
            Response<UserResponse> response = serverInterface.postUserPrettyName(setPrettyNameRequest,
                    token).execute();

            UserResponse userResponse = response.body();
            String toJson = new Gson().toJson(userResponse);
            Data outputData = new Data.Builder().putString("key_get_pretty_name", toJson).build();
            return Result.success(outputData);

        } catch (IOException e) {
            e.printStackTrace();
            return Result.retry();
        }
    }
}
