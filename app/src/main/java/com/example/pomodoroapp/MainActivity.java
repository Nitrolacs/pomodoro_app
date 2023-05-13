package com.example.pomodoroapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;

import com.example.pomodoroapp.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    // ViewBinding. activity_main ==> ActivityMainBinding
    private ActivityMainBinding binding;

    private String nameConfiguration;
    private Integer focusMinutes, restMinutes, roundsCount;

    private CountDownTimer focusTimer, restTimer, startTimer;

    private int mRound = 1;

    private boolean isFocusing = true;
    private boolean isRest = false;

    private MediaPlayer mp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.sideBarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, TimerConfigurationActivity.class);
                startActivity(intent);
            }
        });

        // Receive Extras
        nameConfiguration = getIntent().getStringExtra("name");
        focusMinutes = getIntent().getIntExtra("focus", 0) * 60 * 1000;
        restMinutes = getIntent().getIntExtra("rest", 0) * 60 * 1000;
        roundsCount = getIntent().getIntExtra("roudns", 0);
        // Set Rounds Text
        binding.studyStageNumber.setText(mRound + "/" + roundsCount);
        // Start Timer
        setStartTimer();
        // Reset Button
        binding.buttonStop.setOnClickListener(v -> resetOrStart());
    }

    // Set Rest Timer
    private void setStartTimer() {
        // playSound();

        binding.studyStageText.setText("Приготовьтесь");
        binding.progressBar.setProgress(0);
        binding.progressBar.setMax(10);

        startTimer = new CountDownTimer(10500, 1000) {
            @Override
            public void onTick(long p0) {
                binding.progressBar.setProgress((int)(p0 / 1000));
                binding.timer.setText(String.valueOf(p0 / 1000));
            }

            @Override
            public void onFinish() {
                mp.reset();
                if (isFocusing) {
                    setupFocusingView();
                } else {
                    setupRestView();
                }
            }
        }.start();
    }

    private void setupRestView() {
        binding.studyStageNumber.setText("Break Time");
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
        binding.studyStageText.setText("Study Time");
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
        binding.studyStageText.setText("Нажми кнопку старта чтобы перезапустить");
        binding.buttonStop.setImageResource(R.drawable.ic_play);
        binding.progressBar.setProgress(0);
        binding.timer.setText("0");
        mRound = 1;
        binding.studyStageNumber.setText(mRound + "/" + roundsCount);
        restTimer.cancel();
        focusTimer.cancel();
        startTimer.cancel();
        mp.reset();
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
            setRestTimer();
            isRest = false;
        } else {
            clearAttribute();
        }
    }
}

