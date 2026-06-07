package com.ptithcm.payptithcm.network.models;

import java.util.List;

public class FeesResponse {
    public boolean success;
    public List<FeeData> fees;

    public static class FeeData {
        public int id;
        public String name;
        public long amount;
        public String deadline;
        public String status;
        public String paidDate;
    }
}
