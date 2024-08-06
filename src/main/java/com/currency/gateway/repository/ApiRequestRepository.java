package com.currency.gateway.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.currency.gateway.entity.ApiRequest;

@Repository
public interface ApiRequestRepository extends JpaRepository<ApiRequest, Long> {

}
