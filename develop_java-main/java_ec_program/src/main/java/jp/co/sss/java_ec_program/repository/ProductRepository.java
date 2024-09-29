package jp.co.sss.java_ec_program.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jp.co.sss.java_ec_program.entity.Products;

public interface ProductRepository extends JpaRepository<Products, Long> {

	@Query("SELECT p FROM Products p WHERE p.productName LIKE %:keyword%")
    List<Products> searchByKeyword(@Param("keyword") String keyword);
    
    List<Products> findByCategoryId(Integer categoryId);
}