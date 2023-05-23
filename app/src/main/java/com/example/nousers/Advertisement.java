package com.example.nousers;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Advertisement implements Serializable {
    private String id; // Идентификатор объявления
    private String title; // Заголовок объявления
    private String description; // Описание объявления
    private String location; // Местоположение объявления
    private String sport; // Вид спорта
    private String userid; // Идентификатор пользователя, разместившего объявление
    private int currentParticipants; // Текущее количество участников
    private int maxParticipants; // Максимальное количество участников
    private List<String> participants; // Список участников объявления

    public Advertisement() {
        participants = new ArrayList<>();
    }

    // Конструктор объявления
    public Advertisement(String id, String title, String description, String location, String sport, String userid, int currentParticipants, int maxParticipants) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.location = location;
        this.sport = sport;
        this.userid = userid;
        this.currentParticipants = currentParticipants;
        this.maxParticipants = maxParticipants;
        this.participants = new ArrayList<>();
    }

    // Метод получения идентификатора объявления
    public String getId() {
        return id;
    }

    // Метод получения заголовка объявления
    public String getTitle() {
        return title;
    }

    // Метод получения описания объявления
    public String getDescription() {
        return description;
    }

    // Метод получения местоположения объявления
    public String getLocation() {
        return location;
    }

    // Метод получения вида спорта объявления
    public String getSport() {
        return sport;
    }

    // Метод получения идентификатора пользователя
    public String getUserId() {
        return userid;
    }

    // Метод установки идентификатора пользователя
    public void setUserId(String userid) {
        this.userid = userid;
    }

    // Метод получения текущего количества участников
    public int getCurrentParticipants() {
        return currentParticipants;
    }

    // Метод установки текущего количества участников
    public void setCurrentParticipants(int currentParticipants) {
        this.currentParticipants = currentParticipants;
    }

    // Метод получения максимального количества участников
    public int getMaxParticipants() {
        return maxParticipants;
    }

    // Метод установки максимального количества участников
    public void setMaxParticipants(int maxParticipants) {
        this.maxParticipants = maxParticipants;
    }

    // Метод получения списка участников объявления
    public List<String> getParticipants() {
        return participants;
    }

    // Метод преобразования объявления в объект типа Map
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("id", id);
        result.put("title", title);
        result.put("description", description);
        result.put("location", location);
        result.put("sport", sport);
        result.put("userid", userid);
        result.put("currentParticipants", currentParticipants);
        result.put("maxParticipants", maxParticipants);
        return result;
    }
}