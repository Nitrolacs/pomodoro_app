package com.example.pomodoroapp;

/**
 * Класс таймера для подготовки к фокусировке или отдыху.
 */
class StartTimer extends Timer {

    /**
     * Мостик
     */
    private final Bridge bridge;

    /**
     * Конструктор
     * @param duration Продолжительность таймера
     */
    public StartTimer(long duration) {
        super(duration);
        bridge = Bridge.getBridge(); // инициализируем экземпляр класса мостика
    }

    /**
     * Отправление запросов настройки интерфейса перед запуском таймера
     */
    @Override
    protected void prepare() {
        bridge.setStageText("Приготовьтесь");
        // обнуляем прогресс-бар
        bridge.setProgressBar(0);
        // устанавливаем максимальное значение прогресс-бара равное
        // длительности таймера подготовки в секундах
        bridge.setMaxProgressBar(5);
    }

    /**
     * Обновление интерфейса при каждом тике
     * @param millisUntilFinished оставшееся время в миллисекундах.
     */
    @Override
    protected void tick(long millisUntilFinished) {
        // устанавливаем текущее значение прогресс-бара равное оставшемуся времени в секундах
        bridge.setProgressBar((int)(millisUntilFinished / 1000));
        // устанавливаем текст таймера равным оставшемуся времени в секундах
        bridge.updateTimerText(String.valueOf(millisUntilFinished / 1000));
    }

    /**
     * Выполнение действий после завершения таймера
     */
    @Override
    protected void finish() {
        bridge.checkCurrentTimer();
    }
}
