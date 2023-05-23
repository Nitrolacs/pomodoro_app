package com.example.pomodoroapp;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Класс мостика, который связывает интерфейс и логику таймера.
 * Реализует паттерн проектирования синглтон.
 */
public class Bridge {

    /**
     * Единственный экземпляр класса мостика
     */
    private static Bridge bridge;

    /**
     * Название конфигурации таймера
     */
    private String nameConfiguration = null;

    /**
     * Длительность фокусирования в миллисекундах
     */
    private Integer focusMinutes = null;

    /**
     * Длительность отдыха в миллисекундах
     */
    private Integer restMinutes = null;

    /**
     * Количество раундов фокусирования
     */
    private Integer roundsCount = null;

    /**
     * Временное название конфигурации таймера
     */
    private String tmpNameConfiguration = null;

    /**
     * Временная длительность фокусировки в миллисекундах
     */
    private Integer tmpFocusMinutes = null;

    /**
     * Временная длительность отдыха в миллисекундах
     */
    private Integer tmpRestMinutes = null;

    /**
     * Временное количество раундов
     */
    private Integer tmpRoundsCount = null;

    /**
     * Текущий раунд (по умолчанию 1)
     */
    private int mRound = 1;

    /**
     * Флаг, указывающий на то, что идет фокусировка
     */
    private boolean isFocusing = true;

    /**
     * Флаг, указывающий на то, что идет отдых
     */
    private boolean isRest = false;

    /**
     * Таймер фокусировки
     */
    private FocusTimer focusTimer;

    /**
     * Таймер отдыха
     */
    private RestTimer restTimer;

    /**
     * Таймер подготовки
     */
    private StartTimer startTimer;

    /**
     * Класс хранилище
     */
    private SharedPreferences sharedPreferences;

    /**
     * Редактор хранилища
     */
    private SharedPreferences.Editor editor;

    /**
     * Массив названия конфигураций
     */
    private String[] namesArray;

    /**
     * Список названий конфигураций
     */
    private List<String> configurationNames;

    /**
     * Список параметров конфигураций
     */
    private List<String> configurationParameters;

    /**
     * Получает единственный экземпляр класса мостика.
     */
    public static synchronized Bridge getBridge() {
        if (bridge == null) { // если экземпляр еще не создан, то создаем его
            bridge = new Bridge();
        }
        return bridge; // возвращаем экземпляр класса мостика
    }

    /**
     * Приватный конструктор класса мостика.
     */
    private Bridge() {
    }

    /**
     * ПОлучает список названий конфигураций таймера из SharedPreferences.
     * @param fileName Название файла
     * @param endsWith Окончание ключа
     * @param context Контекст
     * @return Массив названий конфигураций
     */
    public String[] getConfigurationNames(String fileName, String endsWith, Context context) {
        // получаем SharedPreferences
        sharedPreferences = context.getSharedPreferences(fileName,
                Context.MODE_PRIVATE);

        // получаем все записи из SharedPreferences
        Map<String, ?> allEntries = sharedPreferences.getAll();

        // создаем список для хранения названий конфигураций
        configurationNames = new ArrayList<>();
        configurationParameters = new ArrayList<>();

        // перебираем все записи и добавляем в список только те ключи, которые заканчиваются на "_focusingTime"
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            String key = entry.getKey();
            if (key.endsWith(endsWith)) {
                String name = key.substring(0, key.length() - 13);
                // убираем суффикс "_focusingTime" и добавляем в список
                configurationNames.add(name);

                // получаем значения параметров по ключам
                String focusingTime = sharedPreferences.getString(name + "_focusingTime", "0");
                String restTime = sharedPreferences.getString(name + "_restTime", "0");
                String roundsNumber = sharedPreferences.getString(name + "_roundsNumber", "0");
                // формируем строку с параметрами
                String parameters = focusingTime + ":" + restTime + ":" + roundsNumber;
                // добавляем строку в список параметров
                configurationParameters.add(parameters);
            }
        }

        // преобразуем список в массив строк
        namesArray = configurationNames.toArray(new String[0]);

