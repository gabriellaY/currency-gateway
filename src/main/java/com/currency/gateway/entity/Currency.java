package com.currency.gateway.entity;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "CURRENCIES")
@Data
@NoArgsConstructor
public class Currency implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private long id;

    @Column(name = "symbol", unique = true, nullable = false)
    private String symbol;

    @Column(name = "name", nullable = false)
    private String name;
    
    public Currency(String symbol, String name) {
        this.symbol = symbol;
        this.name = name;
    }
}
