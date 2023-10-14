package com.assessment;

import org.springframework.stereotype.Component;

@Component
public class Deal {
    private int dealId;
    private int dealDuration;

    public Deal() {
    }

    public int getDealId() {
        return dealId;
    }

    public void setDealId(int dealId) {
        this.dealId = dealId;
    }

    public int getDealDuration() {
        return dealDuration;
    }

    public void setDealDuration(int dealDuration) {
        this.dealDuration = dealDuration;
    }

}
