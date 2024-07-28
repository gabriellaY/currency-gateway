package com.currency.gateway.entity;

import java.io.Serializable;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "API_REQUESTS")
public class ApiRequest implements Serializable {

    @Id
    @Column(name = "request_id", unique = true, nullable = false)
    private UUID id;

    @Column(name = "end_user_id", nullable = false)
    private String endUserID;

    @ManyToOne
    @JoinColumn(name = "currency_id", referencedColumnName = "id", nullable = false)
    private Currency currency;

    @Column(name = "period", nullable = false)
    private int period;

    @Column(name = "timestamp", nullable = false)
    private long timestamp;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getEndUserID() {
        return endUserID;
    }

    public void setEndUserID(String endUserID) {
        this.endUserID = endUserID;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public int getPeriod() {
        return period;
    }

    public void setPeriod(int period) {
        this.period = period;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
