package com.example.adutucartrider.models;

public class Rider {

    private String name,email,phone,password,pending;

    public Rider(){}

    public Rider(String name,String email,String phone,String password,String pending){
        this.name = name;
        this.email = email;
        this.phone =phone;
        this.password = password;
        this.pending = pending;
    }

    public String getPending() {
        return pending;
    }

    public void setPending(String pending) {
        this.pending = pending;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

}
