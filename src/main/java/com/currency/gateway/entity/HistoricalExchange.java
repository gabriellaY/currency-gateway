package com.currency.gateway.entity;

import java.io.Serializable;
import java.sql.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "HISTORICAL_EXCHANGES")
public class HistoricalExchange implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private long id;

    @ManyToOne
    @JoinColumn(name = "base_currency", referencedColumnName = "id", nullable = false)
    private Currency baseCurrency;

    @ManyToOne
    @JoinColumn(name = "exchange_currency", referencedColumnName = "id", nullable = false)
    private Currency exchangeCurrency;

    @Column(name = "rate", nullable = false)
    private double rate;

    @Column(name = "timestamp", nullable = false)
    private long timestamp;

    @Column(name = "date", nullable = false)
    private Date date;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Currency getBaseCurrency() {
        return baseCurrency;
    }

    public void setBaseCurrency(Currency baseCurrency) {
        this.baseCurrency = baseCurrency;
    }

    public Currency getExchangeCurrency() {
        return exchangeCurrency;
    }

    public void setExchangeCurrency(Currency exchangeCurrency) {
        this.exchangeCurrency = exchangeCurrency;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
