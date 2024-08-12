package com.currency.gateway.entity;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "API_REQUESTS_STATISTICS")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiRequest implements Serializable {

    @Id
    @Column(name = "request_id", unique = true, nullable = false)
    private String id;

    @ManyToOne
    @JoinColumn(name = "service_id", referencedColumnName = "id", nullable = false)
    private Service service;

    @Column(name = "end_user_id", nullable = false)
    private String endUserID;

    @Column(name = "timestamp", nullable = false)
    private long timestamp;
}
