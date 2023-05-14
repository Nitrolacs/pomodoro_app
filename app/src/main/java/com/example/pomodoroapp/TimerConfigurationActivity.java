package com.example.pomodoroapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.pomodoroapp.databinding.ActivityTimerConfigurationBinding;

public class TimerConfigurationActivity extends AppCompatActivity {

    private ActivityTimerConfigurationBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTimerConfigurationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.buttonSaveConfiguration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String configurationName = binding.fieldConfigurationName.getText().toString();
                String focusingTime = binding.fieldFocusingTime.getText().toString();
                String restTime = binding.fieldRestTime.getText().toString();
                String roundsNumber = binding.fieldRoundsNumber.getText().toString();

                if (!configurationName.isEmpty() && !focusingTime.isEmpty() && !restTime.isEmpty()
                        && !roundsNumber.isEmpty()) {

                    if (InputChecker.getInputChecker().checkData(configurationName,
                            Integer.parseInt(focusingTime),
                            Integer.parseInt(restTime),
                            Integer.parseInt(roundsNumber))) {
                        Intent intent = new Intent(TimerConfigurationActivity.this, MainActivity.class);
                        intent.putExtra("name", configurationName);
                        intent.putExtra("focus", Integer.parseInt(focusingTime));
                        intent.putExtra("rest", Integer.parseInt(restTime));
                        intent.putExtra("rounds", Integer.parseInt(roundsNumber));
                        startActivity(intent);
                    } else {
                        Toast.makeText(TimerConfigurationActivity.this,
                                "Введены неверные значения.",
                                Toast.LENGTH_SHORT).show();
                        binding.fieldConfigurationName.setError("Введено неверное значение");

                    }

                } else {
                    Toast.makeText(TimerConfigurationActivity.this, "Заполните все поля",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}