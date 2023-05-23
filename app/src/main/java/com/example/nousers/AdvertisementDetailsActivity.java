package com.example.nousers;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class AdvertisementDetailsActivity extends AppCompatActivity {
    private Advertisement mAdvertisement;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advertisement_details);

        // Получение объявления из предыдущей активности
        Intent intent = getIntent();
        mAdvertisement = (Advertisement) intent.getSerializableExtra("advertisement");

        // Настройка текстовых полей с информацией об объявлении
        TextView titleTextView = findViewById(R.id.advertisement_title);
        TextView descriptionTextView = findViewById(R.id.advertisement_description);
        TextView locationTextView = findViewById(R.id.advertisement_location);
        TextView maxParticipantsTextView = findViewById(R.id.advertisement_max_participants);
        TextView currentParticipantsTextView = findViewById(R.id.advertisement_current_participants);
        titleTextView.setText(mAdvertisement.getTitle());
        descriptionTextView.setText(mAdvertisement.getDescription());
        locationTextView.setText("Расположение: " + mAdvertisement.getLocation());
        maxParticipantsTextView.setText("Максимальное количество участников: " + mAdvertisement.getMaxParticipants());
        currentParticipantsTextView.setText("Нынешнее количество участников: " + mAdvertisement.getCurrentParticipants());

        // Проверка, является ли текущий пользователь создателем объявления
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        if (mAdvertisement.getUserId().equals(uid)) {
            // Если пользователь создатель, показать кнопку удаления объявления
            Button deleteButton = findViewById(R.id.delete_button);
            deleteButton.setVisibility(View.VISIBLE);
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteAdvertisement();
                }
            });
        }

        // Обработка нажатия кнопки "Присоединиться" или "Выйти из группы"
        Button joinButton = findViewById(R.id.join_button);
        joinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isUserJoined()) {
                    leaveGroup();
                } else {
                    joinGroup();
                }
            }
        });

        updateJoinButton();
    }

    // Проверка, присоединен ли текущий пользователь к группе объявления
    private boolean isUserJoined() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        List<String> participants = mAdvertisement.getParticipants();
        return participants.contains(uid);
    }

    // Присоединение к группе объявления
    private void joinGroup() {
        if (isUserJoined()) {
            Toast.makeText(AdvertisementDetailsActivity.this, "Вы уже присоединились к группе", Toast.LENGTH_SHORT).show();
            return;
        }
        if (mAdvertisement.getCurrentParticipants() >= mAdvertisement.getMaxParticipants()) {
            Toast.makeText(AdvertisementDetailsActivity.this, "Достигнуто максимальное количество участников", Toast.LENGTH_SHORT).show();
            return;
        }
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference adsRef = FirebaseDatabase.getInstance().getReference("advertisements");
        mAdvertisement.getParticipants().add(uid);
        mAdvertisement.setCurrentParticipants(mAdvertisement.getCurrentParticipants() + 1);
        adsRef.child(mAdvertisement.getId()).setValue(mAdvertisement)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(AdvertisementDetailsActivity.this, "Вы присоединились к группе", Toast.LENGTH_SHORT).show();
                        updateJoinButton();
                        updateCurrentParticipantsTextView();
                        restartMenuActivity();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AdvertisementDetailsActivity.this, "Ошибка при присоединении к группе", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Покинуть группу объявления
    private void leaveGroup() {
        if (!isUserJoined()) {
            Toast.makeText(AdvertisementDetailsActivity.this, "Вы не присоединены к группе", Toast.LENGTH_SHORT).show();
            return;
        }
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference adsRef = FirebaseDatabase.getInstance().getReference("advertisements");
        mAdvertisement.getParticipants().remove(uid);
        mAdvertisement.setCurrentParticipants(mAdvertisement.getCurrentParticipants() - 1);
        adsRef.child(mAdvertisement.getId()).setValue(mAdvertisement)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(AdvertisementDetailsActivity.this, "Вы покинули группу", Toast.LENGTH_SHORT).show();
                        updateJoinButton();
                        updateCurrentParticipantsTextView();
                        restartMenuActivity();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AdvertisementDetailsActivity.this, "Ошибка при выходе из группы", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Удаление объявления
    private void deleteAdvertisement() {
        DatabaseReference adsRef = FirebaseDatabase.getInstance().getReference("advertisements");
        adsRef.child(mAdvertisement.getId()).removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(AdvertisementDetailsActivity.this, "Объявление удалено", Toast.LENGTH_SHORT).show();
                        restartMenuActivity();
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AdvertisementDetailsActivity.this, "Ошибка при удалении объявления", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Обновление вида кнопки "Присоединиться" или "Выйти из группы"
    private void updateJoinButton() {
        Button joinButton = findViewById(R.id.join_button);
        if (isUserJoined()) {
            joinButton.setText("Выйти из группы");
            joinButton.setEnabled(true);
        } else {
            joinButton.setText("Вступить в группу");
            if (mAdvertisement.getCurrentParticipants() >= mAdvertisement.getMaxParticipants()) {
                joinButton.setEnabled(false);
            } else {
                joinButton.setEnabled(true);
            }
        }
    }

    // Обновление текстового поля с количеством текущих участников
    private void updateCurrentParticipantsTextView() {
        TextView currentParticipantsTextView = findViewById(R.id.advertisement_current_participants);
        currentParticipantsTextView.setText("Нынешнее количество участников: " + mAdvertisement.getCurrentParticipants());
    }

    // Перезапуск активности меню
    private void restartMenuActivity() {
        Intent intent = new Intent(this, MenuActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
