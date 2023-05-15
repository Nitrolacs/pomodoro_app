package com.example.pomodoroapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pomodoroapp.databinding.ActivityTimerConfigurationBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Класс для настройки конфигурации таймера
 */
public class TimerConfigurationActivity extends AppCompatActivity {

    /**
     * Объект привязки, который содержит ссылки на представления в макете activity_timer_configuration.
     */
    private ActivityTimerConfigurationBinding binding;

    /**
     * Название конфигурации
     */
    private static String configurationName;

    /**
     * Время фокусирования
     */
    private static String focusingTime;

    /**
     * Время отдыха
     */
    private static String restTime;

    /**
     * Количество циклов
     */
    private static String roundsNumber;

    /**
     * Запускает Activity с фокусировочным таймером
     */
    private void startMainActivity() {
        Intent intent = new Intent(TimerConfigurationActivity.this, MainActivity.class);
        intent.putExtra("name", configurationName.trim());
        intent.putExtra("focus", Integer.parseInt(focusingTime));
        intent.putExtra("rest", Integer.parseInt(restTime));
        intent.putExtra("rounds", Integer.parseInt(roundsNumber));
        startActivity(intent);
    }

    /**
     * Вызывает проверку параметров конфигурации таймера
     */
    private void checkInput() {
        // Получаем массив непрошедших проверку параметров
        String[] invalidParameters = InputChecker.checkData();

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
            startMainActivity();
        }
    }

    /**
     * Получает значения из полей ввода
     */
    private void getFieldsValues() {
        configurationName = Objects.requireNonNull(binding.fieldConfigurationName.getText()).toString();
        focusingTime = Objects.requireNonNull(binding.fieldFocusingTime.getText()).toString();
        restTime = Objects.requireNonNull(binding.fieldRestTime.getText()).toString();
        roundsNumber = Objects.requireNonNull(binding.fieldRoundsNumber.getText()).toString();
    }

    /**
     * Класс для проверки ввода пользователя.
     */
    static class InputChecker {

        /**
         * Проверяет введённые пользователем данные
         */
        private static String[] checkData() {
            // Создаем пустой список для хранения названий непрошедших проверку параметров
            List<String> invalidParameters = new ArrayList<>();

            if (configurationName.trim().isEmpty()) {
                invalidParameters.add("configurationName");
            }

            if (focusingTime.isEmpty() || Integer.parseInt(focusingTime) == 0 ||
                    Integer.parseInt(focusingTime) > 60) {
                invalidParameters.add("focusingTime");
            }

            if (restTime.isEmpty() || Integer.parseInt(restTime) == 0 ||
                    Integer.parseInt(restTime) > 60) {
                invalidParameters.add("restTime");
            }

            if (roundsNumber.isEmpty() || Integer.parseInt(roundsNumber) == 0 ||
                    Integer.parseInt(roundsNumber) > 20) {
                invalidParameters.add("roundsNumber");
            }

            // Преобразуем список в массив и возвращаем его
            return invalidParameters.toArray(new String[0]);
        }
    }


    /**
     * Вызывается при создании TimerConfigurationActivity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTimerConfigurationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.buttonSaveConfiguration.setOnClickListener(v -> {
            getFieldsValues();
            checkInput();
        });
    }
}