package com.ptithcm.payptithcm.models;

import java.io.Serializable;

public class Student implements Serializable {
    private String studentId;
    private String fullName;
    private String email;
    private String phone;
    private String className;
    private String faculty;

    public Student() {}

    public Student(String studentId, String fullName, String email, String phone,
                   String className, String faculty) {
        this.studentId = studentId;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.className = className;
        this.faculty = faculty;
    }

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }
    public String getFaculty() { return faculty; }
    public void setFaculty(String faculty) { this.faculty = faculty; }
}
