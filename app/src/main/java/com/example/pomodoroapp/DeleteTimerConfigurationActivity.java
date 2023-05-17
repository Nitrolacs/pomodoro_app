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
import java.util.List;
import java.util.Map;

public class DeleteTimerConfigurationActivity extends AppCompatActivity {

    private ActivityDeleteTimerConfigurationBinding binding;

    private ArrayAdapter<String> adapter;

    private List<String> configurationNames;

    private void startMainActivity() {
        Intent intent = new Intent(DeleteTimerConfigurationActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }

    // создаем метод для удаления записи из SharedPreferences по названию конфигурации
    private void deleteConfiguration(String name) {
        // получаем SharedPreferences
        SharedPreferences sp = getSharedPreferences("ConfigurationsPrefs", Context.MODE_PRIVATE);
        // получаем редактор SharedPreferences
        SharedPreferences.Editor editor = sp.edit();
        // удаляем все ключи, связанные с названием конфигурации
        editor.remove(name + "_focusingTime");
        editor.remove(name + "_restTime");
        editor.remove(name + "_roundsNumber");
        // применяем изменения
        editor.apply();

        configurationNames.remove(name);

        adapter.notifyDataSetChanged();
    }

    // создаем метод для показа диалога с подтверждением удаления
    private void showDeleteDialog(String name) {
        // создаем билдер для диалога
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // устанавливаем сообщение диалога
        builder.setMessage("Вы уверены, что хотите удалить конфигурацию " + name + "?");
        // устанавливаем кнопку Да и слушатель нажатия на нее
        builder.setPositiveButton("Да", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // вызываем метод для удаления записи из SharedPreferences
                deleteConfiguration(name);
            }
        });
        // устанавливаем кнопку Нет и слушатель нажатия на нее
        builder.setNegativeButton("Нет", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // закрываем диалог
                dialog.dismiss();
            }
        });
        // создаем и показываем диалог
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDeleteTimerConfigurationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // получаем SharedPreferences
        SharedPreferences sp = getSharedPreferences("ConfigurationsPrefs", Context.MODE_PRIVATE);

        // получаем все записи из SharedPreferences
        Map<String, ?> allEntries = sp.getAll();

        // создаем список для хранения названий конфигураций
        configurationNames = new ArrayList<>();
        List<String> configurationParameters = new ArrayList<>();

        // перебираем все записи и добавляем в список только те ключи, которые заканчиваются на "_focusingTime"
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            String key = entry.getKey();
            if (key.endsWith("_focusingTime")) {
                // убираем суффикс "_focusingTime" и добавляем в список
                String name = key.substring(0, key.length() - 13);
                configurationNames.add(name);

                // получаем значения параметров по ключам
                String focusingTime = sp.getString(name + "_focusingTime", "0");
                String restTime = sp.getString(name + "_restTime", "0");
                String roundsNumber = sp.getString(name + "_roundsNumber", "0");
                // формируем строку с параметрами
                String parameters = focusingTime + ":" + restTime + ":" + roundsNumber;
                // добавляем строку в список параметров
                configurationParameters.add(parameters);
            }
        }

        // преобразуем список в массив строк
        String[] namesArray = configurationNames.toArray(new String[0]);
        //String[] parametersArray = configurationParameters.toArray(new String[0]);

        //adapter = new ArrayAdapter<>(this,
        //        R.layout.name_item, R.id.item_name, namesArray);
        adapter = new MyListAdapter(this, configurationNames, configurationParameters);
        binding.listView.setAdapter(adapter);

        if (namesArray.length != 0) {
            Toast.makeText(DeleteTimerConfigurationActivity.this,
                    "Нажмите на удаляемую строку",
                    Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(DeleteTimerConfigurationActivity.this,
                    "Конфигурации ещё не добавлены",
                    Toast.LENGTH_SHORT).show();
        }

        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startMainActivity();
            }
        });


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

