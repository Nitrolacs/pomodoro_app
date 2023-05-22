package com.example.pomodoroapp;

/**
 * Класс таймера для подготовки к фокусировке или отдыху.
 */
class StartTimer extends Timer {

    private final Bridge bridge; // экземпляр класса мостика

    public StartTimer(long duration) {
        super(duration);
        bridge = Bridge.getBridge(); // инициализируем экземпляр класса мостика
    }

    @Override
    protected void prepare() {
        bridge.setStageText("Приготовьтесь");
        // обнуляем прогресс-бар
        bridge.setProgressBar(0);
        // устанавливаем максимальное значение прогресс-бара равное длительности таймера подготовки в секундах
        bridge.setMaxProgressBar(5);
    }

    @Override
    protected void tick(long millisUntilFinished) {
        // устанавливаем текущее значение прогресс-бара равное оставшемуся времени в секундах
        bridge.setProgressBar((int)(millisUntilFinished / 1000));
        // устанавливаем текст таймера равным оставшемуся времени в секундах
        bridge.updateTimerText(String.valueOf(millisUntilFinished / 1000));
    }

    @Override
    protected void finish() {
        bridge.checkCurrentTimer();
    }
}
