package jp.co.sss.java_ec_program.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Table(name = "java_ec_test_program")
public class Users {
	@Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator ="seq_users_gen")
    @SequenceGenerator(name = "seq_users_gen", sequenceName ="seq_users_gen", allocationSize = 1)
    private Integer user_id;
    
    @Column(name = "user_name")
    private String userName;
    
    @Column(name = "email")
    private Integer email;
    
    @Column(name = "phone")
    private Integer phone;
    
    @Column(name = "passwords")
    private Integer passwords;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

	public Integer getUser_id() {
		return user_id;
	}

	public void setUser_id(Integer user_id) {
		this.user_id = user_id;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public Integer getEmail() {
		return email;
	}

	public void setEmail(Integer email) {
		this.email = email;
	}

	public Integer getPhone() {
		return phone;
	}

	public void setPhone(Integer phone) {
		this.phone = phone;
	}

	public Integer getPasswords() {
		return passwords;
	}

	public void setPasswords(Integer passwords) {
		this.passwords = passwords;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}

}
