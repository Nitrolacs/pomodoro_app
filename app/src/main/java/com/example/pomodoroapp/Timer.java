package com.example.pomodoroapp;

import android.os.CountDownTimer;

/**
 * Абстрактный класс таймера с шаблонным методом start()
 */
public abstract class Timer {

    /**
     * Таймер обратного отсчета
     */
    protected CountDownTimer countDownTimer;

    /**
     * Длительность таймера в миллисекундах
     */
    protected long duration;

    /**
     * Конструктор
     * @param duration длительность в миллисекундах
     */
    public Timer(long duration) {
        this.duration = duration;
    }

    /**
     * Шаблонный метод для запуска таймера.
     * Состоит из трех шагов: prepare(), tick() и finish().
     */
    public final void start() {
        prepare();
        countDownTimer = new CountDownTimer(duration + 500, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                tick(millisUntilFinished);
            }

            @Override
            public void onFinish() {
                finish();
            }
        }.start();
    }

    /**
     * Подготавливает таймер к запуску.
     * Должен быть реализован в подклассах.
     */
    protected abstract void prepare();

    /**
     * Обновляет состояние таймера при каждом тике.
     * Должен быть реализован в подклассах.
     * @param millisUntilFinished оставшееся время в миллисекундах.
     */
    protected abstract void tick(long millisUntilFinished);

    /**
     * Завершает работу таймера.
     * Должен быть реализован в подклассах.
     */
    protected abstract void finish();

    /**
     * Останавливает таймер
     */
    public void stop() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
    }

}