        // преобразуем список в массив строк и возвращаем его
        return namesArray;
    }

    /**
     * Удаляет конфигурацию
     * @param name Название удаляемой конфигурации
     */
    public void deleteConfiguration(String name) {
        // получаем редактор SharedPreferences
        editor = sharedPreferences.edit();
        // удаляем все ключи, связанные с названием конфигурации
        editor.remove(name + "_focusingTime");
        editor.remove(name + "_restTime");
        editor.remove(name + "_roundsNumber");
        // применяем изменения
        editor.apply();

        configurationNames.remove(name);
    }

    /**
     * Сохранение новой конфигурации
     * @param fileName Название файла
     * @param endsWith Окончание ключа
     * @param context Контекст
     * @param configurationName Название конфигурации
     * @param focusingTime Время фокусирования
     * @param restTime Время отдыха
     * @param roundsNumber Количество раундов фокусирования
     * @return Успешность сохранения
     */
    public boolean saveConfigurationSettings(String fileName, String endsWith, Context context,
                                             String configurationName, String focusingTime,
                                             String restTime, String roundsNumber) {
        sharedPreferences = context.getSharedPreferences(fileName,
                Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        if (sharedPreferences.contains(configurationName + endsWith)) {
            return false;
        }

        editor.putString(configurationName + "_focusingTime", focusingTime);
        editor.putString(configurationName + "_restTime", restTime);
        editor.putString(configurationName + "_roundsNumber", roundsNumber);
        editor.apply();

        return true;
    }

    /**
     * Возвращает параметры конфигураций
     * @return Список параметров конфигураций
     */
    public List<String> getConfigurationsParameters() {
        return configurationParameters;
    }

    /**
     * Получает данные выбранной конфигурации
     * @param numberOfConfiguration Номер конфигурации
     */
    public void getSelectedConfiguration(int numberOfConfiguration) {
        // получаем выбранное название конфигурации
        tmpNameConfiguration = namesArray[numberOfConfiguration];

        // получаем настройки по этому названию из SharedPreferences
        tmpFocusMinutes = Integer.parseInt(sharedPreferences.getString(tmpNameConfiguration +
                        "_focusingTime",
                "0")) * 60 * 1000;
        tmpRestMinutes = Integer.parseInt(sharedPreferences.getString(tmpNameConfiguration +
                        "_restTime",
                "0")) * 60 * 1000;
        tmpRoundsCount = Integer.valueOf(sharedPreferences.getString(tmpNameConfiguration
                        + "_roundsNumber",
                "0"));
    }

    /**
     * Проверяет возможность сохранить конфигурацию
     * @return Возможность сохранения
     */
    public boolean saveSelectedConfiguration() {
        if (tmpNameConfiguration == null) {
            return false;
        } else {
            nameConfiguration = tmpNameConfiguration;
            focusMinutes = tmpFocusMinutes;
            restMinutes = tmpRestMinutes;
            roundsCount = tmpRoundsCount;
            return true;
        }
    }

    /**
     * Получает значение темной темы из файла
     * @param name Название файла
     * @param key Ключ
     * @param context Контекст
     * @return Значение темы
     */
    public boolean getNightModeBoolean(String name, String key, Context context) {
        sharedPreferences = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(key, false);
    }

    /**
     * Сохраняет выбранную тему
     * @param key Ключ
     * @param value Значение
     */
    public void saveNightModeBoolean(String key, boolean value) {
        editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    /**
     * Очищает временные поля
     */
    public void resetTmpFields() {
        tmpNameConfiguration = null; // обнуляем временное название конфигурации таймера
        tmpFocusMinutes = null; // обнуляем временную длительность фокусировки в миллисекундах
        tmpRestMinutes = null; // обнуляем временную длительность отдыха в миллисекундах
        tmpRoundsCount = null; // обнуляем временное количество раундов
    }

    /**
     * Подготавливает таймер
     */
    public void timerPreparation() {
        isFocusing = true; // устанавливаем флаг фокусировки в true
        isRest = false; // устанавливаем флаг отдыха в false
        stopAllTimers(); // останавливаем все таймеры, если они запущены
    }

    /**
     * Останавливает таймеры, если они запущены
     */
    public void stopAllTimers() {
        if (restTimer != null) { // если таймер отдыха запущен, то останавливаем его
            restTimer.stop();
        }
        if (focusTimer != null) { // если таймер фокусировки запущен, то останавливаем его
            focusTimer.stop();
        }
        if (startTimer != null) { // если таймер подготовки запущен, то останавливаем его
            startTimer.stop();
        }
    }

    /**
     * Возвращает текстовое представление текущего раунда фокусирования
     * @return Текущий раунд
     */
    public String getRounds() {
        return mRound + "/" + roundsCount;
    }

    /**
     * Создаёт и запускает таймер подготовки
     */
    public void setStartTimer() {
        startTimer = new StartTimer(5000);
        startTimer.start();
    }

    /**
     * Создаёт и запускает таймер фокусирования
     */
    public void setFocusingTimer() {
        if (focusTimer != null) {
            focusTimer.stop();
        }

        focusTimer = new FocusTimer(focusMinutes.longValue());
        focusTimer.start();
    }

    /**
     * Создаёт и запускает таймер отдыха
     */
    public void setRestTimer() {
        if (restTimer != null) {
            restTimer.stop();
        }

        restTimer = new RestTimer(restMinutes.longValue());
        restTimer.start();
    }

    /**
     * Устанавливает текст с названием этапа
     * @param text Строка с названием этапа.
     */
    public void setStageText(String text) {
        // устанавливаем текст с названием этапа
        MainActivity.setStageText(text);
    }

    /**
     * Обновляет прогресс бар при каждом тике таймера
     * @param seconds оставшееся время в секундах.
     */
    public void setProgressBar(int seconds) {
        // устанавливаем текущее значение прогресс-бара равное оставшемуся времени в секундах
        MainActivity.setProgressBar(seconds);
    }

    /**
     * Устанавливает цифру с текущим этапом
     * @param text Значение, которое будет установлено
     */
    public void setStageNumber(String text) {
        MainActivity.setStageNumber(text);
    }

    /**
     * Устанавливает максимальное значенеи прогресс-бара
     * @param max максимальное значение прогресс-бара в секундах.
     */
    public void setMaxProgressBar(int max) {
        // устанавливаем максимальное значение прогресс-бара
        MainActivity.setMaxProgressBar(max);
    }

    /**
     * Обновляет текст таймера при каждом тике таймера фокусировки или отдыха.
     * @param timeLabel строка с оставшимся временем в формате mm:ss.
     */
    public void updateTimerText(String timeLabel) {
        MainActivity.updateTimerText(timeLabel);
    }

    /**
     * Проверяет текущий таймер
     */
    public void checkCurrentTimer() {
        if (isFocusing) {
            MainActivity.setupFocusingTimerView();
        } else {
            MainActivity.setupRestTimerView();
        }
    }

    /**
     * Выполняет действия после окончания таймера фокусирования
     */
    public void actionsAfterFocusing() {
        if (isLastRound()) {
            isFocusing = false;
            setStartTimer();
            mRound++;
        } else {
            clearAttributes();
            MainActivity.finishTimer();
        }
    }

    /**
     * Конвертирует число секунд в строку в формате mm:ss.
     * @param time число секунд.
     * @return строка в формате mm:ss.
     */
    public String createTimeLabels(int time) {
        String timeLabel = "";
        int minutes = time / 60;
        int seconds = time % 60;

        if (minutes < 10) timeLabel += "0";
        timeLabel += minutes + ":";

        if (seconds < 10) timeLabel += "0";
        timeLabel += seconds;

        return timeLabel;
    }

    /**
     * Сбрасывает либо запускает таймер
     */
    public void resetOrStart() {
        if (isRest) {
            MainActivity.setStopButton();
            setStartTimer();
            isRest = false;
        } else { // если не идет отдых, то очищаем все атрибуты
            clearAttributes();
        }
    }

    /**
     * Очищает все атрибуты в классе мостика и на экране.
     */
    public void clearAttributes() {
        // устанавливаем текст с названием этапа
        MainActivity.setStageText("Нажми кнопку старта для запуска");
        // меняем иконку кнопки сброса или запуска таймера на плей
        MainActivity.setStartButton();
        // обнуляем прогресс-бар
        MainActivity.setProgressBar(0);
        // обнуляем текст таймера
        MainActivity.updateTimerText("0");
        // сбрасываем номер раунда
        mRound = 1;
        // устанавливаем текст с номером раунда
        MainActivity.setStageNumber(getRounds());

        // останавливаем все таймеры, если они запущены
        stopAllTimers();

        // устанавливаем флаг отдыха в true
        isRest = true;
    }

    /**
     * Устанавливает фокусирование
     */
    public void setFocusing() {
        isFocusing = true;
    }

    /**
     * Проверяет, является ли текущий раунд последним.
     * @return true, если текущий раунд последний, false - в противном случае.
     */
    public boolean isLastRound() {
        return mRound < roundsCount;
    }

    /**
     * Возвращает состояние отдыха
     * @return
     */
    public boolean isRest() {
        return isRest;
    }

    /**
     * Получает длительность фокусировки в миллисекундах.
     * @return длительность фокусировки в миллисекундах.
     */
    public Integer getFocusMinutes() {
        return focusMinutes;
    }

    /**
     * Получает длительность отдыха в миллисекундах.
     * @return длительность отдыха в миллисекундах.
     */
    public Integer getRestMinutes() {
        return restMinutes;
    }
}
