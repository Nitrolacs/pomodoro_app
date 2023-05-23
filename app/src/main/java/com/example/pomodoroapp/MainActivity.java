package com.example.pomodoroapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;

import com.example.pomodoroapp.databinding.ActivityMainBinding;

/**
 * Главная Activity с таймером
 */
public class MainActivity extends AppCompatActivity {

    /**
     * Доступ к интерфейсу
     */
    private static ActivityMainBinding binding;

    /**
     * Мостик
     */
    private static Bridge bridge;

    /**
     * Вызывается при создании Activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        bridge = Bridge.getBridge();
        setContentView(binding.getRoot());

        binding.buttonStop.setOnClickListener(v -> bridge.resetOrStart());

        binding.sideBarButton.setOnClickListener(v ->
                binding.drawerLayout.openDrawer(GravityCompat.START));

        binding.timerSettings.setOnClickListener(v -> {
            // вызываем метод для показа диалога
            showSettingsDialog();
        });

        binding.navigationView.setNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.configuration) {
                transitionToNewActivity(TimerConfigurationActivity.class);
            }
            if (item.getItemId() == R.id.delete_configuration) {
                transitionToNewActivity(DeleteTimerConfigurationActivity.class);
            }
            if (item.getItemId() == R.id.cutomization) {
                transitionToNewActivity(SettingsActivity.class);
            }
            return true;
        });
    }

    /**
     * Переходит на новую Activity
     * @param cls Класс новой Activity
     */
    private void transitionToNewActivity(Class<?> cls) {
        binding.drawerLayout.closeDrawer(GravityCompat.START);
        Intent intent = new Intent(MainActivity.this, cls);
        startActivity(intent);
    }

    /**
     * Устанавливает настроки таймера
     */
    private void timerEnvironmentSetting() {
        if (bridge.isRest()) {
            setStopButton();
        }
        bridge.timerPreparation();
        setupStartTimerView();
    }

    /**
     * Подготавливает таймер запуска
     */
    private void setupStartTimerView() {
        binding.studyStageNumber.setText(bridge.getRounds());
        bridge.setStartTimer();
    }

    /**
     * Подготавливает таймер фокусирования
     */
    public static void setupFocusingTimerView() {
        setStageNumber(bridge.getRounds());
        bridge.setFocusingTimer();
    }

    /**
     * Подготавливает таймер отдыха
     */
    public static void setupRestTimerView() {
        bridge.setRestTimer();
    }

    /**
     * Устанавливает текст текущего раунда
     * @param text Устанавливаемое значение
     */
    public static void setStageText(String text) {
        binding.studyStageText.setText(text);
    }

    /**
     * Обновляет прогресс бар
     * @param seconds Количество секунд
     */
    public static void setProgressBar(int seconds) {
        binding.progressBar.setProgress(seconds);
    }

    /**
     * Устанавливает максимальное значение прогресс-бара
     * @param max Максимальное значение
     */
    public static void setMaxProgressBar(int max) {
        binding.progressBar.setMax(max);
    }

    /**
     * Устанавливает число текущего раунда
     * @param text Устанавливаемое значение
     */
    public static void setStageNumber(String text) {
        binding.studyStageNumber.setText(text);
    }

    /**
     * Обновляет текст таймера
     * @param timeLabel Устанавливаемое значение
     */
    public static void updateTimerText(String timeLabel) {
        binding.timer.setText(timeLabel);
    }

    /**
     * Показывает диалго выбора конфигурации
     */
    private void showSettingsDialog() {

        // создаем билдер для диалога
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // устанавливаем заголовок диалога
        builder.setTitle("Выберите конфигурацию таймера");

        // создаем разметку для диалога с Spinner
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.dialog_with_spinner, null);

        // находим Spinner в разметке
        Spinner spinner = layout.findViewById(R.id.spinner);

        // создаем адаптер для Spinner с массивом названий конфигураций
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                bridge.getConfigurationNames("ConfigurationsPrefs",
                                                         "_focusingTime", this));

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // устанавливаем адаптер для Spinner
        spinner.setAdapter(adapter);

        // устанавливаем слушатель выбора элемента для Spinner
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                bridge.getSelectedConfiguration(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // do nothing
            }
        });

        // устанавливаем разметку для диалога
        builder.setView(layout);

        // устанавливаем кнопку ОК и слушатель нажатия на нее
        builder.setPositiveButton("Ок", (dialog, which) -> {
            if (!bridge.saveSelectedConfiguration()) {
                Toast.makeText(MainActivity.this, "Не создана конфигурация",
                        Toast.LENGTH_SHORT).show();
            } else {
                timerEnvironmentSetting();
            }
        });

        // в методе showConfigurationDialog()
        // после установки кнопки ОК добавляем кнопку Отмена
        builder.setNegativeButton("Отмена", (dialog, which) -> {
            bridge.resetTmpFields();

            // закрываем диалог
            dialog.dismiss();
        });

        // создаем и показываем диалог
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * Устанавливает иконку кнопки стоп
     */
    public static void setStopButton() {
        binding.buttonStop.setImageResource(R.drawable.ic_stop);
    }

    /**
     * Устанавливает иконку кнопки старт
     */
    public static void setStartButton() {
        binding.buttonStop.setImageResource(R.drawable.ic_play);
    }

    /**
     * Выводит текст по завершению всех таймеров
     */
    public static void finishTimer() {
        binding.studyStageText.setText("Вы закончили все циклы :)");
    }
}