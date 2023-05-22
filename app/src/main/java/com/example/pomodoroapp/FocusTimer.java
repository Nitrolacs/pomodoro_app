package com.example.pomodoroapp;

import android.content.Context;

/**
 * Класс таймера для фокусирования.
 */
class FocusTimer extends Timer {

    private Bridge bridge; // экземпляр класса мостика

    public FocusTimer(long duration) {
        super(duration);
        bridge = Bridge.getBridge(); // инициализируем экземпляр класса мостика
    }

    @Override
    protected void prepare() {
        // вызываем метод из класса мостика для подготовки экрана к фокусированию
        bridge.setStageNumber(bridge.getRounds());
        bridge.setStageText("Время фокусирования");
        bridge.setMaxProgressBar(bridge.getFocusMinutes() / 1000);
    }

    @Override
    protected void tick(long millisUntilFinished) {
        bridge.setProgressBar((int)(millisUntilFinished / 1000));
        // вызываем метод из класса мостика для обновления текста таймера при каждом тике таймера фокусирования
        bridge.updateTimerText(bridge.createTimeLabels((int)(millisUntilFinished / 1000)));

    }

    @Override
    protected void finish() {
        bridge.actionsAfterFocusing();
    }
}