package jp.co.sss.java_ec_program.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Table(name = "java_ec_test_program")
public class Products {
	@Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator ="seq_products_gen")
    @SequenceGenerator(name = "seq_products_gen", sequenceName ="seq_products_gen", allocationSize = 1)
    private Integer product_id;
    
    @Column(name = "product_name")
    private String productName;
    
    @Column(name = "price")
    private Integer price;
    
    @Column(name = "stock")
    private Integer stock;
    
    @Column(name = "comment")
    private String comment;
    
    @Column(name = "img_path")
    private Integer imgPath;
    
    @Column(name = "company_id")
    private Integer companyId;
    
    @Column(name = "category_id")
    private Integer categoryId;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

	public Integer getProduct_id() {
		return product_id;
	}

	public void setProduct_id(Integer product_id) {
		this.product_id = product_id;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public Integer getPrice() {
		return price;
	}

	public void setPrice(Integer price) {
		this.price = price;
	}

	public Integer getStock() {
		return stock;
	}

	public void setStock(Integer stock) {
		this.stock = stock;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public Integer getImgPath() {
		return imgPath;
	}

	public void setImgPath(Integer imgPath) {
		this.imgPath = imgPath;
	}

	public Integer getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Integer companyId) {
		this.companyId = companyId;
	}

	public Integer getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(Integer categoryId) {
		this.categoryId = categoryId;
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
