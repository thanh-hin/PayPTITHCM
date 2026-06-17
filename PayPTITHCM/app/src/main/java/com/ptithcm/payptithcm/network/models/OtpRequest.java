package com.ptithcm.payptithcm.network.models;

public class OtpRequest {
    public String identifier; // MSSV hoặc email

    public OtpRequest(String identifier) {
        this.identifier = identifier;
    }
}
