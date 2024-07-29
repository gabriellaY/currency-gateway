package com.currency.gateway.entity;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "STATISTICS")
@Getter
@Setter
public class Statistic implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private long id;

    @ManyToOne
    @JoinColumn(name = "service_id", referencedColumnName = "id", nullable = false)
    private Service serviceId;

    @ManyToOne
    @JoinColumn(name = "request_id", referencedColumnName = "request_id", nullable = false)
    private ApiRequest requestId;

    @Column(name = "timestamps", nullable = false)
    private long timestamp;

    @Column(name = "end_user_id", nullable = false)
    private String end_user_id;
}
