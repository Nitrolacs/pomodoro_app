package com.example.pomodoroapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.pomodoroapp.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    // ViewBinding. activity_main ==> ActivityMainBinding
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.sideBarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, TimerConfiguration.class);
                startActivity(intent);
            }
        });
    }
}
