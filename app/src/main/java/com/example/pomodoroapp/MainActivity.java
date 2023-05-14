package com.example.pomodoroapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Toast;

import com.example.pomodoroapp.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    private String nameConfiguration = null;
    private Integer focusMinutes = null;
    private Integer restMinutes = null;
    private Integer roundsCount = null;


    private CountDownTimer focusTimer = null;
    private CountDownTimer restTimer = null;
    private CountDownTimer startTimer = null;

    private int mRound = 1;

    private boolean isFocusing = true;
    private boolean isRest = false;

    private MediaPlayer mp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.sideBarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Receive Extras
        nameConfiguration = getIntent().getStringExtra("name");
        focusMinutes = getIntent().getIntExtra("focus", 0) * 60 * 1000;
        restMinutes = getIntent().getIntExtra("rest", 0) * 60 * 1000;
        roundsCount = getIntent().getIntExtra("rounds", 0);
        // Set Rounds Text
        binding.studyStageNumber.setText(mRound + "/" + roundsCount);
        // Start Timer
        setStartTimer();
        // Reset Button
        binding.buttonStop.setOnClickListener(v -> resetOrStart());
    }

    // Set Rest Timer
    private void setStartTimer() {
        // playSound();

        binding.studyStageText.setText("Приготовьтесь");
        binding.progressBar.setProgress(0);
        binding.progressBar.setMax(5);

        startTimer = new CountDownTimer(5500, 1000) {
            @Override
            public void onTick(long p0) {
                binding.progressBar.setProgress((int)(p0 / 1000));
                binding.timer.setText(String.valueOf(p0 / 1000));
            }

            @Override
            public void onFinish() {
                //mp.reset();
                if (isFocusing) {
                    setupFocusingView();
                } else {
                    setupRestView();
                }
            }
        }.start();
    }

    private void setupRestView() {
        binding.studyStageText.setText("Время отдыха");
        binding.progressBar.setMax(restMinutes/1000);

        if (restTimer != null)
            restTimer.cancel();

        setRestTimer();
    }

    // Set Rest Timer
    private void setRestTimer() {
        restTimer = new CountDownTimer(restMinutes.longValue() + 500,
                1000 ) {
            @Override
            public void onTick(long p0) {
                binding.progressBar.setProgress((int)(p0 / 1000));
                binding.timer.setText(createTimeLabels((int)(p0 / 1000)));
            }

            @Override
            public void onFinish() {
                isFocusing = true;
                setStartTimer();
            }

        }.start();
    }

    // Prepare Screen for Focusing Timer
    private void setupFocusingView() {
        binding.studyStageNumber.setText(mRound + "/" + roundsCount);
        binding.studyStageText.setText("Время фокусирования");
        binding.progressBar.setMax(focusMinutes/1000);

        if (focusTimer != null)
            focusTimer.cancel();

        setFocusingTimer();
    }

    // Set Focusing Timer
    private void setFocusingTimer() {
        focusTimer = new CountDownTimer(focusMinutes.longValue() + 500, 1000) {
            @Override
            public void onTick(long p0) {
                binding.progressBar.setProgress((int) p0/ 1000);
                binding.timer.setText(createTimeLabels((int) (p0 / 1000)));
            }

            @Override
            public void onFinish() {
                if (mRound < roundsCount) {
                    isFocusing = false;
                    setStartTimer();
                    mRound++;
                } else {

                    clearAttribute();
                    binding.studyStageText.setText("Вы закончили все циклы :)");
                }
            }
        }.start();
    }

    // Rest Whole Attributes in MainActivity
    private void clearAttribute() {
        binding.studyStageText.setText("Нажми кнопку старта для повтора");
        binding.buttonStop.setImageResource(R.drawable.ic_play);
        binding.progressBar.setProgress(0);
        binding.timer.setText("0");
        mRound = 1;
        binding.studyStageNumber.setText(mRound + "/" + roundsCount);

        if (restTimer != null) {
            restTimer.cancel();
        }

        if (focusTimer != null) {
            focusTimer.cancel();
        }

        if (startTimer != null) {
            startTimer.cancel();
        }
        //mp.reset();
        isRest = true;
    }

    // Convert Received Numbers to Minutes and Seconds
    private String createTimeLabels(int time) {
        String timeLabel = "";
        int minutes = time / 60;
        int seconds = time % 60;

        if (minutes < 10) timeLabel += "0";
        timeLabel += minutes + ":";

        if (seconds < 10) timeLabel += "0";
        timeLabel += seconds;

        return timeLabel;
    }

    // For Reset or Restart Pomodoro
    private void resetOrStart() {
        if (isRest) {
            binding.buttonStop.setImageResource(R.drawable.ic_stop);
            setStartTimer();
            isRest = false;
        } else {
            clearAttribute();
        }
    }
}

