package com.example.pomodoroapp;

import android.app.Activity;
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

import com.example.pomodoroapp.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Класс мостика, который связывает интерфейс и логику таймера.
 * Реализует паттерн проектирования синглтон.
 */
public class TimerBridge {
    private static TimerBridge instance; // единственный экземпляр класса мостика
    private Context context; // контекст приложения
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

    private Spinner spinner; // переменная для Spinner

    private FocusTimer focusTimer; // переменная для таймера фокусировки
    private RestTimer restTimer; // переменная для таймера отдыха
    private StartTimer startTimer; // переменная для таймера подготовки
    private AlertDialog.Builder builder;
    private ImageButton buttonStop; // переменная для кнопки старта или сброса таймера

    private TextView studyStageNumber;

    private TextView studyStageText;
    private TextView timer; // переменная для текста с оставшимся временем

    private ProgressBar progressBar; // переменная для прогресс-бара



    /**
     * Приватный конструктор класса мостика.
     * @param context контекст приложения.
     */
    private TimerBridge(Context context) {
        buttonStop = (ImageButton) ((Activity) context).findViewById(R.id.button_stop);
        studyStageNumber = (TextView) ((Activity) context).findViewById(R.id.study_stage_number);
        studyStageText = (TextView) ((Activity) context).findViewById(R.id.study_stage_text);
        progressBar = (ProgressBar) ((Activity) context).findViewById(R.id.progress_bar);
        timer = (TextView) ((Activity) context).findViewById(R.id.timer);

        this.context = context;
        // устанавливаем слушатель нажатия на кнопку сброса или запуска таймера
        buttonStop.setOnClickListener(v -> resetOrStart());
    }

    /**
     * Статический метод для получения единственного экземпляра класса мостика.
     * @param context контекст приложения.
     * @return экземпляр класса мостика.
     */
    public static TimerBridge getInstance(Context context) {
        if (instance == null) { // если экземпляр еще не создан, то создаем его
            instance = new TimerBridge(context);
        }
        return instance; // возвращаем экземпляр класса мостика
    }

