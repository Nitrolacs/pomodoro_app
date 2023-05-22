package com.example.pomodoroapp;

import android.os.CountDownTimer;

/**
 * Абстрактный класс таймера с шаблонным методом start()
 */
public abstract class Timer {
    protected CountDownTimer countDownTimer; // таймер обратного отсчета
    protected long duration; // длительность таймера в миллисекундах

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
     * Метод для подготовки таймера к запуску.
     * Должен быть реализован в подклассах.
     */
    protected abstract void prepare();

    /**
     * Метод для обновления состояния таймера при каждом тике.
     * Должен быть реализован в подклассах.
     * @param millisUntilFinished оставшееся время в миллисекундах.
     */
    protected abstract void tick(long millisUntilFinished);

    /**
     * Метод для завершения работы таймера.
     * Должен быть реализован в подклассах.
     */
    protected abstract void finish();

    /**
     * Метод для остановки таймера.
     */
    public void stop() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
    }

}
