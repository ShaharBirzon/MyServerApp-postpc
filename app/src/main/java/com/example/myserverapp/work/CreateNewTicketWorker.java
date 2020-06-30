package com.example.myserverapp.work;

import android.content.Context;

import com.example.myserverapp.data.Ticket;
import com.example.myserverapp.server.MyServerInterface;
import com.example.myserverapp.server.ServerHolder;
import com.google.gson.Gson;


import java.io.IOException;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import retrofit2.Response;

public class CreateNewTicketWorker extends Worker {
    public CreateNewTicketWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        MyServerInterface serverInterface = ServerHolder.getInstance().serverInterface;

        String ticketAsJson = getInputData().getString("key_input_ticket");
        Ticket ticket = new Gson().fromJson(ticketAsJson, Ticket.class);
        try {
            Response<Ticket> response = serverInterface.insertNewTicket(ticket).execute();
            Ticket responseBody = response.body();
            String responseAsJson = new Gson().toJson(responseBody);
            Data outputData = new Data.Builder()
                    .putString("key_output", responseAsJson)
                    .build();

            return Result.success(outputData);

        } catch (IOException e) {
            e.printStackTrace();
            return Result.retry();
        }
    }
}