/*
import android.media.MediaPlayer;
import android.net.Uri;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.os.CountDownTimer;
import com.exo.pomodoro.databinding.ActivityFeedBinding;

public class FeedActivity extends AppCompatActivity {
    private ActivityFeedBinding binding;

    private Integer studyMinute = null;
    private Integer breakMinute = null;
    private Integer roundCount = null;

    private CountDownTimer restTimer = null;
    private CountDownTimer studyTimer = null;
    private CountDownTimer breakTimer = null;

    private int mRound = 1;

    private boolean isStudy = true;

    private boolean isStop = false;

    private MediaPlayer mp = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFeedBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        // Receive Extras
        studyMinute = getIntent().getIntExtra("study", 0) * 60 * 1000;
        breakMinute = getIntent().getIntExtra("break", 0) * 60 * 1000;
        roundCount = getIntent().getIntExtra("round", 0);
        // Set Rounds Text
        binding.tvRound.setText(mRound + "/" + roundCount);
        //Start Timer
        setRestTimer();
        // Reset Button
        binding.ivStop.setOnClickListener(v -> resetOrStart());
    }
    // Set Rest Timer
    private void setRestTimer(){
       playSound();
        binding.tvStatus.setText("Get Ready");
        binding.progressBar.setProgress(0);
        binding.progressBar.setMax(10);
        restTimer = new CountDownTimer(10500,1000) {
            @Override
            public void onTick(long p0) {
                binding.progressBar.setProgress((int)(p0 / 1000));
                binding.tvTimer.setText(String.valueOf(p0 / 1000));
            }
            @Override
            public void onFinish() {
                mp.reset();
                if (isStudy){
                    setupStudyView();
                }else{
                    setupBreakView();
                }
            }
        }.start();
    }
    // Set Study Timer
    private void setStudyTimer(){

        studyTimer = new CountDownTimer(studyMinute.longValue() + 500,1000) {
            @Override
            public void onTick(long p0) {
                binding.progressBar.setProgress((int)(p0 /1000));
                binding.tvTimer.setText(createTimeLabels((int)(p0 / 1000)));
            }
            @Override
            public void onFinish() {
                if(mRound < roundCount){
                    isStudy = false;
                    setRestTimer();
                    mRound++;
                }else{
                    clearAttribute();
                    binding.tvStatus.setText("You have finish your rounds :)");
                }
            }
        }.start();
    }
    // Set Break Timer
    private void setBreakTimer() {
        breakTimer = new CountDownTimer(breakMinute.longValue()+500, 1000 ) {
            @Override
            public void onTick(long p0) {
                binding.progressBar.setProgress((int)(p0 / 1000));
                binding.tvTimer.setText(createTimeLabels((int)(p0 / 1000)));
            }

            @Override
            public void onFinish() {
                isStudy = true;
                setRestTimer();
            }

        }.start();
    }
    // Prepare Screen for Study Timer
    private void setupStudyView() {
        binding.tvRound.setText(mRound + "/" + roundCount);
        binding.tvStatus.setText("Study Time");
        binding.progressBar.setMax(studyMinute/1000);

        if (studyTimer != null)
            studyTimer.cancel();

        setStudyTimer();
    }
    // Prepare Screen for Study Timer
    private void setupBreakView() {
        binding.tvStatus.setText("Break Time");
        binding.progressBar.setMax(breakMinute/1000);

        if (breakTimer != null)
            breakTimer.cancel();

        setBreakTimer();
    }
    // Initialize sound file to MediaPlayer
    private void playSound() {

        try {
            Uri soundUrl = Uri.parse("android.resource://com.exo.pomodoro/" + R.raw.count_down);
            mp = MediaPlayer.create(this,soundUrl);
            mp.setLooping(false);
            mp.start();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    // Rest Whole Attributes in FeedActivity
    private void clearAttribute() {
        binding.tvStatus.setText("Press Play Button to Restart");
        binding.ivStop.setImageResource(R.drawable.ic_play);
        binding.progressBar.setProgress(0);
        binding.tvTimer.setText("0");
        mRound = 1;
        binding.tvRound.setText(mRound + "/" + roundCount);
        restTimer.cancel();
        studyTimer.cancel();
        breakTimer.cancel();
        mp.reset();
        isStop = true;
    }
    // Convert Received Numbers to Minutes and Seconds
    private String createTimeLabels(int time) {
        String timeLabel = "";
        int minutes = time / 60;
        int secends = time % 60;

        if (minutes < 10) timeLabel += "0";
        timeLabel += minutes + ":";

        if (secends < 10) timeLabel += "0";
        timeLabel += secends;

        return timeLabel;
    }
    // For Reset or Restart Pomodoro
    private void resetOrStart() {
        if (isStop) {
            binding.ivStop.setImageResource(R.drawable.ic_stop);
            setRestTimer();
            isStop = false;
        } else {
            clearAttribute();
        }
    }
    // Clear Everything When App Destroyed
    @Override
    protected void onDestroy() {
        super.onDestroy();
        restTimer.cancel();
        studyTimer.cancel();
        breakTimer.cancel();
        mp.reset();
    }
 */