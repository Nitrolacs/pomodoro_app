package com.example.pomodoroapp;

import android.content.Context;

/**
 * Класс таймера для подготовки к фокусировке или отдыху.
 */
class StartTimer extends Timer {

    private TimerBridge timerBridge; // экземпляр класса мостика

    public StartTimer(Context context, long duration) {
        super(context, duration);
        timerBridge = TimerBridge.getInstance(context); // инициализируем экземпляр класса мостика
    }

    @Override
    protected void prepare() {
        // устанавливаем текст с названием этапа в зависимости от флага фокусировки
        if (timerBridge.isFocusing()) {
            timerBridge.setStageText("Приготовьтесь к фокусировке");
        } else {
            timerBridge.setStageText("Приготовьтесь к отдыху");
        }
        // обнуляем прогресс-бар
        timerBridge.updateProgressBar(0);
        // устанавливаем максимальное значение прогресс-бара равное длительности таймера подготовки в секундах
        timerBridge.setMaxProgressBar((int)(duration / 1000));
    }

    @Override
    protected void tick(long millisUntilFinished) {
        // устанавливаем текущее значение прогресс-бара равное оставшемуся времени в секундах
        timerBridge.updateProgressBar((int)(millisUntilFinished / 1000));
        // устанавливаем текст таймера равным оставшемуся времени в секундах
        timerBridge.updateTimerText(String.valueOf(millisUntilFinished / 1000));
    }

    @Override
    protected void finish() {
        // в зависимости от флага фокусировки создаем и запускаем таймер фокусировки или отдыха с нужной длительностью
        if (timerBridge.isFocusing()) {
            FocusTimer focusTimer = new FocusTimer(context, timerBridge.getFocusMinutes());
            focusTimer.start();
        } else {
            RestTimer restTimer = new RestTimer(context, timerBridge.getRestMinutes());
            restTimer.start();
        }
    }
}
