package com.learning.learningSpring.model;

import lombok.Data;

@Data
public class RegistrationForm {
    private String username;
    private String password;
    private String repeatPassword;

    public boolean isValid() {
        return password.equals(repeatPassword);
    }
}