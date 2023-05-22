package com.example.pomodoroapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.pomodoroapp.databinding.ActivityMainBinding;
import com.google.android.material.navigation.NavigationView;

import java.util.Map;

/**
 * Главная Activity с таймером
 */
public class MainActivity extends AppCompatActivity {
    private static ActivityMainBinding binding;
    private static Bridge bridge;

    /**
     * Метод, вызываемый при создании Activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        bridge = Bridge.getBridge();
        setContentView(binding.getRoot());

        binding.sideBarButton.setOnClickListener(v ->
                binding.drawerLayout.openDrawer(GravityCompat.START));

        binding.timerSettings.setOnClickListener(v -> {
            // вызываем метод для показа диалога
            showSettingsDialog();
        });

        binding.navigationView.setNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.configuration) {
                transitionToNewActivity(TimerConfigurationActivity.class);
            }
            if (item.getItemId() == R.id.delete_configuration) {
                transitionToNewActivity(DeleteTimerConfigurationActivity.class);
            }
            if (item.getItemId() == R.id.cutomization) {
                transitionToNewActivity(SettingsActivity.class);
            }
            return true;
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

    private void timerEnvironmentSetting() {
        if (bridge.isRest()) {
            setStopButton();
        }
        bridge.timerPreparation();
        setupStartTimerView();
    }

    private void setupStartTimerView() {
        // Set Rounds Text
        binding.studyStageNumber.setText(bridge.getRounds());
        bridge.setStartTimer();

        // Reset Button
        binding.buttonStop.setOnClickListener(v -> bridge.resetOrStart());
    }

    public static void setupFocusingTimerView() {
        setStageNumber(bridge.getRounds());
        bridge.setFocusingTimer();
    }

    public static void setupRestTimerView() {
        bridge.setRestTimer();
    }

    public static void setStageText(String text) {
        binding.studyStageText.setText(text);
    }

    public static void setProgressBar(int seconds) {
        binding.progressBar.setProgress(seconds);
    }

    public static void setMaxProgressBar(int max) {
        binding.progressBar.setMax(max);
    }

    public static void setStageNumber(String text) {
        binding.studyStageNumber.setText(text);
    }

    public static void updateTimerText(String timeLabel) {
        binding.timer.setText(timeLabel);
    }

    /**
     * Вызов диалога
     */
    private void showSettingsDialog() {

        // создаем билдер для диалога
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // устанавливаем заголовок диалога
        builder.setTitle("Выберите конфигурацию таймера");

        // создаем разметку для диалога с Spinner
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.dialog_with_spinner, null);

        // находим Spinner в разметке
        Spinner spinner = (Spinner) layout.findViewById(R.id.spinner);

        // создаем адаптер для Spinner с массивом названий конфигураций
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                bridge.getConfigurationNames("ConfigurationsPrefs",
                                                         "_focusingTime", this));

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // устанавливаем адаптер для Spinner
        spinner.setAdapter(adapter);

        // устанавливаем слушатель выбора элемента для Spinner
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                bridge.getSelectedConfiguration(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // do nothing
            }
        });

        // устанавливаем разметку для диалога
        builder.setView(layout);

        // устанавливаем кнопку ОК и слушатель нажатия на нее
        builder.setPositiveButton("Ок", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (!bridge.saveSelectedConfiguration()) {
                    Toast.makeText(MainActivity.this, "Не создана конфигурация",
                            Toast.LENGTH_SHORT).show();
                } else {
                    timerEnvironmentSetting();
                }
            }
        });

        // в методе showConfigurationDialog()
        // после установки кнопки ОК добавляем кнопку Отмена
        builder.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                bridge.resetTmpFields();

                // закрываем диалог
                dialog.dismiss();
            }
        });

        // создаем и показываем диалог
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public static void setStopButton() {
        binding.buttonStop.setImageResource(R.drawable.ic_stop);
    }

    public static void setStartButton() {
        binding.buttonStop.setImageResource(R.drawable.ic_play);
    }

    public static void finishTimer() {
        binding.studyStageText.setText("Вы закончили все циклы :)");
    }
}