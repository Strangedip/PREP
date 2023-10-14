package com.assessment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Partner {
    private int id;
    private String name;
    private long netWorth;
    @Autowired
    private Deal deal;

    public Deal getDeal() {
        return deal;
    }

    public void setDeal(Deal deal) {
        this.deal = deal;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getNetWorth() {
        return netWorth;
    }

    public void setNetWorth(long netWorth) {
        this.netWorth = netWorth;
    }

    @Override
    public String toString() {
        return "Partner [id=" + id + ", name=" + name + ", netWorth=" + netWorth + ", deal=" + deal + "]";
    }
}
