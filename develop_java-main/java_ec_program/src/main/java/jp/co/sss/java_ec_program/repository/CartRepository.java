package jp.co.sss.java_ec_program.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import jp.co.sss.java_ec_program.entity.Carts;
import jp.co.sss.java_ec_program.entity.Users;

public interface CartRepository extends JpaRepository<Carts, Long> {
	List<Carts> findByUser(Users user);
}