package com.example.pomodoroapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pomodoroapp.databinding.ActivityTimerConfigurationBinding;

import java.util.Objects;


public class TimerConfigurationActivity extends AppCompatActivity {

    private ActivityTimerConfigurationBinding binding;

    private void startMainActivity(String configurationName, String focusingTime, String restTime,
                                   String roundsNumber) {
        Intent intent = new Intent(TimerConfigurationActivity.this, MainActivity.class);
        intent.putExtra("name", configurationName.trim());
        intent.putExtra("focus", Integer.parseInt(focusingTime));
        intent.putExtra("rest", Integer.parseInt(restTime));
        intent.putExtra("rounds", Integer.parseInt(roundsNumber));
        startActivity(intent);
    }

    private void checkInput(String configurationName, String focusingTime, String restTime,
                       String roundsNumber) {
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
            startMainActivity(configurationName, focusingTime, restTime, roundsNumber);
        }
    }

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

            checkInput(configurationName, focusingTime, restTime, roundsNumber);
        });
    }
}