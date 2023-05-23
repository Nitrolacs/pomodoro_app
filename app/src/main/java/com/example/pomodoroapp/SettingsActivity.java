package com.example.pomodoroapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.pomodoroapp.databinding.ActivitySettingsBinding;

/**
 * Класс настроек
 */
public class SettingsActivity extends AppCompatActivity {

    private void startMainActivity() {
        Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        com.example.pomodoroapp.databinding.ActivitySettingsBinding binding =
                ActivitySettingsBinding.inflate(getLayoutInflater());
        Bridge bridge = Bridge.getBridge();
        setContentView(binding.getRoot());

        boolean nightMode = bridge.getNightModeBoolean("MODE", "night", this);

        if (nightMode) {
            binding.switcher.setChecked(true);
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }

        binding.switcher.setOnClickListener(v -> {
            if (nightMode) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                bridge.saveNightModeBoolean("night", false);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                bridge.saveNightModeBoolean("night", true);
            }
        });

        binding.backButton.setOnClickListener(v -> startMainActivity());
    }
}
