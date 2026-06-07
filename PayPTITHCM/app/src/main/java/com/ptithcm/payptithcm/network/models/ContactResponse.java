package com.ptithcm.payptithcm.network.models;

public class ContactResponse {
    public boolean success;
    public ContactData contact;

    public static class ContactData {
        public String phone;
        public String phoneDisplay;
        public String email;
        public String address;
        public String hours;
        public String fax;
        public String website;
    }
}
