package com.example.pomodoroapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.example.pomodoroapp.databinding.ActivityMainBinding;
import com.google.android.material.navigation.NavigationView;

import java.util.Map;

/**
 * Главная Activity с таймером
 */
public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private TimerBridge timerBridge;

    /**
     * Метод, вызываемый при создании Activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        // инициализируем экземпляр класса мостика

        timerBridge = TimerBridge.getInstance(this);
        binding.sideBarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        binding.timerSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // вызываем метод для показа диалога
                showSettingsDialog();
            }
        });

        binding.navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.configuration) {
                    if (timerBridge.isTimerRunning()) { // проверяем, запущен ли таймер
                        timerBridge.resetOrStart(); // если да, то сбрасываем его
                    }
                    transitionToNewActivity(TimerConfigurationActivity.class);
                }
                if (item.getItemId() == R.id.delete_configuration) {
                    transitionToNewActivity(DeleteTimerConfigurationActivity.class);
                }
                if (item.getItemId() == R.id.cutomization) {
                    transitionToNewActivity(SettingsActivity.class);
                }
                return true;
            }
        });
    }

    /**
     * Метод для перехода на новую Activity
     * @param cls Класс новой Activity
     */
    private void transitionToNewActivity(Class<?> cls) {
        binding.drawerLayout.closeDrawer(GravityCompat.START);
        Intent intent = new Intent(MainActivity.this, cls);
        startActivity(intent);
    }

    /**
     * Вызов диалога
     */
    private void showSettingsDialog() {
        // получаем SharedPreferences
        SharedPreferences sp = getSharedPreferences("ConfigurationsPrefs", Context.MODE_PRIVATE);
        // получаем все записи из SharedPreferences
        Map<String, ?> allEntries = sp.getAll();
        // создаем список для хранения названий конфигураций
        String[] configurationNames = timerBridge.getConfigurationNames(allEntries);
        // вызываем метод из класса мостика для установки разметки для диалога
        timerBridge.setDialogView(this);
        // вызываем метод из класса мостика для установки адаптера для Spinner
        timerBridge.setSpinnerAdapter(this, configurationNames);
        // вызываем метод из класса мостика для установки слушателя для Spinner
        timerBridge.setSpinnerListener(sp);
        // устанавливаем кнопку ОК и слушатель нажатия на нее
        timerBridge.setDialogPositiveButton(this);
        // устанавливаем кнопку Отмена и слушатель нажатия на нее
        timerBridge.setDialogNegativeButton();
        // создаем и показываем диалог
        timerBridge.showDialog();
    }
}