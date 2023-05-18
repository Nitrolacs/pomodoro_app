package com.example.pomodoroapp;

import android.content.Context;

/**
 * Класс таймера для фокусирования.
 */
class FocusTimer extends Timer {

    private TimerBridge timerBridge; // экземпляр класса мостика

    public FocusTimer(Context context, long duration) {
        super(context, duration);
        timerBridge = TimerBridge.getInstance(context); // инициализируем экземпляр класса мостика
    }

    @Override
    protected void prepare() {
        // вызываем метод из класса мостика для подготовки экрана к фокусированию
        timerBridge.setupFocusingView();
    }

    @Override
    protected void tick(long millisUntilFinished) {
        // вызываем метод из класса мостика для обновления прогресс-бара при каждом тике таймера фокусирования
        timerBridge.updateProgressBar((int)(millisUntilFinished / 1000));
        // вызываем метод из класса мостика для обновления текста таймера при каждом тике таймера фокусирования
        timerBridge.updateTimerText(timerBridge.createTimeLabels((int)(millisUntilFinished / 1000)));

    }

    @Override
    protected void finish() {
        // проверяем, является ли текущий раунд последним
        if (timerBridge.isLastRound()) {

            // если да, то вызываем метод из класса мостика для очистки атрибутов
            timerBridge.clearAttributes();

        } else { // если нет, то переходим к следующему раунду

            // вызываем метод из класса мостика для переход к следующему раунду
            timerBridge.nextRound();

            // создаем экземпляр таймер отдыха с нужной длительностью
            RestTimer restTimer = new RestTimer(context, timerBridge.getRestMinutes());

            restTimer.start();
        }
    }
}