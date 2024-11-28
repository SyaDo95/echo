package com.echoproject.echo.dto;

public class ChatbotConcept {
    private String job;
    private String age;
    private String hobby;
    private String favoriteFood;
    private String favoriteColor;
    private String name; // 이름 필드 추가

    // 기본 생성자, getter, setter 추가
    public ChatbotConcept() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getHobby() {
        return hobby;
    }

    public void setHobby(String hobby) {
        this.hobby = hobby;
    }

    public String getFavoriteFood() {
        return favoriteFood;
    }

    public void setFavoriteFood(String favoriteFood) {
        this.favoriteFood = favoriteFood;
    }

    public String getFavoriteColor() {
        return favoriteColor;
    }

    public void setFavoriteColor(String favoriteColor) {
        this.favoriteColor = favoriteColor;
    }
    @Override
    public String toString() {
        return String.format(
                "ChatbotConcept{name='%s', job='%s', age='%s', hobby='%s', favoriteFood='%s', favoriteColor='%s'}",
                name, job, age, hobby, favoriteFood, favoriteColor
        );
    }
}

