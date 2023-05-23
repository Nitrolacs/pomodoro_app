package com.example.pomodoroapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pomodoroapp.databinding.ActivityDeleteTimerConfigurationBinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Класс для удаления конфигураций таймера
 */
public class DeleteTimerConfigurationActivity extends AppCompatActivity {

    /**
     * Переменная для доступа к элементам Activity
     */
    private ActivityDeleteTimerConfigurationBinding binding;

    private static Bridge bridge;

    /**
     * Адаптер
     */
    private ArrayAdapter<String> adapter;

    /**
     * Переход на главную Activity
     */
    private void startMainActivity() {
        Intent intent = new Intent(DeleteTimerConfigurationActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }

    /**
     * Метод для показа диалога с подтверждением удаления
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
            adapter.notifyDataSetChanged();
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
     * Метод, вызываемый при создании Activity
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


        binding.listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // получаем название конфигурации по позиции
                String name = adapter.getItem(position);
                // вызываем метод для показа диалога с подтверждением удаления и передаем ему позицию элемента
                showDeleteDialog(name);
            }
        });
    }
}

