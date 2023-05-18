package com.example.pomodoroapp;

import android.content.Context;

/**
 * Класс таймера для отдыха.
 */
class RestTimer extends Timer {

    private TimerBridge timerBridge; // экземпляр класса мостика

    public RestTimer(Context context, long duration) {
        super(context, duration);
        timerBridge = TimerBridge.getInstance(context); // инициализируем экземпляр класса мостика
    }

    @Override
    protected void prepare() {
        timerBridge.setupRestView(); // вызываем метод из класса мостика для подготовки экрана к отдыху
    }

    @Override
    protected void tick(long millisUntilFinished) {
        // вызываем метод из класса мостика для обновления прогресс-бара при каждом тике таймера отдыха
        timerBridge.updateProgressBar((int)(millisUntilFinished / 1000));
        // вызываем метод из класса мостика для обновления текста таймера при каждом тике таймера отдыха
        timerBridge.updateTimerText(timerBridge.createTimeLabels((int)(millisUntilFinished / 1000)));
    }

    @Override
    protected void finish() {
        // создаем экземпляр таймер фокусировки с нужной длительностью
        FocusTimer focusTimer = new FocusTimer(context, timerBridge.getFocusMinutes());

        focusTimer.start();
    }
}