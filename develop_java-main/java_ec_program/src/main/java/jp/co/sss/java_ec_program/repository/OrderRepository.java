package jp.co.sss.java_ec_program.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import jp.co.sss.java_ec_program.entity.Orders;

public interface OrderRepository extends JpaRepository<Orders, Long> {
}