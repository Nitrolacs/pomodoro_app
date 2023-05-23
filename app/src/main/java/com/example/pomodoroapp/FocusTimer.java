package com.example.pomodoroapp;

/**
 * Класс таймера для фокусирования.
 */
class FocusTimer extends Timer {

    /**
     * Мостик
     */
    private final Bridge bridge;

    /**
     * Конструктор фокусировочного таймера
     * @param duration Длительность таймера
     */
    public FocusTimer(long duration) {
        super(duration);
        bridge = Bridge.getBridge(); // инициализируем экземпляр класса мостика
    }

    /**
     * Выполняет все необходимые подготовительные действия
     */
    @Override
    protected void prepare() {
        // вызываем метод из класса мостика для подготовки экрана к фокусированию
        bridge.setStageNumber(bridge.getRounds());
        bridge.setStageText("Время фокусирования");
        bridge.setMaxProgressBar(bridge.getFocusMinutes() / 1000);
    }

    /**
     * Отправляет запрос обновления интерфейса при каждом тике
     * @param millisUntilFinished оставшееся время в миллисекундах.
     */
    @Override
    protected void tick(long millisUntilFinished) {
        bridge.setProgressBar((int)(millisUntilFinished / 1000));
        // вызываем метод из класса мостика для обновления текста таймера при каждом тике таймера фокусирования
        bridge.updateTimerText(bridge.createTimeLabels((int)(millisUntilFinished / 1000)));
    }

    /**
     * Вызывает метод для выполнения действий после окончания таймера
     */
    @Override
    protected void finish() {
        bridge.actionsAfterFocusing();
    }
}