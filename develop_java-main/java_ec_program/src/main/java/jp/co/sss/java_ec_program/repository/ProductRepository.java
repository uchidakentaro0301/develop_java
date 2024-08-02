package jp.co.sss.java_ec_program.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import jp.co.sss.java_ec_program.entity.Products;

public interface ProductRepository extends JpaRepository<Products, Long> {
}