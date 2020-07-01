package com.example.myserverapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class LoginActivity extends AppCompatActivity {
    private EditText usernameInput;
    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sp = getApplicationContext().getSharedPreferences("myPreferences", Context.MODE_PRIVATE);
        usernameInput = findViewById(R.id.et_username);

        if (!sp.getBoolean("isFirstTime", true)) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }

    }

    public void loginOnClick(View view){
        sp.edit().putString("username", usernameInput.getText().toString()).apply();
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }


}
