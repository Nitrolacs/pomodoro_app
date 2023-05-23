package com.example.pomodoroapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pomodoroapp.databinding.ActivityDeleteTimerConfigurationBinding;

import java.util.Arrays;

/**
 * Класс для удаления конфигураций таймера
 */
public class DeleteTimerConfigurationActivity extends AppCompatActivity {

    /**
     * Поле для доступа к элементам Activity
     */
    private ActivityDeleteTimerConfigurationBinding binding;

    /**
     * Мост
     */
    private static Bridge bridge;

    /**
     * Адаптер
     */
    private ArrayAdapter<String> adapter;

    /**
     * Переходит на главную Activity
     */
    private void startMainActivity() {
        Intent intent = new Intent(DeleteTimerConfigurationActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }

    /**
     * Показывает диалог с подтверждением удаления
     * @param name Название удаляемой конфигурации
     */
    private void showDeleteDialog(String name) {
        // создаем билдер для диалога
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // устанавливаем сообщение диалога
        builder.setMessage("Вы уверены, что хотите удалить конфигурацию " + name + "?");
        // устанавливаем кнопку Да и слушатель нажатия на нее

        builder.setPositiveButton("Да", (dialog, which) -> {
            // вызываем метод для удаления записи из SharedPreferences
            bridge.deleteConfiguration(name);
            startMainActivity();
        });

        // устанавливаем кнопку Нет и слушатель нажатия на нее
        builder.setNegativeButton("Нет", (dialog, which) -> {
            // закрываем диалог
            dialog.dismiss();
        });

        // создаем и показываем диалог
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * Вызывается при создании Activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDeleteTimerConfigurationBinding.inflate(getLayoutInflater());
        bridge = Bridge.getBridge();
        setContentView(binding.getRoot());

        adapter = new MyListAdapter(this,
                Arrays.asList(bridge.getConfigurationNames("ConfigurationsPrefs",
                        "_focusingTime", this)),
                              bridge.getConfigurationsParameters());
        binding.listView.setAdapter(adapter);

        if (!bridge.getConfigurationsParameters().isEmpty()) {
            Toast.makeText(DeleteTimerConfigurationActivity.this,
                    "Нажмите на удаляемую строку",
                    Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(DeleteTimerConfigurationActivity.this,
                    "Конфигурации ещё не добавлены",
                    Toast.LENGTH_SHORT).show();
        }

        binding.backButton.setOnClickListener(v -> startMainActivity());

        binding.listView.setOnItemClickListener((parent, view, position, id) -> {
            // получаем название конфигурации по позиции
            String name = adapter.getItem(position);
            // вызываем метод для показа диалога с подтверждением удаления и передаем ему позицию элемента
            showDeleteDialog(name);
        });
    }
}

