package com.example.pomodoroapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Класс мостика, который связывает интерфейс и логику таймера.
 * Реализует паттерн проектирования синглтон.
 */
public class Bridge {
    private static Bridge bridge; // единственный экземпляр класса мостика

    private String nameConfiguration = null; // название конфигурации таймера
    private Integer focusMinutes = null; // длительность фокусировки в миллисекундах
    private Integer restMinutes = null; // длительность отдыха в миллисекундах
    private Integer roundsCount = null; // количество раундов
    private String tmpNameConfiguration = null; // временное название конфигурации таймера
    private Integer tmpFocusMinutes = null; // временная длительность фокусировки в миллисекундах
    private Integer tmpRestMinutes = null; // временная длительность отдыха в миллисекундах
    private Integer tmpRoundsCount = null; // временное количество раундов
    private int mRound = 1; // текущий раунд
    private boolean isFocusing = true; // флаг, указывающий на то, что идет фокусировка
    private boolean isRest = false; // флаг, указывающий на то, что идет отдых


    private FocusTimer focusTimer; // переменная для таймера фокусировки
    private RestTimer restTimer; // переменная для таймера отдыха
    private StartTimer startTimer; // переменная для таймера подготовки


    private SharedPreferences sharedPreferences;

    private String[] namesArray;

    /**
     * Статический метод для получения единственного экземпляра класса мостика.
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
     * Метод для получения списка названий конфигураций таймера из SharedPreferences.
     */
    public String[] getConfigurationNames(String fileName, String endsWith, Context context) {
        // получаем SharedPreferences
        sharedPreferences = context.getSharedPreferences(fileName,
                Context.MODE_PRIVATE);

        // получаем все записи из SharedPreferences
        Map<String, ?> allEntries = sharedPreferences.getAll();

        // создаем список для хранения названий конфигураций
        List<String> configurationNames = new ArrayList<>();

        // перебираем все записи и добавляем в список только те ключи, которые заканчиваются на "_focusingTime"
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            String key = entry.getKey();
            if (key.endsWith(endsWith)) {
                // убираем суффикс "_focusingTime" и добавляем в список
                configurationNames.add(key.substring(0, key.length() - 13));
            }
        }

        // преобразуем список в массив строк
        namesArray = configurationNames.toArray(new String[0]);

        // преобразуем список в массив строк и возвращаем его
        return namesArray;
    }

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

    public void resetTmpFields() {
        tmpNameConfiguration = null; // обнуляем временное название конфигурации таймера
        tmpFocusMinutes = null; // обнуляем временную длительность фокусировки в миллисекундах
        tmpRestMinutes = null; // обнуляем временную длительность отдыха в миллисекундах
        tmpRoundsCount = null; // обнуляем временное количество раундов
    }

    /**
     * Метод для подготовки таймера к запуску.
     */
    public void timerPreparation() {
        isFocusing = true; // устанавливаем флаг фокусировки в true
        isRest = false; // устанавливаем флаг отдыха в false
        stopAllTimers(); // останавливаем все таймеры, если они запущены
    }

    /**
     * Метод для остановки всех таймеров, если они запущены.
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

    public String getRounds() {
        return mRound + "/" + roundsCount;
    }

    public void setStartTimer() {
        startTimer = new StartTimer(5000);
        startTimer.start();
    }

    public void setFocusingTimer() {
        if (focusTimer != null) {
            focusTimer.finish();
        }

        focusTimer = new FocusTimer(focusMinutes.longValue());
        focusTimer.start();
    }

    public void setRestTimer() {
        if (restTimer != null) {
            restTimer.finish();
        }

        restTimer = new RestTimer(restMinutes.longValue());
        restTimer.start();
    }


    /**
     * Метод для установки текста с названием этапа.
     * @param text строка с названием этапа.
     */
    public void setStageText(String text) {
        // устанавливаем текст с названием этапа
        MainActivity.setStageText(text);
    }

    /**
     * Метод для обновления прогресс-бара при каждом тике таймера фокусировки или отдыха.
     * @param seconds оставшееся время в секундах.
     */
    public void setProgressBar(int seconds) {
        // устанавливаем текущее значение прогресс-бара равное оставшемуся времени в секундах
        MainActivity.setProgressBar(seconds);
    }

    public void setStageNumber(String text) {
        MainActivity.setStageNumber(text);
    }

    /**
     * Метод для установки максимального значения прогресс-бара.
     * @param max максимальное значение прогресс-бара в секундах.
     */
    public void setMaxProgressBar(int max) {
        // устанавливаем максимальное значение прогресс-бара
        MainActivity.setMaxProgressBar(max);
    }

    /**
     * Метод для обновления текста таймера при каждом тике таймера фокусировки или отдыха.
     * @param timeLabel строка с оставшимся временем в формате mm:ss.
     */
    public void updateTimerText(String timeLabel) {
        // устанавливаем текст таймера равным строке с оставшимся временем
        MainActivity.updateTimerText(timeLabel);
    }

    public void checkCurrentTimer() {
        if (isFocusing) {
            MainActivity.setupFocusingTimerView();
        } else {
            MainActivity.setupRestTimerView();
        }
    }

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
     * Метод для конвертации числа секунд в строку в формате mm:ss.
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
     * Метод для сброса или запуска таймера.
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
     * Метод для очистки всех атрибутов в классе мостика и на экране.
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
     * Метод для получения значения флага фокусировки.
     * @return true, если идет фокусировка, false - в противном случае.
     */
    public boolean isFocusing() {
        return isFocusing;
    }

    public void setFocusing() {
        isFocusing = true;
    }

    /**
     * Метод для перехода к следующему раунду.
     */
    public void nextRound() {
        // увеличиваем номер раунда на единицу
        mRound++;
        // меняем флаг фокусировки на true
        isFocusing = true;
    }

    /**
     * Метод для проверки, является ли текущий раунд последним.
     * @return true, если текущий раунд последний, false - в противном случае.
     */
    public boolean isLastRound() {
        return mRound < roundsCount;
    }

    public boolean isRest() {
        return isRest;
    }

    /**
     * Метод для проверки, запущен ли какой-либо таймер.
     * @return true, если запущен хотя бы один таймер, false - в противном случае.
     */
    public boolean isTimerRunning() {
        return focusTimer != null || restTimer != null || startTimer != null;
    }

    /**
     * Метод для получения длительности фокусировки в миллисекундах.
     * @return длительность фокусировки в миллисекундах.
     */
    public Integer getFocusMinutes() {
        return focusMinutes;
    }

    /**
     * Метод для получения длительности отдыха в миллисекундах.
     * @return длительность отдыха в миллисекундах.
     */
    public Integer getRestMinutes() {
        return restMinutes;
    }
}
