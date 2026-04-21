package com.ptithcm.payptithcm.network.models;

public class LoginRequest {
    public String identifier; // MSSV hoặc email
    public String password;
    public String otp;

    public LoginRequest(String identifier, String password, String otp) {
        this.identifier = identifier;
        this.password = password;
        this.otp = otp;
    }
}