/*

Для использования шаблона проектирования Фабричный метод в этом коде тебе нужно сделать следующие шаги:

Создать абстрактный класс или интерфейс Timer, который определяет общий интерфейс для разных типов таймеров (startTimer, focusTimer, restTimer).
Создать конкретные классы StartTimer, FocusTimer и RestTimer, которые наследуются от Timer и реализуют его методы согласно своей логике.
Создать абстрактный класс или интерфейс TimerFactory, который определяет метод createTimer(String type), который возвращает объект типа Timer в зависимости от переданного параметра type.
Создать конкретный класс PomodoroTimerFactory, который реализует метод createTimer(String type) таким образом, что он возвращает экземпляр StartTimer, если type равен “start”, экземпляр FocusTimer, если type равен “focus”, и экземпляр RestTimer, если type равен “rest”.
В классе MainActivity создать поле private TimerFactory timerFactory и инициализировать его в методе onCreate() как new PomodoroTimerFactory().
Вместо создания объектов startTimer, focusTimer и restTimer напрямую в методах setStartTimer(), setFocusingTimer() и setRestTimer(), использовать метод timerFactory.createTimer(type) для получения нужного типа таймера.
Вот пример кода с применением шаблона Фабричный метод:

package com.example.pomodoroapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Toast;

import com.example.pomodoroapp.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    private String nameConfiguration = null;
    private Integer focusMinutes = null;
    private Integer restMinutes = null;
    private Integer roundsCount = null;
    private Timer startTimer = null;
    private Timer focusTimer = null;
    private Timer restTimer = null;

    private int mRound = 1;

    private boolean isFocusing = true;
    private boolean isRest = false;

    private MediaPlayer mp;

    // Создаем поле для фабрики таймеров
    private TimerFactory timerFactory;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.sideBarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Receive Extras
        nameConfiguration = getIntent().getStringExtra("name");
        focusMinutes = getIntent().getIntExtra("focus", 0) * 60 * 1000;
        restMinutes = getIntent().getIntExtra("rest", 0) * 60 * 1000;
        roundsCount = getIntent().getIntExtra("rounds", 0);
        // Set Rounds Text
        binding.studyStageNumber.setText(mRound + "/" + roundsCount);
        // Инициализируем фабрику таймеров
        timerFactory = new PomodoroTimerFactory();
        // Start Timer
        setStartTimer();
        // Reset Button
        binding.buttonStop.setOnClickListener(v -> resetOrStart());
    }

    // Set Rest Timer
    private void setStartTimer() {
        // playSound();

        binding.studyStageText.setText("Приготовьтесь");
        binding.progressBar.setProgress(0);
        binding.progressBar.setMax(5);

        // Используем фабрику таймеров для создания объекта StartTimer
        startTimer = timerFactory.createTimer("start");

        startTimer.start();
    }

    // Set Rest Timer
    private void setRestTimer() {

        // Используем фабрику таймеров для создания объекта RestTimer
        restTimer = timerFactory.createTimer("rest");

        restTimer.start();
    }


    // Set Focusing Timer
    private void setFocusingTimer() {

        // Используем фабрику таймеров для создания объекта FocusTimer
focusTimer = timerFactory.createTimer("focus");

focusTimer.start();
}

// For Reset or Restart Pomodoro
private void resetOrStart() {
if (isRest) {
binding.buttonStop.setImageResource(R.drawable.ic_stop);
setStartTimer();
isRest = false;
} else {
clearAttribute();
}
}
}

// Создаем абстрактный класс Timer, который определяет общий интерфейс для таймеров
abstract class Timer {
    // Абстрактный метод для запуска таймера
    public abstract void start();
}

// Создаем класс StartTimer, который наследуется от Timer и реализует его метод start()
class StartTimer extends Timer {

    // Поле для хранения ссылки на MainActivity
    private MainActivity activity;

    // Конструктор, который принимает ссылку на MainActivity
    public StartTimer(MainActivity activity) {
        this.activity = activity;
    }

    // Метод для запуска таймера
    @Override
    public void start() {
        // Создаем объект CountDownTimer с нужными параметрами
        CountDownTimer timer = new CountDownTimer(5500, 1000) {
            @Override
            public void onTick(long p0) {
                activity.binding.progressBar.setProgress((int)(p0 / 1000));
                activity.binding.timer.setText(String.valueOf(p0 / 1000));
            }

            @Override
            public void onFinish() {
                //mp.reset();
                if (activity.isFocusing) {
                    activity.setupFocusingView();
                } else {
                    activity.setupRestView();
                }
            }
        }.start();
    }
}

// Создаем класс FocusTimer, который наследуется от Timer и реализует его метод start()
class FocusTimer extends Timer {

    // Поле для хранения ссылки на MainActivity
    private MainActivity activity;

    // Конструктор, который принимает ссылку на MainActivity
    public FocusTimer(MainActivity activity) {
        this.activity = activity;
    }

    // Метод для запуска таймера
    @Override
    public void start() {
        // Создаем объект CountDownTimer с нужными параметрами
        CountDownTimer timer = new CountDownTimer(activity.focusMinutes.longValue() + 500, 1000) {
            @Override
            public void onTick(long p0) {
                activity.binding.progressBar.setProgress((int) p0/ 1000);
                activity.binding.timer.setText(createTimeLabels((int) (p0 / 1000)));
            }

            @Override
            public void onFinish() {
                if (activity.mRound < activity.roundsCount) {
                    activity.isFocusing = false;
                    activity.setStartTimer();
                    activity.mRound++;
                } else {

                    activity.clearAttribute();
                    activity.binding.studyStageText.setText("Вы закончили все циклы :)");
                }
            }
        }.start();
    }
}

// Создаем класс RestTimer, который наследуется от Timer и реализует его метод start()
class RestTimer extends Timer {

    // Поле для хранения ссылки на MainActivity
    private MainActivity activity;

    // Конструктор, который принимает ссылку на MainActivity
    public RestTimer(MainActivity activity) {
        this.activity = activity;
    }

    // Метод для запуска таймера
    @Override
    public void start() {
        // Создаем объект CountDownTimer с нужными параметрами
        CountDownTimer timer = new CountDownTimer(activity.restMinutes.longValue() + 500,
                1000 ) {
            @Override
            public void onTick(long p0) {
                activity.binding.progressBar.setProgress((int)(p0 / 1000));
                activity.binding.timer.setText(createTimeLabels((int)(p0 / 1000)));
            }

            @Override
            public void onFinish() {
                activity.isFocusing = true;
                activity.setStartTimer();
            }

        }.start();
    }
}

// Создаем абстрактный класс TimerFactory, который определяет метод createTimer(String type)
abstract class TimerFactory {

    // Абстрактный метод для создания объекта типа Timer в зависимости от параметра type
    public abstract Timer createTimer(String type);
}


// Создаем класс PomodoroTimerFactory, который реализует метод createTimer(String type)
class PomodoroTimerFactory extends TimerFactory {

    // Поле для хранения ссылки на MainActivity
    private MainActivity activity;

    // Конструктор, который принимает ссылку на MainActivity
    public PomodoroTimerFactory(MainActivity activity) {
        this.activity = activity;
    }

    // Метод для создания объекта типа Timer в зависимости от параметра type
    @Override
    public Timer createTimer(String type) {
        // Проверяем значение параметра type
        switch (type) {
            case "start":
                // Если type равен "start", возвращаем объект StartTimer
                return new StartTimer(activity);
            case "focus":
                // Если type равен "focus", возвращаем объект FocusTimer
                return new FocusTimer(activity);
            case "rest":
                // Если type равен "rest", возвращаем объект RestTimer
                return new RestTimer(activity);
            default:
                // Если type не соответствует ни одному известному типу, возвращаем null
                return null;
        }
    }
}

 */