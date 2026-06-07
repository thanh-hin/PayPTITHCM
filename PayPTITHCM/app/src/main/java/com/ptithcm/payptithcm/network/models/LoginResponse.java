package com.ptithcm.payptithcm.network.models;

public class LoginResponse {
    public boolean success;
    public String message;
    public String token;
    public StudentData student;

    public static class StudentData {
        public String studentId;
        public String fullName;
        public String email;
        public String phone;
        public String className;
        public String faculty;
    }
}
