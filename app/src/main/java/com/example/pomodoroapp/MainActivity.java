package com.example.pomodoroapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.pomodoroapp.databinding.ActivityMainBinding;
import com.google.android.material.navigation.NavigationView;

/**
 * Activity с таймером
 */
public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    private String nameConfiguration = null;
    private Integer focusMinutes = null;
    private Integer restMinutes = null;
    private Integer roundsCount = null;


    private CountDownTimer focusTimer = null;
    private CountDownTimer restTimer = null;
    private CountDownTimer startTimer = null;

    private int mRound = 1;

    private boolean isFocusing = true;
    private boolean isRest = false;

    private MediaPlayer mp;

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if (intent.hasExtra("name") || intent.hasExtra("focus") ||
                intent.hasExtra("rest") || intent.hasExtra("rounds")) {
            nameConfiguration = intent.getStringExtra("name");
            focusMinutes = intent.getIntExtra("focus", 0) * 60 * 1000;
            restMinutes = intent.getIntExtra("rest", 0) * 60 * 1000;
            roundsCount = intent.getIntExtra("rounds", 0);
            startTimerSetting();
        }
    }

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

        binding.navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.configuration) {
                    if (focusMinutes != null && !isRest) {
                        resetOrStart();
                    }

                    binding.drawerLayout.closeDrawer(GravityCompat.START);
                    Intent intent = new Intent(MainActivity.this, TimerConfigurationActivity.class);
                    startActivity(intent);
                }
                return true;
            }
        });
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
}
