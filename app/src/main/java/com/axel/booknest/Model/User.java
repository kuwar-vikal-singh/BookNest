package com.axel.booknest.Model;

public class User {
    private String name;
    private String email;
    private String password; // You might not need to store password in Firebase for security reasons
    private boolean premium;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String name, String email, String password, boolean premium) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.premium = premium;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isPremium() {
        return premium;
    }

    public void setPremium(boolean premium) {
        this.premium = premium;
    }
}
