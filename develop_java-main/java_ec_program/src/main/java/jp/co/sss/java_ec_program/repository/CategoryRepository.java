package jp.co.sss.java_ec_program.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import jp.co.sss.java_ec_program.entity.Categories;

public interface CategoryRepository extends JpaRepository<Categories, Long> {
}