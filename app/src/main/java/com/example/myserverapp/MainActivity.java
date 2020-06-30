package com.example.myserverapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.Operation;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.myserverapp.data.Ticket;
import com.example.myserverapp.data.TokenResponse;
import com.example.myserverapp.data.User;
import com.example.myserverapp.work.ConnectivityCheckWorker;
import com.example.myserverapp.work.CreateNewTicketWorker;
import com.example.myserverapp.work.GetTokenWorker;
import com.example.myserverapp.work.GetUserWorker;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    User user;
    private SharedPreferences sp;
    private static final String USER_ID = "3";
    private static String TAG = "MainActivity";
    public static String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sp = getApplicationContext().getSharedPreferences("myPreferences", Context.MODE_PRIVATE);
        if (sp.getBoolean("isFirstTime", true)){
            sp.edit().putBoolean("isFirstTime", false).apply();
            startActivity(new Intent(this, LoginActivity.class));
        }

//        checkConnectivityAndSetUI();

        String username = sp.getString("username", "");
        if(username.equals("")){
            Log.d(TAG, "error! cannot find username in sp");
        }
        else{
            getUserToken(username);
        }


//        getUser();
//        createSampleTicket();
    }

    private void getUserToken(String username) {
        UUID workTagUniqueId = UUID.randomUUID();
        OneTimeWorkRequest checkConnectivityWork = new OneTimeWorkRequest.Builder(GetTokenWorker.class)
                .setConstraints(new Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build())
                .setInputData(new Data.Builder().putString("key_user_id", username).build())
                .addTag(workTagUniqueId.toString())
                .build();

        WorkManager.getInstance().enqueue(checkConnectivityWork);

        WorkManager.getInstance().getWorkInfosByTagLiveData(workTagUniqueId.toString())
                .observe(this, new Observer<List<WorkInfo>>() {
                    @Override
                    public void onChanged(List<WorkInfo> workInfos) {
                        // we know there will be only 1 work info in this list - the 1 work with that specific tag!
                        // there might be some time until this worker is finished to work (in the mean team we will get an empty list
                        // so check for that
                        if (workInfos == null || workInfos.isEmpty())
                            return;

                        if (workInfos.get(0).getState() == WorkInfo.State.FAILED ||
                                workInfos.get(0).getState() != WorkInfo.State.SUCCEEDED) {
                            return;
                        }

                        WorkInfo info = workInfos.get(0);

                        // now we can use it
                        String tokenAsJson = info.getOutputData().getString("key_output_user");
                        Log.d(TAG, "got token: " + tokenAsJson);
                        sp.edit().putString(tokenAsJson, "").apply(); // adding token to sp

                        TokenResponse tokenResponse = new Gson().fromJson(tokenAsJson, TokenResponse.class);

                        System.out.println("yay");

                        TextView tokenTxt = findViewById(R.id.tv_token);
                        tokenTxt.setText(tokenResponse.data);
                    }
                });
    }

    public void savePrettyOnClick(View view){

    }


    private void checkConnectivityAndSetUI(){

        OneTimeWorkRequest checkConnectivityWork = new OneTimeWorkRequest.Builder(ConnectivityCheckWorker.class)

                .setConstraints(new Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build())
                // if we will remove the constraints - then the connectivity check will happen immediately
                // if we will add the constraints - then the connectivity check will happen only after we have access to the internet

                .build();

        Operation runningWork = WorkManager.getInstance().enqueue(checkConnectivityWork);

        runningWork.getState().observe(this, new Observer<Operation.State>() {
            @Override
            public void onChanged(Operation.State state) {
                if (state == null) return;

                if (state instanceof Operation.State.SUCCESS) {
                    // update UI - connected
                }
                else {
                    // update UI - not connected :(
                }
            }
        });
    }

    private void getUser(){
        UUID workTagUniqueId = UUID.randomUUID();
        OneTimeWorkRequest checkConnectivityWork = new OneTimeWorkRequest.Builder(GetUserWorker.class)
                .setConstraints(new Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build())
                .setInputData(new Data.Builder().putString("key_user_id", USER_ID).build())
                .addTag(workTagUniqueId.toString())
                .build();

        WorkManager.getInstance().enqueue(checkConnectivityWork);

        WorkManager.getInstance().getWorkInfosByTagLiveData(workTagUniqueId.toString()).observe(this, new Observer<List<WorkInfo>>() {
            @Override
            public void onChanged(List<WorkInfo> workInfos) {
                // we know there will be only 1 work info in this list - the 1 work with that specific tag!
                // there might be some time until this worker is finished to work (in the mean team we will get an empty list
                // so check for that
                if (workInfos == null || workInfos.isEmpty())
                    return;

                WorkInfo info = workInfos.get(0);

                // now we can use it
                String userAsJson = info.getOutputData().getString("key_output_user");
                Log.d(TAG, "got user: " + userAsJson);

                user = new Gson().fromJson(userAsJson, User.class);
                // update UI with the user we got
            }
        });
    }

    private void getAllTicketsForUser(){
        UUID workTagUniqueId = UUID.randomUUID();
        OneTimeWorkRequest checkConnectivityWork = new OneTimeWorkRequest.Builder(GetUserWorker.class)
                .setConstraints(new Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build())
                .setInputData(new Data.Builder().putString("key_user_id", USER_ID).build())
                .addTag(workTagUniqueId.toString())
                .build();

        WorkManager.getInstance().enqueue(checkConnectivityWork);

        WorkManager.getInstance().getWorkInfosByTagLiveData(workTagUniqueId.toString()).observe(this, new Observer<List<WorkInfo>>() {
            @Override
            public void onChanged(List<WorkInfo> workInfos) {
                // we know there will be only 1 work info in this list - the 1 work with that specific tag!
                // there might be some time until this worker is finished to work (in the mean team we will get an empty list
                // so check for that
                if (workInfos == null || workInfos.isEmpty())
                    return;

                WorkInfo info = workInfos.get(0);

                // now we can use it
                String ticketsAsJson = info.getOutputData().getString("key_output_tickets");
                List<Ticket> allTickets = new Gson().fromJson(ticketsAsJson, new TypeToken<List<Ticket>>(){}.getType());

                Log.d(TAG, "got tickets list with size " + allTickets.size());


                // update UI with the list we got
            }
        });
    }


    private void createSampleTicket(){
        Ticket ticket = new Ticket();
        ticket.id = 0;
        ticket.user_id = Integer.valueOf(USER_ID);
        ticket.title = "mock ticket";
        ticket.completed = false;

        String ticketAsJson = new Gson().toJson(ticket);

        UUID workTagUniqueId = UUID.randomUUID();
        OneTimeWorkRequest checkConnectivityWork = new OneTimeWorkRequest.Builder(CreateNewTicketWorker.class)
                .setConstraints(new Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build())
                .setInputData(new Data.Builder().putString("key_input_ticket", ticketAsJson).build())
                .addTag(workTagUniqueId.toString())
                .build();

        WorkManager.getInstance().enqueue(checkConnectivityWork);

        WorkManager.getInstance().getWorkInfosByTagLiveData(workTagUniqueId.toString()).observe(this, new Observer<List<WorkInfo>>() {
            @Override
            public void onChanged(List<WorkInfo> workInfos) {
                // we know there will be only 1 work info in this list - the 1 work with that specific tag!
                // there might be some time until this worker is finished to work (in the mean team we will get an empty list
                // so check for that
                if (workInfos == null || workInfos.isEmpty())
                    return;

                WorkInfo info = workInfos.get(0);

                // now we can use it
                String ticketAsJson = info.getOutputData().getString("key_output");
                Log.d(TAG, "got created ticket: " + ticketAsJson);
                Ticket ticketResponse = new Gson().fromJson(ticketAsJson, Ticket.class);

                // update UI with the ticket response.
            }
        });
    }
}
