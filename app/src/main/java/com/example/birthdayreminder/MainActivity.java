package com.example.birthdayreminder;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import java.util.Calendar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    private TextView messageView, ageView;
    private SharedPreferences preferences;

    private static final String PREFS_NAME = "BirthdayPrefs";
    private static final String KEY_YEAR = "birth_year";
    private static final String KEY_MONTH = "birth_month";
    private static final String KEY_DAY = "birth_day";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        messageView = findViewById(R.id.MessageView);
        ageView = findViewById(R.id.AgeView);
        Button setDateButton = findViewById(R.id.SetDateButton);
        Button clearButton = findViewById(R.id.ClearButton);

        preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        messageView.setText(R.string.MessageView);
      // ageView.setText("");
ageView.setVisibility(ageView.GONE);
        setDateButton.setOnClickListener(new setDateButtonClickListener());
        clearButton.setOnClickListener(v -> clearBirthday());

        loadBirthday();
    }
    //birthdayselect  button event implementation
    private class setDateButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Calendar today = Calendar.getInstance();
            new DatePickerDialog(MainActivity.this,
                    android.R.style.Theme_Holo_Light_Dialog,
                    (view, year, month, dayOfMonth) -> {
                        saveBirthday(year, month, dayOfMonth);
                        updateBirthdayInfo(year, month, dayOfMonth);
                    },
                    today.get(Calendar.YEAR),
                    today.get(Calendar.MONTH),
                    today.get(Calendar.DAY_OF_MONTH)).show();
        }
    }

    private void saveBirthday(int year, int month, int day) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(KEY_YEAR, year);
        editor.putInt(KEY_MONTH, month);
        editor.putInt(KEY_DAY, day);
        editor.apply();
    }

    private void loadBirthday() {
        if (preferences.contains(KEY_YEAR)) {
            int year = preferences.getInt(KEY_YEAR, 0);
            int month = preferences.getInt(KEY_MONTH, 0);
            int day = preferences.getInt(KEY_DAY, 0);
            updateBirthdayInfo(year, month, day);
        }
    }

    private void clearBirthday() {
        preferences.edit().clear().apply();
        messageView.setText(R.string.MessageView);
     //  ageView.setText("");
        //計算の時だけ表示してほしかったので。。。
        ageView.setVisibility(View.GONE);
    }

    private void updateBirthdayInfo(int year, int month, int day) {
        Calendar today = Calendar.getInstance();
        Calendar birthday = Calendar.getInstance();
        birthday.set(year, month, day);

        int age = today.get(Calendar.YEAR) - year;
        if (today.get(Calendar.MONTH) < month ||
                (today.get(Calendar.MONTH) == month && today.get(Calendar.DAY_OF_MONTH) < day)) {
            age--;
        }

        Calendar nextBirthday = Calendar.getInstance();
        nextBirthday.set(Calendar.MONTH, month);
        nextBirthday.set(Calendar.DAY_OF_MONTH, day);
        if (today.after(nextBirthday)) {
            nextBirthday.add(Calendar.YEAR, 1);
        }

        long diffMillis = nextBirthday.getTimeInMillis() - today.getTimeInMillis();
        long daysUntilBirthday = diffMillis / (1000 * 60 * 60 * 24);

        String birthdayMessage = getString(R.string.birthday_message, daysUntilBirthday);
        String ageMessage = getString(R.string.age_message, age);

        messageView.setText(birthdayMessage);
        ageView.setText(ageMessage);
        ageView.setVisibility(View.VISIBLE);
    }
}
