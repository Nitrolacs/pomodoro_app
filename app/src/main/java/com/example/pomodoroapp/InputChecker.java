package com.example.pomodoroapp;

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
     * @param
     * @param
     */
    public boolean checkData(String configurationName, int focusingTime, int restTime,
                             int roundsNumber) {
        if (configurationName.trim().isEmpty()) {
            return false;
        }
        if (focusingTime == 0 || focusingTime > 60) {
            return false;
        }
        if (restTime == 0 || restTime > 60) {
            return false;
        }

        return roundsNumber != 0 && roundsNumber <= 20;
    }
}
