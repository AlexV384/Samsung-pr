package com.example.nousers;

import androidx.appcompat.app.AppCompatActivity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.HashMap;
import java.util.Map;

public class NewAdvertisementActivity extends AppCompatActivity {

    private EditText mTitleField;
    private EditText mDescriptionField;
    private EditText mLocationField;
    private EditText mMaxParticipantsField;
    private Spinner mSportSpinner;
    private Button mSubmitButton;

    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_advertisement);

        // Получение ссылки на базу данных Firebase
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Инициализация полей ввода и кнопки отправки
        mTitleField = findViewById(R.id.title_edit_text);
        mDescriptionField = findViewById(R.id.description_edit_text);
        mLocationField = findViewById(R.id.location_edit_text);
        mMaxParticipantsField = findViewById(R.id.max_participants_edit_text);
        mSportSpinner = findViewById(R.id.sport_spinner);
        mSubmitButton = findViewById(R.id.button_submit);

        // Создание адаптера для спиннера выбора спорта
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.sports_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSportSpinner.setAdapter(adapter);

        // Установка обработчика нажатия на кнопку отправки
        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitAdvertisement();
            }
        });
    }

    // Метод для отправки объявления
    private void submitAdvertisement() {
        String title = mTitleField.getText().toString().trim();
        String description = mDescriptionField.getText().toString().trim();
        String location = mLocationField.getText().toString().trim();
        String sport = mSportSpinner.getSelectedItem().toString();
        String userid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        int maxParticipants;

        // Проверка наличия значения в поле максимального количества участников
        if (!mMaxParticipantsField.getText().toString().trim().isEmpty()) {
            maxParticipants = Integer.parseInt(mMaxParticipantsField.getText().toString());
        } else {
            Toast.makeText(this, "Пожалуйста, заполните все поля.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Проверка заполненности всех полей
        if (title.isEmpty() || description.isEmpty() || location.isEmpty()) {
            Toast.makeText(this, "Пожалуйста, заполните все поля.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Генерация уникального идентификатора объявления
        String id = mDatabase.child("advertisements").push().getKey();

        // Создание объекта Advertisement
        Advertisement advertisement = new Advertisement(id, title, description, location, sport, userid, 0, maxParticipants);

        // Преобразование объекта Advertisement в Map
        Map<String, Object> advertisementValues = advertisement.toMap();

        // Создание Map с обновлениями для базы данных Firebase
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/advertisements/" + id, advertisementValues);

        // Обновление базы данных Firebase
        mDatabase.updateChildren(childUpdates);

        // Вывод сообщения об успешной отправке объявления
        Toast.makeText(this, "Объявление сохранено.", Toast.LENGTH_SHORT).show();

        // Завершение активности
        finish();
    }
}