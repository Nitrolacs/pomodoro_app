package com.example.pomodoroapp;

/**
 * Класс таймера для отдыха.
 */
class RestTimer extends Timer {

    private Bridge bridge; // экземпляр класса мостика

    public RestTimer(long duration) {
        super(duration);
        bridge = Bridge.getBridge(); // инициализируем экземпляр класса мостика
    }

    @Override
    protected void prepare() {
        bridge.setStageText("Время отдыха");
        bridge.setMaxProgressBar(bridge.getRestMinutes() / 1000);
    }

    @Override
    protected void tick(long millisUntilFinished) {
        bridge.setProgressBar((int)(millisUntilFinished / 1000));
        // вызываем метод из класса мостика для обновления текста таймера при каждом тике таймера фокусирования
        bridge.updateTimerText(bridge.createTimeLabels((int)(millisUntilFinished / 1000)));
    }

    @Override
    protected void finish() {
        bridge.setFocusing();
        bridge.setStartTimer();
    }
}