package com.example.pomodoroapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.pomodoroapp.databinding.ActivityMainBinding;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Activity с таймером
 */
public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    private String nameConfiguration = null;

    private Integer focusMinutes = null;
    private Integer restMinutes = null;
    private Integer roundsCount = null;


    private String tmpNameConfiguration = null;
    private Integer tmpFocusMinutes = null;
    private Integer tmpRestMinutes = null;
    private Integer tmpRoundsCount = null;


    private CountDownTimer focusTimer = null;
    private CountDownTimer restTimer = null;
    private CountDownTimer startTimer = null;

    private int mRound = 1;

    private boolean isFocusing = true;
    private boolean isRest = false;

    private MediaPlayer mp;

    private void startTimerSetting() {
        // Set Rounds Text
        binding.studyStageNumber.setText(mRound + "/" + roundsCount);
        // Start Timer
        setStartTimer();
        // Reset Button
        binding.buttonStop.setOnClickListener(v -> resetOrStart());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.sideBarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        binding.timerSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // вызываем метод для показа диалога
                showSettingsDialog();
            }
        });

        binding.navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.configuration) {
                    if (focusMinutes != null && !isRest) {
                        resetOrStart();
                    }

                    transitionToNewActivity(TimerConfigurationActivity.class);
                }

                if (item.getItemId() == R.id.delete_configuration) {
                    transitionToNewActivity(DeleteTimerConfigurationActivity.class);
                }
                return true;
            }
        });
    }

    private void transitionToNewActivity(Class<?> cls) {
        binding.drawerLayout.closeDrawer(GravityCompat.START);
        Intent intent = new Intent(MainActivity.this, cls);
        startActivity(intent);
    }

    // Set Rest Timer
    private void setStartTimer() {
        // playSound();

        binding.studyStageText.setText("Приготовьтесь");
        binding.progressBar.setProgress(0);
        binding.progressBar.setMax(5);

        startTimer = new CountDownTimer(5500, 1000) {
            @Override
            public void onTick(long p0) {
                binding.progressBar.setProgress((int)(p0 / 1000));
                binding.timer.setText(String.valueOf(p0 / 1000));
            }

            @Override
            public void onFinish() {
                //mp.reset();
                if (isFocusing) {
                    setupFocusingView();
                } else {
                    setupRestView();
                }
            }
        }.start();
    }

    private void setupRestView() {
        binding.studyStageText.setText("Время отдыха");
        binding.progressBar.setMax(restMinutes/1000);

        if (restTimer != null)
            restTimer.cancel();

        setRestTimer();
    }

    // Set Rest Timer
    private void setRestTimer() {
        restTimer = new CountDownTimer(restMinutes.longValue() + 500,
                1000 ) {
            @Override
            public void onTick(long p0) {
                binding.progressBar.setProgress((int)(p0 / 1000));
                binding.timer.setText(createTimeLabels((int)(p0 / 1000)));
            }

            @Override
            public void onFinish() {
                isFocusing = true;
                setStartTimer();
            }

        }.start();
    }

    // Prepare Screen for Focusing Timer
    private void setupFocusingView() {
        binding.studyStageNumber.setText(mRound + "/" + roundsCount);
        binding.studyStageText.setText("Время фокусирования");
        binding.progressBar.setMax(focusMinutes/1000);

        if (focusTimer != null)
            focusTimer.cancel();

        setFocusingTimer();
    }

    // Set Focusing Timer
    private void setFocusingTimer() {
        focusTimer = new CountDownTimer(focusMinutes.longValue() + 500, 1000) {
            @Override
            public void onTick(long p0) {
                binding.progressBar.setProgress((int) p0/ 1000);
                binding.timer.setText(createTimeLabels((int) (p0 / 1000)));
            }

            @Override
            public void onFinish() {
                if (mRound < roundsCount) {
                    isFocusing = false;
                    setStartTimer();
                    mRound++;
                } else {
                    clearAttribute();
                    binding.studyStageText.setText("Вы закончили все циклы :)");

                }
            }
        }.start();
    }

    private void timerPreparation() {
        isFocusing = true;

        if (isRest) {
            binding.buttonStop.setImageResource(R.drawable.ic_stop);
        }

        isRest = false;

        if (restTimer != null) {
            restTimer.cancel();
        }

        if (focusTimer != null) {
            focusTimer.cancel();
        }

        if (startTimer != null) {
            startTimer.cancel();
        }
    }

    // Rest Whole Attributes in MainActivity
    private void clearAttribute() {
        binding.studyStageText.setText("Нажми кнопку старта для запуска");
        binding.buttonStop.setImageResource(R.drawable.ic_play);
        binding.progressBar.setProgress(0);
        binding.timer.setText("0");
        mRound = 1;
        binding.studyStageNumber.setText(mRound + "/" + roundsCount);

        if (restTimer != null) {
            restTimer.cancel();
        }

        if (focusTimer != null) {
            focusTimer.cancel();
        }

        if (startTimer != null) {
            startTimer.cancel();
        }
        //mp.reset();
        isRest = true;
    }

    // Convert Received Numbers to Minutes and Seconds
    private String createTimeLabels(int time) {
        String timeLabel = "";
        int minutes = time / 60;
        int seconds = time % 60;

        if (minutes < 10) timeLabel += "0";
        timeLabel += minutes + ":";

        if (seconds < 10) timeLabel += "0";
        timeLabel += seconds;

        return timeLabel;
    }

    // For Reset or Restart Pomodoro
    private void resetOrStart() {
        if (isRest) {
            binding.buttonStop.setImageResource(R.drawable.ic_stop);
            setStartTimer();
            isRest = false;
        } else {
            clearAttribute();
        }
    }

    // создаем метод для вызова диалога
    private void showSettingsDialog() {

        // получаем SharedPreferences
        SharedPreferences sp = getSharedPreferences("ConfigurationsPrefs", Context.MODE_PRIVATE);

        // получаем все записи из SharedPreferences
        Map<String, ?> allEntries = sp.getAll();

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

        // преобразуем список в массив строк
        String[] namesArray = configurationNames.toArray(new String[0]);

        // создаем билдер для диалога
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // устанавливаем заголовок диалога
        builder.setTitle("Выберите конфигурацию таймера");

        // создаем разметку для диалога с Spinner
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.dialog_with_spinner, null);

        // находим Spinner в разметке
        Spinner spinner = (Spinner) layout.findViewById(R.id.spinner);

        // создаем адаптер для Spinner с массивом названий конфигураций
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, namesArray);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // устанавливаем адаптер для Spinner
        spinner.setAdapter(adapter);

        // устанавливаем слушатель выбора элемента для Spinner
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // получаем выбранное название конфигурации
                tmpNameConfiguration = namesArray[position];
                // получаем настройки по этому названию из SharedPreferences
                tmpFocusMinutes = Integer.parseInt(sp.getString(tmpNameConfiguration + "_focusingTime",
                        "0")) * 60 * 1000;
                tmpRestMinutes = Integer.parseInt(sp.getString(tmpNameConfiguration + "_restTime",
                        "0")) * 60 * 1000;
                tmpRoundsCount = Integer.valueOf(sp.getString(tmpNameConfiguration + "_roundsNumber",
                        "0"));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // do nothing
            }
        });

        // устанавливаем разметку для диалога
        builder.setView(layout);

        // устанавливаем кнопку ОК и слушатель нажатия на нее
        builder.setPositiveButton("Ок", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (tmpNameConfiguration == null) {
                    Toast.makeText(MainActivity.this, "Не создана конфигурация",
                            Toast.LENGTH_SHORT).show();
                }

                else {
                    // присваиваем значения переменным в MainActivity
                    MainActivity.this.nameConfiguration = tmpNameConfiguration;
                    MainActivity.this.focusMinutes = tmpFocusMinutes;
                    MainActivity.this.restMinutes = tmpRestMinutes;
                    MainActivity.this.roundsCount = tmpRoundsCount;

                    timerPreparation();
                    startTimerSetting();
                }
            }
        });

        // в методе showConfigurationDialog()
        // после установки кнопки ОК добавляем кнопку Отмена
        builder.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                tmpNameConfiguration = null;
                tmpFocusMinutes = null;
                tmpRestMinutes = null;
                tmpRoundsCount = null;

                // закрываем диалог
                dialog.dismiss();
            }
        });

        // создаем и показываем диалог
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
