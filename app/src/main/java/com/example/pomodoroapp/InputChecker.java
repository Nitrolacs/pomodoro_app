package com.example.pomodoroapp;

import java.util.ArrayList;
import java.util.List;

/**
 * Класс для проверки ввода пользователя, здесь используется шаблон проектирования Singleton
 */
public class InputChecker {
    /**
     * Создаём экземпляр
     */
    private static InputChecker inputChecker;

    /**
     * Возвращает экземпляр InputChecker
     *
     * @return экземпляр InputChecker
     */
    public static InputChecker getInputChecker() {
        if (inputChecker == null) {
            inputChecker = new InputChecker();
        }

        return inputChecker;
    }

    /**
     * Приватный пустой конструктор
     */
    private InputChecker() {

    }

    /**
     * Проверяет введённые пользователем данные
     *
     * @param configurationName Название конфигурации
     * @param focusingTime Время фокусирования
     * @param restTime Время отдыха
     * @param roundsNumber Количество циклов
     */
    public String[] checkData(String configurationName, String focusingTime, String restTime,
                             String roundsNumber) {
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
