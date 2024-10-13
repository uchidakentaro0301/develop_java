package jp.co.sss.java_ec_program.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import jp.co.sss.java_ec_program.entity.Order_items;
import jp.co.sss.java_ec_program.entity.Orders;

public interface OrderItemRepository extends JpaRepository<Order_items, Long> {
    List<Order_items> findByOrderOrderId(Long orderId);
    List<Order_items> findByOrder(Orders order);
}