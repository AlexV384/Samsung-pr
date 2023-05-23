package com.example.nousers;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private EditText editTextName, editTextEmail, editTextPassword;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registr);

        // Получение ссылок на элементы пользовательского интерфейса
        editTextName = findViewById(R.id.edit_text_name);
        editTextEmail = findViewById(R.id.edit_text_email);
        editTextPassword = findViewById(R.id.edit_text_password);

        // Получение экземпляра FirebaseAuth и DatabaseReference
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("users");

        // Установка обработчиков клика на кнопки
        Button buttonRegister = findViewById(R.id.button_register);
        buttonRegister.setOnClickListener(view -> registerUser());

        Button buttonLogin = findViewById(R.id.button_login);
        buttonLogin.setOnClickListener(view -> loginUser());
    }

    // Метод регистрации нового пользователя
    private void registerUser() {
        final String name = editTextName.getText().toString().trim();
        final String email = editTextEmail.getText().toString().trim();
        final String password = editTextPassword.getText().toString().trim();

        // Проверка на заполненность полей
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "Пожалуйста, введите ваше имя", Toast.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Пожалуйста, введите вашу почту", Toast.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Пожалуйста, введите ваш пароль", Toast.LENGTH_LONG).show();
            return;
        }

        // Регистрация нового пользователя с использованием FirebaseAuth
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        assert user != null;
                        String userId = user.getUid();
                        String userEmail = user.getEmail();
                        String userName = user.getDisplayName();
                        String userPhotoUrl = user.getPhotoUrl() != null ? user.getPhotoUrl().toString() : null;

                        // Создание нового пользователя в базе данных Firebase
                        Map<String, Object> newUser = new HashMap<>();
                        newUser.put("userId", userId);
                        newUser.put("userEmail", userEmail);
                        newUser.put("userName", userName);
                        newUser.put("userPhotoUrl", userPhotoUrl);
                        mDatabase.child(userId).setValue(newUser);

                        // Переход к следующей активности
                        startActivity(new Intent(MainActivity.this, MenuActivity.class));
                        finish();
                    } else {
                        Toast.makeText(MainActivity.this, "Ошибка регистрации", Toast.LENGTH_LONG).show();
                    }
                });
    }

    // Метод входа пользователя
    private void loginUser() {
        final String email = editTextEmail.getText().toString().trim();
        final String password = editTextPassword.getText().toString().trim();

        // Проверка на заполненность полей
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Пожалуйста, введите вашу почту", Toast.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Пожалуйста, введите ваш пароль", Toast.LENGTH_LONG).show();
            return;
        }

        // Вход пользователя с использованием FirebaseAuth
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Переход к следующей активности при успешном входе
                        startActivity(new Intent(MainActivity.this, MenuActivity.class));
                        finish();
                    } else {
                        Toast.makeText(MainActivity.this, "Ошибка входа", Toast.LENGTH_LONG).show();
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            // Переход к следующей активности, если пользователь уже вошел в систему
            startActivity(new Intent(MainActivity.this, MenuActivity.class));
            finish();
        }
    }
}
