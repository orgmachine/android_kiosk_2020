package com.ehealthkiosk.kiosk.model;

public class UserItem {

    public String name;
    public int drawable;
    public String gender;
    public String age;

    public UserItem(String name, int drawable, String gender, String age) {
        this.name = name;
        this.drawable = drawable;
        this.gender = gender;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDrawable() {
        return drawable;
    }

    public void setDrawable(int drawable) {
        this.drawable = drawable;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }
}
