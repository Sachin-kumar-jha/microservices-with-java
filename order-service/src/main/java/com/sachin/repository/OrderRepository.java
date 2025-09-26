package com.sachin.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sachin.model.Order;

public interface OrderRepository extends JpaRepository<Order,Long> {

}
