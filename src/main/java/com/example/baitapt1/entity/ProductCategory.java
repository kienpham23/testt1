package com.example.baitapt1.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;


@Entity
@Table(name="product_category",
        uniqueConstraints = @UniqueConstraint(columnNames = {"product_id", "category_id"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreationTimestamp
    @Column(name= "create_date", updatable= false)
    private LocalDateTime createDate;

    @UpdateTimestamp
    private LocalDateTime modifiedDate;
    @Column(name = "created_by", length = 100)
    private String createdBy;

    private String modifiedBy;
    @ManyToOne

    @JoinColumn(name= "product_id")
    private Product product;

    @ManyToOne
    @JoinColumn(name ="category_id")
    private Category category;

    public Long getId() {
        return id;
    }

    public LocalDateTime getCreateDate() {
        return createDate;
    }

    public LocalDateTime getModifiedDate() {
        return modifiedDate;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }

    public Product getProduct() {
        return product;
    }

    public Category getCategory() {
        return category;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setCreateDate(LocalDateTime createDate) {
        this.createDate = createDate;
    }

    public void setModifiedDate(LocalDateTime modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public void setCategory(Category category) {
        this.category = category;
    }
}
