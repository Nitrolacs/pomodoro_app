package com.example.pomodoroapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Switch;

import com.example.pomodoroapp.databinding.ActivityDeleteTimerConfigurationBinding;
import com.example.pomodoroapp.databinding.ActivitySettingsBinding;

/**
 * Класс настроек
 */
public class SettingsActivity extends AppCompatActivity {

    private ActivitySettingsBinding binding;
    private boolean nightMode;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private void startMainActivity() {
        Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sharedPreferences = getSharedPreferences("MODE", Context.MODE_PRIVATE);
        nightMode = sharedPreferences.getBoolean("night", false);

        if (nightMode) {
            binding.switcher.setChecked(true);
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }

        binding.switcher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (nightMode) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    editor = sharedPreferences.edit();
                    editor.putBoolean("night", false);
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    editor = sharedPreferences.edit();
                    editor.putBoolean("night", true);
                }
                editor.apply();
            }
        });

        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startMainActivity();
            }
        });
    }
}
