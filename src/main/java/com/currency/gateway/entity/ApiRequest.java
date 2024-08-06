package com.currency.gateway.entity;

import java.io.Serializable;
import java.util.UUID;

import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "API_REQUESTS")
@Getter
@Setter
@NoArgsConstructor
public class ApiRequest implements Serializable {

    @Id
    @Column(name = "request_id", unique = true, nullable = false)
    private UUID id;

    @Column(name = "end_user_id", nullable = false)
    private String endUserID;

    @ManyToOne
    @JoinColumn(name = "currency_id", referencedColumnName = "id", nullable = false)
    private Currency currency;

    @Column(name = "period")
    private int period;

    @Column(name = "timestamp", nullable = false)
    private long timestamp;
    
    public ApiRequest(UUID id, String endUserID, Currency currency, Integer period, long timestamp) {
        this.id = id;
        this.endUserID = endUserID;
        this.currency = currency;
        this.period = period;
        this.timestamp = timestamp;
    }
}
