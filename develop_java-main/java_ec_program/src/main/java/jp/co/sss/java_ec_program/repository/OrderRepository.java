package jp.co.sss.java_ec_program.repository;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import jp.co.sss.java_ec_program.entity.Orders;
import jp.co.sss.java_ec_program.entity.Users;

public interface OrderRepository extends JpaRepository<Orders, Long> {
	Optional<Orders> findTopByUserOrderByCreatedAtDesc(Users user);
}