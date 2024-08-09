package com.currency.gateway.entity;

import java.sql.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "LATEST_EXCHANGE")
@Data
@NoArgsConstructor
public class LatestExchange {
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
    
    public LatestExchange(Currency baseCurrency, Currency exchangeCurrency,
                                         double rate, long timestamp, Date date) {
        this.baseCurrency = baseCurrency;
        this.exchangeCurrency = exchangeCurrency;
        this.rate = rate;
        this.timestamp = timestamp;
        this.date = date;
    }
}
