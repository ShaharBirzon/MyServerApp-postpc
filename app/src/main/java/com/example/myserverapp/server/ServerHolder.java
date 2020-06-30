package com.example.myserverapp.server;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServerHolder {
    private static ServerHolder instance = null;

    public synchronized static ServerHolder getInstance() {
        if (instance != null)
            return instance;

        OkHttpClient client = new OkHttpClient.Builder()
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .client(client)
                .baseUrl("https://hujipostpc2019.pythonanywhere.com") // notice the absence of the last slash!
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        MyServerInterface serverInterface = retrofit.create(MyServerInterface.class);
        instance = new ServerHolder(serverInterface);
        return instance;
    }


    public final MyServerInterface serverInterface;

    private ServerHolder(MyServerInterface serverInterface) {
        this.serverInterface = serverInterface;
    }
}