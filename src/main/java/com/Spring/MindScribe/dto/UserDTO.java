package com.Spring.MindScribe.dto;

public class UserDTO {

    private String email;
    private String password;
    private String name;
    private String confirmPassword;

    public UserDTO() {}

    public UserDTO(String email, String password, String name, String confirmPassword) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.confirmPassword = confirmPassword;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}
