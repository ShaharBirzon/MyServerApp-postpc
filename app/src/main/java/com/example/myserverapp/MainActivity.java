package com.example.myserverapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.myserverapp.data.TokenResponse;
import com.example.myserverapp.data.UserResponse;
import com.example.myserverapp.work.GetTokenWorker;
import com.example.myserverapp.work.GetUserWorker;
import com.example.myserverapp.work.PostNewPrettyNameWorker;
import com.google.gson.Gson;

import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    TokenResponse tokenResponse;
    private SharedPreferences sp;
    ProgressBar pb;
    TextView tokenTxt;
    TextView prettyNameTxt;
    TextView usernameTxt;
    ImageView userImg;
    private static String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // init views
        pb = findViewById(R.id.progress_bar);
        tokenTxt = findViewById(R.id.tv_token);
        prettyNameTxt = findViewById(R.id.tv_pretty);
        usernameTxt = findViewById(R.id.tv_username);
        userImg = findViewById(R.id.user_img);

        sp = getApplicationContext().getSharedPreferences("myPreferences", Context.MODE_PRIVATE);
        if (sp.getBoolean("isFirstTime", true)){ // check if first time
            sp.edit().putBoolean("isFirstTime", false).apply();

            String username = sp.getString("username", "");
            if(username.equals("")){
                Log.d(TAG, "error! cannot find username in sp");
            }
            else{
                getUserToken(username);
            }
        }

        else{
            String trJson = sp.getString("user_token", "");
            tokenResponse = new Gson().fromJson(trJson, TokenResponse.class);
            tokenTxt.setText(tokenResponse.data);
            usernameTxt.setText(sp.getString("username", ""));
            getUser();
        }
    }


    /**
     * get token for first time user
     * @param username
     */
    private void getUserToken(String username) {
        pb.setVisibility(View.VISIBLE);
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
                        if (workInfos == null || workInfos.isEmpty())
                            return;

                        if (workInfos.get(0).getState() == WorkInfo.State.FAILED ||
                                workInfos.get(0).getState() != WorkInfo.State.SUCCEEDED) {
                            return;
                        }

                        WorkInfo info = workInfos.get(0);

                        String tokenAsJson = info.getOutputData().getString("key_output_user");
                        Log.d(TAG, "got token: " + tokenAsJson);
                        sp.edit().putString("user_token", tokenAsJson).apply(); // adding token to sp

                        tokenResponse = new Gson().fromJson(tokenAsJson, TokenResponse.class);

                        tokenTxt.setText(tokenResponse.data);
                    }
                });
        pb.setVisibility(View.GONE);
    }

    /**
     * on click for saving pretty name
     * @param view
     */
    public void savePrettyOnClick(View view){
        TextView prettyNameTxt = findViewById(R.id.et_prettyname);
        setNewPrettyName(prettyNameTxt.getText().toString());
        prettyNameTxt.setText("");
    }

    /**
     * set pretty name in server
     * @param newName
     */
    public void setNewPrettyName(final String newName) {
        pb.setVisibility(View.VISIBLE);
        UUID workTagUniqueId = UUID.randomUUID();
        OneTimeWorkRequest oneTimeWorkRequest = new OneTimeWorkRequest
                .Builder(PostNewPrettyNameWorker.class)
                .setConstraints(new Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED).build())
                .setInputData(new Data.Builder().putString("key_get_new_pretty_name", newName)
                        .putString("key_get_token", tokenResponse.data).build())
                .addTag(workTagUniqueId.toString())
                .build();

        WorkManager.getInstance().enqueue(oneTimeWorkRequest);
        WorkManager.getInstance().getWorkInfosByTagLiveData(workTagUniqueId.toString())
                .observe(this, new Observer<List<WorkInfo>>() {
                    @Override
                    public void onChanged(List<WorkInfo> workInfos) {
                        if (workInfos == null || workInfos.isEmpty()) {
                            return;
                        }
                        if (workInfos.get(0).getState() == WorkInfo.State.FAILED ||
                                workInfos.get(0).getState() != WorkInfo.State.SUCCEEDED) {
                            return;
                        }

                        WorkInfo info = workInfos.get(0);
                        String AsJson = info.getOutputData().getString("key_get_pretty_name");
                        UserResponse userResponse = new Gson().fromJson(AsJson, UserResponse.class);

                        String userName = userResponse.data.pretty_name;
                        if (userName == null || userName.equals("")) {
                            userResponse.data.pretty_name = userResponse.data.username;
                        }

                        updateUserUI(userResponse);
                    }
                });

        pb.setVisibility(View.GONE);
    }

    /**
     * get logged in user data from server
     */
    private void getUser(){
        pb.setVisibility(View.VISIBLE);
        UUID workTagUniqueId = UUID.randomUUID();
        OneTimeWorkRequest oneTimeWorkRequest = new OneTimeWorkRequest
                .Builder(GetUserWorker.class)
                .setConstraints(new Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED).build())
                .setInputData(new Data.Builder().putString("key_get_info",
                        tokenResponse.data).build())
                .addTag(workTagUniqueId.toString())
                .build();

        WorkManager.getInstance().enqueue(oneTimeWorkRequest);
        WorkManager.getInstance().getWorkInfosByTagLiveData(workTagUniqueId.toString())
                .observe(this, new Observer<List<WorkInfo>>() {
                    @Override
                    public void onChanged(List<WorkInfo> workInfos) {
                        if (workInfos == null || workInfos.isEmpty()) {
                            return;
                        }
                        if (workInfos.get(0).getState() == WorkInfo.State.FAILED ||
                                workInfos.get(0).getState() != WorkInfo.State.SUCCEEDED) {
                            return;
                        }
                        WorkInfo info = workInfos.get(0);
                        String AsJson = info.getOutputData().getString("key_get_user");
                        UserResponse userResponse = new Gson().fromJson(AsJson, UserResponse.class);

                        String prettyName = userResponse.data.pretty_name;
                        if (prettyName == null || prettyName.equals("")) {  // in case pretty name not set
                            userResponse.data.pretty_name = userResponse.data.username;
                        }

                       updateUserUI(userResponse);

                    }
                });
        pb.setVisibility(View.GONE);
    }

    /**
     * update relevant ui for user
     * @param ur UserResponse object
     */
    private void updateUserUI(UserResponse ur){
        prettyNameTxt.setText(ur.data.pretty_name);
        usernameTxt.setText(ur.data.username);
        Glide.with(MainActivity.this)
                .load(Uri.parse("https://hujipostpc2019.pythonanywhere.com" + ur.data.image_url))
                .into(userImg);
    }

}
