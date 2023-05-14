package com.example.pomodoroapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pomodoroapp.databinding.ActivityTimerConfigurationBinding;

import java.util.Objects;


public class TimerConfigurationActivity extends AppCompatActivity {

    private ActivityTimerConfigurationBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTimerConfigurationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.buttonSaveConfiguration.setOnClickListener(v -> {
            String configurationName = Objects.requireNonNull(binding.fieldConfigurationName.getText()).toString();
            String focusingTime = Objects.requireNonNull(binding.fieldFocusingTime.getText()).toString();
            String restTime = Objects.requireNonNull(binding.fieldRestTime.getText()).toString();
            String roundsNumber = Objects.requireNonNull(binding.fieldRoundsNumber.getText()).toString();

            // Получаем массив непрошедших проверку параметров
            String[] invalidParameters = InputChecker.getInputChecker().checkData(configurationName,
                    focusingTime, restTime, roundsNumber);

            // Проверяем, что массив не пустой
            if (invalidParameters.length > 0) {

                // Проходим по всем элементам массива с помощью цикла for-each
                for (String parameter : invalidParameters) {

                    // Сравниваем название параметра с названием поля
                    switch (parameter) {
                        case "configurationName":
                            binding.fieldConfigurationName.setError("Введите непустое значение");
                            break;
                        case "focusingTime":
                            binding.fieldFocusingTime.setError("Введите число от 1 до 60");
                            break;
                        case "restTime":
                            binding.fieldRestTime.setError("Введите число от 1 до 60");
                            break;
                        case "roundsNumber":
                            binding.fieldRoundsNumber.setError("Введите число от 1 до 20");
                            break;
                    }
                }

            } else {
                Intent intent = new Intent(TimerConfigurationActivity.this, MainActivity.class);
                intent.putExtra("name", configurationName.trim());
                intent.putExtra("focus", Integer.parseInt(focusingTime));
                intent.putExtra("rest", Integer.parseInt(restTime));
                intent.putExtra("rounds", Integer.parseInt(roundsNumber));
                startActivity(intent);
            }
        });
    }
}