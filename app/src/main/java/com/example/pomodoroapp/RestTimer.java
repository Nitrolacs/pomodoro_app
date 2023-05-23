package com.example.pomodoroapp;

/**
 * Класс таймера для отдыха.
 */
class RestTimer extends Timer {

    /**
     * Мостик
     */
    private final Bridge bridge; // экземпляр класса мостика

    /**
     * Конструктор
     * @param duration Длительность таймера
     */
    public RestTimer(long duration) {
        super(duration);
        bridge = Bridge.getBridge(); // инициализируем экземпляр класса мостика
    }

    /**
     * Подготавливает интерфейс перед запуском таймера
     */
    @Override
    protected void prepare() {
        bridge.setStageText("Время отдыха");
        bridge.setMaxProgressBar(bridge.getRestMinutes() / 1000);
    }

    /**
     * Отправляет запросы на обновление интерфейса при каждом тике
     * @param millisUntilFinished оставшееся время в миллисекундах.
     */
    @Override
    protected void tick(long millisUntilFinished) {
        bridge.setProgressBar((int)(millisUntilFinished / 1000));
        // вызываем метод из класса мостика для обновления текста таймера при каждом тике таймера фокусирования
        bridge.updateTimerText(bridge.createTimeLabels((int)(millisUntilFinished / 1000)));
    }

    /**
     * Выполняет действия по завершении таймера
     */
    @Override
    protected void finish() {
        bridge.setFocusing();
        bridge.setStartTimer();
    }
}