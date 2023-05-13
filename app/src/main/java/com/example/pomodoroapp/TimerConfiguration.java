package com.example.pomodoroapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.pomodoroapp.databinding.ActivityMainBinding;

public class TimerConfiguration extends AppCompatActivity {

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
                Intent intent = new Intent(TimerConfiguration.this, MainActivity.class);
                startActivity(intent);
            }
        });


    }
}

/*
public class TimerConfiguration extends AppCompatActivity {
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        binding.tvStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String studyTime = binding.etStudy.getText().toString();
                String breakTime = binding.etBreak.getText().toString();
                String roundCount = binding.etRound.getText().toString();

                if (!studyTime.isEmpty() && !breakTime.isEmpty() && !roundCount.isEmpty()) {
                    Intent intent = new Intent(MainActivity.this, FeedActivity.class);
                    intent.putExtra("study", Integer.parseInt(studyTime));
                    intent.putExtra("break", Integer.parseInt(breakTime));
                    intent.putExtra("round", Integer.parseInt(roundCount));
                    startActivity(intent);
                } else {
                    Toast.makeText(MainActivity.this, "Fill fields above", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
 */