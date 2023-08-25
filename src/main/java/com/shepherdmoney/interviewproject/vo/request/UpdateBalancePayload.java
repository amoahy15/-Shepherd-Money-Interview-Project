package com.shepherdmoney.interviewproject.vo.request;

import java.time.Instant;

import lombok.Data;

@Data
public class UpdateBalancePayload {

    private String creditCardNumber;
    
    private Instant transactionTime;

    private double transactionAmount;

    public CharSequence getDate() {
        return null;
    }

    public Object getAmount() {
        return null;
    }
}
