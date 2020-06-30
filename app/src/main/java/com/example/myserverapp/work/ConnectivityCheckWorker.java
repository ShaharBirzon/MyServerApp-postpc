package com.example.myserverapp.work;

import android.content.Context;
import java.io.IOException;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.myserverapp.data.User;
import com.example.myserverapp.server.MyServerInterface;
import com.example.myserverapp.server.ServerHolder;

import retrofit2.Response;

public class ConnectivityCheckWorker extends Worker {
    public ConnectivityCheckWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        MyServerInterface serverInterface = ServerHolder.getInstance().serverInterface;

        try {
            Response<User> step1response = serverInterface.connectivityCheck().execute();
            return Result.success();

        } catch (IOException e) {
            e.printStackTrace();
            return Result.retry();
        }
    }
}
