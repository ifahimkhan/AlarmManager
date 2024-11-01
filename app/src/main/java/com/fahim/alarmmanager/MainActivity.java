package com.fahim.alarmmanager;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.TimePicker;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.fahim.alarmmanager.databinding.ActivityMainBinding;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    AlarmManager alarmManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        alarmManager = getSystemService(AlarmManager.class);
        EdgeToEdge.enable(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        binding.btnSelectDate.setOnClickListener(view -> showDatePickerDialog());
        binding.btnSelectTime.setOnClickListener(view -> showTimePickerDialog());
        binding.schedule.setOnClickListener(v -> {
            LocalDateTime date = createLocalDateTime();
            scheduleAlarm(date);
        });

    }

    private void scheduleAlarm(LocalDateTime date) {
        Intent intent = new Intent(this, AlarmReceiver.class);

        long triggerTimeMillis = date.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this, date.hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerTimeMillis,
                pendingIntent
        );
    }

    private LocalDateTime createLocalDateTime() {
        LocalDateTime localDateTime = null;
        try {
            String selectedDateTime = binding.tvSelectedDate.getText().toString() + " " + binding.tvSelectedTime.getText().toString();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            localDateTime = LocalDateTime.parse(selectedDateTime, formatter);

            System.out.println(localDateTime);

        } catch (DateTimeParseException e) {
            e.printStackTrace();
            String selectedDateTime = binding.tvSelectedDate.getText().toString() + " " + binding.tvSelectedTime.getText().toString();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/M/yyyy HH:mm");
            localDateTime = LocalDateTime.parse(selectedDateTime, formatter);

            System.out.println(localDateTime);
        }
        return localDateTime;
    }


    private void showDatePickerDialog() {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        String selectedDate = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                        binding.tvSelectedDate.setText(selectedDate);
                    }
                }, year, month, day);
        datePickerDialog.show();
    }

    private void showTimePickerDialog() {
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        String selectedTime = hourOfDay + ":" + String.format("%02d", minute);
                        binding.tvSelectedTime.setText(selectedTime);
                    }
                }, hour, minute, true);
        timePickerDialog.show();
    }

}