    /**
     * Метод для получения списка названий конфигураций таймера из SharedPreferences.
     * @param allEntries все записи из SharedPreferences.
     * @return массив строк с названиями конфигураций таймера.
     */
    public String[] getConfigurationNames(Map<String, ?> allEntries) {
        // создаем список для хранения названий конфигураций
        List<String> configurationNames = new ArrayList<>();
        // перебираем все записи и добавляем в список только те ключи, которые заканчиваются на "_focusingTime"
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            String key = entry.getKey();
            if (key.endsWith("_focusingTime")) {
                // убираем суффикс "_focusingTime" и добавляем в список
                configurationNames.add(key.substring(0, key.length() - 13));
            }
        }
        // преобразуем список в массив строк и возвращаем его
        return configurationNames.toArray(new String[0]);
    }

    /**
     * Метод для установки адаптера для Spinner с массивом названий конфигураций таймера.
     * @param context контекст приложения.
     * @param namesArray массив строк с названиями конфигураций таймера.
     */
    public void setSpinnerAdapter(Context context, String[] namesArray) {
        // создаем адаптер для Spinner с массивом названий конфигураций таймера
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, namesArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // устанавливаем адаптер для Spinner
        spinner.setAdapter(adapter);
    }

    /**
     * Метод для установки слушателя выбора элемента для Spinner.
     * @param sp SharedPreferences для получения настроек по выбранному названию конфигурации таймера.
     */
    public void setSpinnerListener(SharedPreferences sp) {
        // устанавливаем слушатель выбора элемента для Spinner
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // получаем выбранное название конфигурации таймера
                tmpNameConfiguration = spinner.getSelectedItem().toString();
                // получаем настройки по этому названию из SharedPreferences
                tmpFocusMinutes = Integer.parseInt(sp.getString(tmpNameConfiguration + "_focusingTime", "0")) * 60 * 1000;
                tmpRestMinutes = Integer.parseInt(sp.getString(tmpNameConfiguration + "_restTime", "0")) * 60 * 1000;
                tmpRoundsCount = Integer.valueOf(sp.getString(tmpNameConfiguration + "_roundsNumber", "0"));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // do nothing
            }
        });
    }

    /**
     * Метод для установки разметки для диалога с выбором конфигурации таймера.
     * @param context контекст приложения.
     */
    public void setDialogView(Context context) {
        // создаем билдер для диалога
        builder = new AlertDialog.Builder(context);
        // устанавливаем заголовок диалога
        builder.setTitle("Выберите конфигурацию таймера");
        // создаем разметку для диалога с Spinner
        LayoutInflater inflater = LayoutInflater.from(context);
        View layout = inflater.inflate(R.layout.dialog_with_spinner, null);
        // находим Spinner в разметке
        spinner = (Spinner) layout.findViewById(R.id.spinner);
        // устанавливаем разметку для диалога
        builder.setView(layout);
    }

    /**
     * Метод для установки кнопки ОК и слушателя нажатия на нее для диалога с выбором конфигурации таймера.
     * @param context контекст приложения.
     */
    public void setDialogPositiveButton(Context context) {
        // устанавливаем кнопку ОК и слушатель нажатия на нее
        builder.setPositiveButton("Ок", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (tmpNameConfiguration == null) { // если не выбрано название конфигурации таймера, то показываем сообщение об ошибке
                    Toast.makeText(context, "Не создана конфигурация", Toast.LENGTH_SHORT).show();
                } else { // если выбрано название конфигурации таймера, то присваиваем значения переменным в классе мостика
                    nameConfiguration = tmpNameConfiguration;
                    focusMinutes = tmpFocusMinutes;
                    restMinutes = tmpRestMinutes;
                    roundsCount = tmpRoundsCount;
                    timerPreparation(); // вызываем метод для подготовки таймера к запуску
                    startTimerSetting(); // вызываем метод для запуска таймера
                }
            }
        });
    }

    /**
     * Метод для установки кнопки Отмена и слушателя нажатия на нее для диалога с выбором конфигурации таймера.
     */
    public void setDialogNegativeButton() {
        // устанавливаем кнопку Отмена и слушатель нажатия на нее
        builder.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                tmpNameConfiguration = null; // обнуляем временное название конфигурации таймера
                tmpFocusMinutes = null; // обнуляем временную длительность фокусировки в миллисекундах
                tmpRestMinutes = null; // обнуляем временную длительность отдыха в миллисекундах
                tmpRoundsCount = null; // обнуляем временное количество раундов
                // закрываем диалог
                dialog.dismiss();
            }
        });
    }

    /**
     * Метод для создания и показа диалога с выбором конфигурации таймера.
     */
    public void showDialog() {
        // создаем и показываем диалог
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * Метод для подготовки таймера к запуску.
     */
    public void timerPreparation() {
        isFocusing = true; // устанавливаем флаг фокусировки в true
        if (isRest) { // если идет отдых, то меняем иконку кнопки сброса или запуска таймера на стоп
            buttonStop.setImageResource(R.drawable.ic_stop);
        }
        isRest = false; // устанавливаем флаг отдыха в false
        stopAllTimers(); // останавливаем все таймеры, если они запущены
    }

    /**
     * Метод для остановки всех таймеров, если они запущены.
     */
    public void stopAllTimers() {
        if (focusTimer != null) { // если таймер фокусировки запущен, то останавливаем его
            focusTimer.stop();
        }
        if (restTimer != null) { // если таймер отдыха запущен, то останавливаем его
            restTimer.stop();
        }
        if (startTimer != null) { // если таймер подготовки запущен, то останавливаем его
            startTimer.stop();
        }
    }

    /**
     * Метод для запуска таймера подготовки к фокусировке или отдыху.
     */
    public void startTimerSetting() {
        // устанавливаем текст с номером раунда
        studyStageNumber.setText(mRound + "/" + roundsCount);
        // создаем и запускаем таймер подготовки
        startTimer = new StartTimer(context, 5500);
        startTimer.start();
        // устанавливаем слушатель нажатия на кнопку сброса или запуска таймера
        buttonStop.setOnClickListener(v -> resetOrStart());
    }

    /**
     * Метод для подготовки экрана к фокусировке.
     */
    public void setupFocusingView() {
        // устанавливаем текст с номером раунда
        studyStageNumber.setText(mRound + "/" + roundsCount);
        // устанавливаем текст с названием этапа
        studyStageText.setText("Время фокусирования");
        // устанавливаем максимальное значение прогресс-бара равное длительности фокусировки в секундах
        progressBar.setMax(focusMinutes / 1000);
    }

    /**
     * Метод для подготовки экрана к отдыху.
     */
    public void setupRestView() {
        // устанавливаем текст с названием этапа
        studyStageText.setText("Время отдыха");
        // устанавливаем максимальное значение прогресс-бара равное длительности отдыха в секундах
        progressBar.setMax(restMinutes / 1000);
    }

    /**
     * Метод для обновления прогресс-бара при каждом тике таймера фокусировки или отдыха.
     * @param seconds оставшееся время в секундах.
     */
    public void updateProgressBar(int seconds) {
        // устанавливаем текущее значение прогресс-бара равное оставшемуся времени в секундах
        progressBar.setProgress(seconds);
    }

    /**
     * Метод для обновления текста таймера при каждом тике таймера фокусировки или отдыха.
     * @param timeLabel строка с оставшимся временем в формате mm:ss.
     */
    public void updateTimerText(String timeLabel) {
        // устанавливаем текст таймера равным строке с оставшимся временем
        timer.setText(timeLabel);
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
        if (isRest) { // если идет отдых, то меняем иконку кнопки сброса или запуска таймера на стоп
            buttonStop.setImageResource(R.drawable.ic_stop);
            stopAllTimers();
            startTimerSetting(); // вызываем метод для запуска таймера подготовки
            isRest = false; // устанавливаем флаг отдыха в false
        } else { // если не идет отдых, то очищаем все атрибуты
            clearAttributes();
        }
    }

    /**
     * Метод для очистки всех атрибутов в классе мостика и на экране.
     */
    public void clearAttributes() {
        // устанавливаем текст с названием этапа
        studyStageText.setText("Нажми кнопку старта для запуска");
        // меняем иконку кнопки сброса или запуска таймера на плей
        buttonStop.setImageResource(R.drawable.ic_play);
        // обнуляем прогресс-бар
        progressBar.setProgress(0);
        // обнуляем текст таймера
        timer.setText("0");
        // сбрасываем номер раунда
        mRound = 1;
        // устанавливаем текст с номером раунда
        studyStageNumber.setText(mRound + "/" + roundsCount);
        // останавливаем все таймеры, если они запущены
        stopAllTimers();
        // устанавливаем флаг отдыха в true
        isRest = true;
    }

    /**
     * Метод для установки текста с названием этапа.
     * @param text строка с названием этапа.
     */
    public void setStageText(String text) {
        // устанавливаем текст с названием этапа
        studyStageText.setText(text);
    }

    /**
     * Метод для установки максимального значения прогресс-бара.
     * @param max максимальное значение прогресс-бара в секундах.
     */
    public void setMaxProgressBar(int max) {
        // устанавливаем максимальное значение прогресс-бара
        progressBar.setMax(max);
    }

    /**
     * Метод для получения значения флага фокусировки.
     * @return true, если идет фокусировка, false - в противном случае.
     */
    public boolean isFocusing() {
        return isFocusing;
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
        return mRound == roundsCount;
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
