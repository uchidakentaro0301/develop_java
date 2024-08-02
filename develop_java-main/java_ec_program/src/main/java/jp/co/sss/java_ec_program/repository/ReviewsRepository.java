package jp.co.sss.java_ec_program.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import jp.co.sss.java_ec_program.entity.Reviews;

public interface ReviewsRepository extends JpaRepository<Reviews, Integer> {
    List<Reviews> findByProductId(Integer productId);
}